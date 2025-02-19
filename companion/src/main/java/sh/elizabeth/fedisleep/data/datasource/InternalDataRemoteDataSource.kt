package sh.elizabeth.fedisleep.data.datasource

import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
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
import sh.elizabeth.fedisleep.common.TOKEN_ITEM
import sh.elizabeth.fedisleep.common.TOKEN_PATH
import sh.elizabeth.fedisleep.common.TYPE_ITEM
import javax.inject.Inject

class InternalDataRemoteDataSource @Inject constructor(private val dataClient: DataClient) {
	fun sendInstance(
		instance: String,
		instanceType: SupportedInstances,
		endpoint: String,
		appId: String?,
		appSecret: String,
	) {
		val putDataReq = PutDataMapRequest.create(INSTANCE_PATH).run {
			dataMap.putString(INSTANCE_ITEM, instance)
			dataMap.putString(TYPE_ITEM, instanceType.name)
			dataMap.putString(ENDPOINT_ITEM, endpoint)
			if (appId != null) dataMap.putString(APP_ID_ITEM, appId)
			dataMap.putString(APP_SECRET_ITEM, appSecret)
			asPutDataRequest()
		}

		dataClient.putDataItem(putDataReq)
	}

	fun sendToken(account: String, token: String) {
		val putDataReq = PutDataMapRequest.create(TOKEN_PATH).run {
			dataMap.putString(ACCOUNT_ITEM, account)
			dataMap.putString(TOKEN_ITEM, token)
			asPutDataRequest()
		}

		dataClient.putDataItem(putDataReq)
	}

	fun sendNames(account: String, asleepName: String, awakeName: String) {
		val putDataReq = PutDataMapRequest.create(NAMES_PATH).run {
			dataMap.putString(ACCOUNT_ITEM, account)
			dataMap.putString(ASLEEP_NAME_ITEM, asleepName)
			dataMap.putString(AWAKE_NAME_ITEM, awakeName)
			asPutDataRequest()
		}

		dataClient.putDataItem(putDataReq)
	}

	fun sendOAuthInstance(instance: String) {
		val putDataReq = PutDataMapRequest.create(OAUTH_INSTANCE_PATH).run {
			dataMap.putString(INSTANCE_ITEM, instance)
			asPutDataRequest()
		}

		dataClient.putDataItem(putDataReq)
	}
}
