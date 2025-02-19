package sh.elizabeth.fedisleep.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedisleep.data.datasource.AuthRemoteDataSource
import sh.elizabeth.fedisleep.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedisleep.data.datasource.MetaRemoteDataSource
import sh.elizabeth.fedisleep.model.Profile
import javax.inject.Inject

class AuthRepository @Inject constructor(
	private val authRemoteDataSource: AuthRemoteDataSource,
	private val metaRemoteDataSource: MetaRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	val internalData = internalDataLocalDataSource.internalData

	val activeAccount =
		internalDataLocalDataSource.internalData.map { it.activeAccount }

	val loggedInAccounts =
		internalDataLocalDataSource.internalData.map { it.accountTokens.keys }

	suspend fun prepareOAuth(instance: String): String {
		internalDataLocalDataSource.setLastLoginInstance(instance)

		val (delegatedEndpoint, instanceType) =
			metaRemoteDataSource.getInstanceData(instance)
				?: throw IllegalArgumentException("Instance type not supported")

		internalDataLocalDataSource.setInstance(
			instance = instance,
			newDelegatedEndpoint = delegatedEndpoint,
			newInstanceType = instanceType,
			newAppId = null,
			newAppSecret = null
		)

		return authRemoteDataSource.prepareOAuth(instance, instanceType)
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
