package sh.elizabeth.fedisleep.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(val name: String, val url: String, val width: Int?, val height: Int?)

fun Emoji.toDomain(instance: String) =
	sh.elizabeth.fedisleep.model.Emoji(
		fullEmojiId = "$name@$instance",
		instance = instance,
		shortcode = name,
		url = url
	)
