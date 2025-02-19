package sh.elizabeth.fedisleep.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedisleep.data.datasource.AuthRemoteDataSource
import sh.elizabeth.fedisleep.data.datasource.InternalDataRemoteDataSource
import sh.elizabeth.fedisleep.data.datasource.MetaRemoteDataSource
import sh.elizabeth.fedisleep.model.Profile
import javax.inject.Inject
import kotlin.text.get

class AuthRepository @Inject constructor(
	private val authRemoteDataSource: AuthRemoteDataSource,
	private val metaRemoteDataSource: MetaRemoteDataSource,
    private val internalDataRemoteDataSource: InternalDataRemoteDataSource
) {
    val internalData = internalDataLocalDataSource.internalData

    val activeAccount =
        internalDataLocalDataSource.internalData.map { it.activeAccount }

    val loggedInAccounts =
        internalDataLocalDataSource.internalData.map { it.accountTokens.keys }

    suspend fun prepareOAuth(instance: String): String {
        internalDataRemoteDataSource.sendOAuthInstance(instance)

        val (delegatedEndpoint, instanceType) =
            metaRemoteDataSource.getInstanceData(instance)
                ?: throw IllegalArgumentException("Instance type not supported")

        val (appSecret, token) = authRemoteDataSource.prepareOAuth(instance, instanceType)

        internalDataRemoteDataSource.sendInstance(
            instance = instance,
            endpoint = delegatedEndpoint,
            instanceType = instanceType,
            appId = null,
            appSecret = appSecret
        )
    }

    suspend fun finishOAuth(token: String): Profile {
        val settingsData = internalDataLocalDataSource.internalData.first()
        val instance =
            if (settingsData.lastLoginInstance != "") settingsData.lastLoginInstance else throw IllegalStateException(
                "Last login instance not found"
            )

        val instanceType =
            settingsData.instances[instance]?.instanceType
                ?: throw IllegalStateException("Instance type not found")

        val (accessToken, profile) = authRemoteDataSource.finishOAuth(
            instance, instanceType, token
        )

        internalDataLocalDataSource.setAccessToken(profile.id, accessToken)
        internalDataLocalDataSource.setActiveAccount(profile.id)

        return profile
    }

    suspend fun setActiveAccount(profileId: String) {
        internalDataLocalDataSource.setActiveAccount(profileId)
    }
}
