package sh.elizabeth.fedisleep.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

typealias InstantAsString = @Serializable(InstantAsStringSerializer::class) Instant

object InstantAsStringSerializer : KSerializer<Instant> {
	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Instant) =
		encoder.encodeString(value.toString())

	override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}
