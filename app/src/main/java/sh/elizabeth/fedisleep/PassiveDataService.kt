package sh.elizabeth.fedisleep

import android.content.Context
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.UserActivityInfo
import androidx.health.services.client.data.UserActivityState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.elizabeth.fedisleep.data.repository.ProfileRepository

@HiltWorker
class HandleActivityStateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val profileRepository: ProfileRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val isAsleep = workerParams.inputData.getBoolean("asleep", false)
            profileRepository.handleUserActivityState(isAsleep)
            return@withContext Result.success()
        } catch (e: Exception) {
            return@withContext Result.failure()
        }
    }

}

class PassiveDataService : PassiveListenerService() {
    override fun onUserActivityInfoReceived(info: UserActivityInfo) {
        super.onUserActivityInfoReceived(info)

        WorkManager.getInstance(this).beginUniqueWork(
            "handleActivityState",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.Builder(HandleActivityStateWorker::class.java).setInputData(
                Data.Builder().putBoolean(
                    "asleep", info.userActivityState == UserActivityState.USER_ACTIVITY_ASLEEP
                ).build()
            ).build()
        ).enqueue()
    }
}