package sh.elizabeth.fedisleep.data.repository

import sh.elizabeth.fedisleep.data.datasource.InternalDataLocalDataSource
import javax.inject.Inject

class InternalDataRepository @Inject constructor(private val internalDataLocalDataSource: InternalDataLocalDataSource) {
    suspend fun setNames(activeAccount: String, asleepName: String, awakeName: String) {
        internalDataLocalDataSource.setNames(
            accountIdentifier = activeAccount,
            newAsleepName = asleepName,
            newAwakeName = awakeName
        )
    }
}