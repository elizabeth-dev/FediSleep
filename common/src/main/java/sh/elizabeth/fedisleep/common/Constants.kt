package sh.elizabeth.fedisleep.common

import kotlinx.serialization.SerialName

enum class SupportedInstances {
    @SerialName("iceshrimp")
    ICESHRIMP,
}

const val NAMES_PATH = "/names"
const val ACCOUNT_ITEM = "accounts"
const val ASLEEP_NAME_ITEM = "asleepName"
const val AWAKE_NAME_ITEM = "awakeName"

const val INSTANCE_PATH = "/instance"
const val INSTANCE_ITEM = "instance"
const val ENDPOINT_ITEM = "endpoint"
const val TYPE_ITEM = "type"
const val APP_ID_ITEM = "appId"
const val APP_SECRET_ITEM = "appSecret"

const val TOKEN_PATH = "/token"
const val TOKEN_ITEM = "token"

const val OAUTH_INSTANCE_PATH = "/oauthInstance"
