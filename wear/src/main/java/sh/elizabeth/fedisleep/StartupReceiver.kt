package sh.elizabeth.fedisleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.health.services.client.HealthServices
import androidx.health.services.client.setPassiveListenerService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import sh.elizabeth.fedisleep.util.passiveListenerConfig

class StartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<RegisterForPassiveDataWorker>().build()
        )
    }
}

class RegisterForPassiveDataWorker(
    private val appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        runBlocking {
            HealthServices.getClient(appContext).passiveMonitoringClient.setPassiveListenerService(
                PassiveDataService::class.java, passiveListenerConfig
            )
        }
        return Result.success()
    }
}
