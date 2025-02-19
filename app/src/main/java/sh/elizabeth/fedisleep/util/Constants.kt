package sh.elizabeth.fedisleep.util

import androidx.health.services.client.data.PassiveListenerConfig
import kotlinx.serialization.SerialName

enum class SupportedInstances {
    @SerialName("iceshrimp")
    ICESHRIMP,
}

const val APP_NAME = "Test Fedi Client"
const val APP_DESCRIPTION = "Test Fedi Client by @elizabeth@tech.lgbt"

val ICESHRIMP_APP_PERMISSION = listOf(
    "read:account",
    "write:account",
)

const val APP_DEEPLINK_URI = "app://sh.elizabeth.fedisleep"
const val APP_LOGIN_OAUTH_PATH = "/login/oauth"

val passiveListenerConfig =
    PassiveListenerConfig.builder().setShouldUserActivityInfoBeRequested(true)
        .build()