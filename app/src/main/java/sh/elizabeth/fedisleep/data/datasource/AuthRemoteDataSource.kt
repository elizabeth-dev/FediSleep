package sh.elizabeth.fedisleep.data.datasource

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedisleep.api.iceshrimp.AuthIceshrimpApi
import sh.elizabeth.fedisleep.api.iceshrimp.model.toDomain
import sh.elizabeth.fedisleep.model.Profile
import sh.elizabeth.fedisleep.util.SupportedInstances
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authIceshrimpApi: AuthIceshrimpApi,
    private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {

    suspend fun prepareOAuth(
        instance: String,
        instanceType: SupportedInstances,
    ): String {
        val instanceData = internalDataLocalDataSource.internalData.first().instances[instance]
            ?: throw IllegalStateException("Instance data is missing")

        when (instanceType) {
            SupportedInstances.ICESHRIMP -> {
                val appSecret = instanceData.appSecret.takeUnless { it.isNullOrBlank() }
                    ?: authIceshrimpApi.createApp(endpoint = instanceData.delegatedEndpoint).secret.also { appSecret ->
                        internalDataLocalDataSource.setInstance(
                            instance = instance,
                            newDelegatedEndpoint = null,
                            newInstanceType = null,
                            newAppSecret = appSecret,
                            newAppId = null
                        )
                    }
                val session = authIceshrimpApi.generateSession(
                    endpoint = instanceData.delegatedEndpoint,
                    appSecret = appSecret,
                )

                return session.url
            }
        }
    }

    suspend fun finishOAuth(
        instance: String,
        instanceType: SupportedInstances,
        token: String,
    ): Pair<String, Profile> {
        val instanceData = internalDataLocalDataSource.internalData.first().instances[instance]
            ?: throw IllegalStateException("Instance data is missing")

        when (instanceType) {
            SupportedInstances.ICESHRIMP -> {
                val appSecret = instanceData.appSecret
                    ?: throw IllegalStateException("App secret for $instance not found")

                val userKey = authIceshrimpApi.getUserKey(
                    endpoint = instanceData.delegatedEndpoint, appSecret = appSecret, token = token
                )

                return userKey.accessToken to userKey.user.toDomain(instance)
            }
        }
    }
}
