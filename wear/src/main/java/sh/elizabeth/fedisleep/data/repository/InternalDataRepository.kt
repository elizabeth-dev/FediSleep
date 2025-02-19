package sh.elizabeth.fedisleep.data.repository

import sh.elizabeth.fedisleep.common.SupportedInstances
import sh.elizabeth.fedisleep.data.datasource.InternalDataLocalDataSource
import javax.inject.Inject

class InternalDataRepository @Inject constructor(private val internalDataLocalDataSource: InternalDataLocalDataSource) {
	suspend fun updateNames(account: String, asleepName: String, awakeName: String) {
		internalDataLocalDataSource.setNames(account, asleepName, awakeName)
	}

	suspend fun updateInstanceData(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		appId: String,
		appSecret: String,
	) {
		internalDataLocalDataSource.setInstance(instance, endpoint, instanceType, appId, appSecret)
	}

	suspend fun updateToken(account: String, token: String) {
		internalDataLocalDataSource.setAccessToken(account, token)
	}

	suspend fun updateOAuthInstance(instance: String) {
		internalDataLocalDataSource.setLastLoginInstance(instance)
	}
}
