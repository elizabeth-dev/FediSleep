// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    kotlin("plugin.serialization") version "1.9.20" apply false
    id("com.google.protobuf") version "0.9.4" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.30" apply false
    alias(libs.plugins.android.library) apply false
}