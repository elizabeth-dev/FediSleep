package sh.elizabeth.fedisleep

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sh.elizabeth.fedisleep.common.ACCOUNT_ITEM
import sh.elizabeth.fedisleep.common.APP_ID_ITEM
import sh.elizabeth.fedisleep.common.APP_SECRET_ITEM
import sh.elizabeth.fedisleep.common.ASLEEP_NAME_ITEM
import sh.elizabeth.fedisleep.common.AWAKE_NAME_ITEM
import sh.elizabeth.fedisleep.common.ENDPOINT_ITEM
import sh.elizabeth.fedisleep.common.INSTANCE_ITEM
import sh.elizabeth.fedisleep.common.INSTANCE_PATH
import sh.elizabeth.fedisleep.common.NAMES_PATH
import sh.elizabeth.fedisleep.common.OAUTH_INSTANCE_PATH
import sh.elizabeth.fedisleep.common.SupportedInstances
import sh.elizabeth.fedisleep.common.TOKEN_PATH
import sh.elizabeth.fedisleep.common.TYPE_ITEM
import sh.elizabeth.fedisleep.data.repository.InternalDataRepository
import javax.inject.Inject

@HiltWorker
class UpdateNamesWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted private val workerParams: WorkerParameters,
	private val internalDataRepository: InternalDataRepository,
) : CoroutineWorker(appContext, workerParams) {
	override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
		try {
			// TODO: do only if settings allow
			val account =
				workerParams.inputData.getString("account")
					?: return@withContext Result.failure()
			val asleepName =
				workerParams.inputData.getString("asleepName")
					?: return@withContext Result.failure()
			val awakeName =
				workerParams.inputData.getString("awakeName")
					?: return@withContext Result.failure()

			internalDataRepository.updateNames(
				account = account,
				asleepName = asleepName,
				awakeName = awakeName
			)
			return@withContext Result.success()
		} catch (e: Exception) {
			return@withContext Result.failure()
		}
	}

}

class DataLayerListenerService @Inject constructor(private val internalDataRepository: InternalDataRepository) :
	WearableListenerService() {

	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	override fun onDataChanged(dataEvents: DataEventBuffer) {
		super.onDataChanged(dataEvents)

		dataEvents.forEach { dataEvent ->
			val uri = dataEvent.dataItem.uri
			scope.launch {
				when (uri.path) {
					NAMES_PATH -> {
						DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.apply {
							val account = getString(ACCOUNT_ITEM) ?: return@apply
							val asleepName = getString(ASLEEP_NAME_ITEM) ?: return@apply
							val awakeName = getString(AWAKE_NAME_ITEM) ?: return@apply

							internalDataRepository.updateNames(
								account = account,
								asleepName = asleepName,
								awakeName = awakeName
							)
						}

					}

					INSTANCE_PATH -> {
						DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.apply {
							val instance = getString(INSTANCE_ITEM) ?: return@apply
							val endpoint = getString(ENDPOINT_ITEM) ?: return@apply
							val type = getString(TYPE_ITEM) ?: return@apply
							val appId = getString(APP_ID_ITEM) ?: return@apply
							val appSecret = getString(APP_SECRET_ITEM) ?: return@apply

							internalDataRepository.updateInstanceData(
								instance = instance,
								endpoint = endpoint,
								instanceType = SupportedInstances.valueOf(type),
								appId = appId,
								appSecret = appSecret
							)
						}
					}

					TOKEN_PATH -> {
						DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.apply {
							val account = getString(ACCOUNT_ITEM) ?: return@apply
							val token = getString(TOKEN_PATH) ?: return@apply

							internalDataRepository.updateToken(
								account = account,
								token = token
							)
						}
					}

					OAUTH_INSTANCE_PATH -> {
						DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.apply {
							val instance = getString(INSTANCE_ITEM) ?: return@apply

							internalDataRepository.updateOAuthInstance(instance)
						}
					}
				}
			}
		}
	}

	// When the message to start the Wearable app is received, this method starts the Wearable app.
	// Alternative to this implementation, Horologist offers a DataHelper API which allows to
	// start the main activity or a different activity of your choice from the Wearable app
	// see https://google.github.io/horologist/datalayer-helpers-guide/#launching-a-specific-activity-on-the-other-device
	// for details
	override fun onMessageReceived(messageEvent: MessageEvent) {
		super.onMessageReceived(messageEvent)

		when (messageEvent.path) {
			START_ACTIVITY_PATH -> {
				startActivity(
					Intent(this, MainActivity::class.java)
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				)
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		scope.cancel()
	}

	companion object {
		private const val TAG = "DataLayerService"

		private const val START_ACTIVITY_PATH = "/start-activity"
		private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"

	}
}
