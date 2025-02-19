package sh.elizabeth.fedisleep.data.datasource

import sh.elizabeth.fedisleep.api.iceshrimp.AuthIceshrimpApi
import sh.elizabeth.fedisleep.api.iceshrimp.model.toDomain
import sh.elizabeth.fedisleep.common.SupportedInstances
import sh.elizabeth.fedisleep.model.Profile
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
	private val authIceshrimpApi: AuthIceshrimpApi,
) {

    suspend fun prepareOAuth(
        endpoint: String,
        instanceType: SupportedInstances,
        appSecret: String?,
    ): Pair<String, String> {
        when (instanceType) {
            SupportedInstances.ICESHRIMP -> {
                val _appSecret = appSecret ?: authIceshrimpApi.createApp(endpoint = endpoint).secret

                val session = authIceshrimpApi.generateSession(
                    endpoint = endpoint,
                    appSecret = _appSecret,
                )

                return Pair(_appSecret, session.token)
            }
        }
    }

    suspend fun finishOAuth(
        instance: String,
        endpoint: String,
        instanceType: SupportedInstances,
        token: String,
        appSecret: String,
    ): Pair<String, Profile> {
        when (instanceType) {
            SupportedInstances.ICESHRIMP -> {
                val userKey = authIceshrimpApi.getUserKey(
                    endpoint = endpoint, appSecret = appSecret, token = token
                )

                return userKey.accessToken to userKey.user.toDomain(instance)
            }
        }
    }
}
