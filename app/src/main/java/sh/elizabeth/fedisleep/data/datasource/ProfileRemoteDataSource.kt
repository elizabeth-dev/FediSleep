package sh.elizabeth.fedisleep.data.datasource

import sh.elizabeth.fedisleep.api.iceshrimp.ProfileIceshrimpApi
import sh.elizabeth.fedisleep.util.SupportedInstances
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val profileIceshrimpApi: ProfileIceshrimpApi,
) {
    suspend fun updateSelf(
        endpoint: String,
        instanceType: SupportedInstances,
        token: String,
        name: String,
    ) {
        when (instanceType) {
            SupportedInstances.ICESHRIMP -> profileIceshrimpApi.updateSelf(
                endpoint = endpoint, token = token, name = name
            )
        }
    }
}
