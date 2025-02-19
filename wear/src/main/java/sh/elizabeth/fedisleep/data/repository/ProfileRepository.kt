package sh.elizabeth.fedisleep.data.repository

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedisleep.common.InstanceEndpointTypeToken
import sh.elizabeth.fedisleep.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedisleep.data.datasource.ProfileRemoteDataSource
import javax.inject.Inject
import kotlin.text.get

class ProfileRepository @Inject constructor(
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
	private val profileRemoteDataSource: ProfileRemoteDataSource
) {
    private suspend fun getInstanceAndEndpointAndTypeAndToken(activeAccount: String): InstanceEndpointTypeToken =
        activeAccount.let {
            val internalData = internalDataLocalDataSource.internalData.first()
            val instance = it.split('@')[1]
            InstanceEndpointTypeToken(
                instance,
                internalData.instances[instance]?.delegatedEndpoint!!,
                internalData.instances[instance]?.instanceType!!,
                internalData.accountTokens[it]!!
            )
        }

    suspend fun handleUserActivityState(isAsleep: Boolean) {
        val internalData = internalDataLocalDataSource.internalData.first()

        for (account in internalData.accountTokens.keys) {
            val instanceData = getInstanceAndEndpointAndTypeAndToken(account)
            val names = internalData.accountSettings[account]

            if (names == null) {
                continue
            }

            profileRemoteDataSource.updateSelf(
                instanceData.endpoint,
                instanceData.instanceType,
                instanceData.token,
                if (isAsleep) names.asleepName else names.awakeName
            )
        }
    }
}
