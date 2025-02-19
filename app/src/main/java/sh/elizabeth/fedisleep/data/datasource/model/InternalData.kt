package sh.elizabeth.fedisleep.data.datasource.model

import sh.elizabeth.fedisleep.util.SupportedInstances

data class InternalDataValues(
    val lastLoginInstance: String,
    val activeAccount: String,
    val accountTokens: Map<String, String>,
    val instances: Map<String, InternalDataInstance>,
    val accountSettings: Map<String, InternalDataAccountSettings>
)

data class InternalDataInstance(
    val instanceType: SupportedInstances,
    val appId: String?,
    val appSecret: String?,
    val delegatedEndpoint: String,
)

data class InternalDataAccountSettings(
    val asleepName: String,
    val awakeName: String,
)