package sh.elizabeth.fedisleep.common

data class InstanceEndpointTypeToken(
    val instance: String,
    val endpoint: String,
    val instanceType: SupportedInstances,
    val token: String,
)