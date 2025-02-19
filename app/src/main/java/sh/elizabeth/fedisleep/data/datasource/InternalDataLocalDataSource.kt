package sh.elizabeth.fedisleep.data.datasource

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedisleep.AccountSettings
import sh.elizabeth.fedisleep.Instance
import sh.elizabeth.fedisleep.InternalData
import sh.elizabeth.fedisleep.copy
import sh.elizabeth.fedisleep.data.datasource.model.InternalDataAccountSettings
import sh.elizabeth.fedisleep.data.datasource.model.InternalDataInstance
import sh.elizabeth.fedisleep.data.datasource.model.InternalDataValues
import sh.elizabeth.fedisleep.util.SupportedInstances
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class InternalDataSerializer @Inject constructor() : Serializer<InternalData> {
    override val defaultValue: InternalData = InternalData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): InternalData {
        try {
            return InternalData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: InternalData, output: OutputStream,
    ) = t.writeTo(output)
}

class InternalDataLocalDataSource @Inject constructor(
    private val internalDataDataStore: DataStore<InternalData>,
) {
    val internalData = internalDataDataStore.data.map {
        InternalDataValues(
            lastLoginInstance = it.lastLoginInstance,
            activeAccount = it.activeAccount,
            accountTokens = it.accountTokensMap,
            instances = it.instancesMap.map { (key, value) ->
                key to InternalDataInstance(
                    instanceType = SupportedInstances.valueOf(value.instanceType),
                    appId = value.appId,
                    appSecret = value.appSecret,
                    delegatedEndpoint = value.delegatedEndpoint
                )
            }.toMap(),
            accountSettings = it.accountSettingsMap.map { (key, value) ->
                key to InternalDataAccountSettings(
                    asleepName = value.asleepName,
                    awakeName = value.awakeName
                )
            }.toMap()
        )
    }

    suspend fun setLastLoginInstance(instance: String) {
        try {
            internalDataDataStore.updateData {
                it.copy {
                    lastLoginInstance = instance
                }
            }
        } catch (ioException: IOException) {
            Log.e(
                "InternalDataLocalDataSource",
                "Failed to update settings",
                ioException
            )
        }
    }

    suspend fun setActiveAccount(identifier: String) {
        try {
            internalDataDataStore.updateData {
                it.copy {
                    activeAccount = identifier
                }
            }
        } catch (ioException: IOException) {
            Log.e(
                "InternalDataLocalDataSource",
                "Failed to update settings",
                ioException
            )
        }
    }

    suspend fun setAccessToken(
        accountIdentifier: String,
        newAccessToken: String,
    ) {
        try {
            internalDataDataStore.updateData {
                it.copy {
                    accountTokens.put(accountIdentifier, newAccessToken)
                }
            }
        } catch (ioException: IOException) {
            Log.e(
                "InternalDataLocalDataSource",
                "Failed to update settings",
                ioException
            )
        }
    }

    suspend fun setInstance(
        instance: String,
        newDelegatedEndpoint: String?,
        newInstanceType: SupportedInstances?,
        newAppId: String?,
        newAppSecret: String?,
    ) {
        try {
            val currentInstance =
                internalDataDataStore.data.map { it.instancesMap[instance] }
                    .first()

            internalDataDataStore.updateData {
                it.copy {
                    instances.put(instance, Instance.newBuilder().apply {
                        instanceType =
                            newInstanceType?.name
                                ?: currentInstance?.instanceType
                        delegatedEndpoint =
                            newDelegatedEndpoint ?: currentInstance?.delegatedEndpoint
                        appId = newAppId ?: currentInstance?.appId ?: ""
                        appSecret =
                            newAppSecret ?: currentInstance?.appSecret ?: ""
                    }.build())
                }
            }
        } catch (ioException: IOException) {
            Log.e(
                "InternalDataLocalDataSource",
                "Failed to update settings",
                ioException
            )
        }
    }

    suspend fun setNames(accountIdentifier: String, newAsleepName: String, newAwakeName: String) {
        try {
            internalDataDataStore.updateData {
                it.copy {
                    accountSettings.put(accountIdentifier, AccountSettings.newBuilder().apply {
                        asleepName = newAsleepName
                        awakeName = newAwakeName
                    }.build())
                }
            }
        } catch (ioException: IOException) {
            Log.e(
                "InternalDataLocalDataSource",
                "Failed to update settings",
                ioException
            )
        }
    }
}
