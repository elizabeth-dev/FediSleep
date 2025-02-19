package sh.elizabeth.fedisleep.util

import androidx.health.services.client.data.PassiveListenerConfig

val passiveListenerConfig =
    PassiveListenerConfig.builder().setShouldUserActivityInfoBeRequested(true)
        .build()
