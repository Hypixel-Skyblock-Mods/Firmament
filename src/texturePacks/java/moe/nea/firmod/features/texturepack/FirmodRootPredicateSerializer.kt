package moe.nea.firmod.features.texturepack

import com.google.gson.JsonObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import moe.nea.firmod.features.texturepack.predicates.AndPredicate
import moe.nea.firmod.util.json.intoGson

object FirmodRootPredicateSerializer : KSerializer<FirmodModelPredicate> {
	val delegateSerializer = kotlinx.serialization.json.JsonObject.serializer()
	override val descriptor: SerialDescriptor
		get() = SerialDescriptor("FirmodModelRootPredicate", delegateSerializer.descriptor)

	override fun deserialize(decoder: Decoder): FirmodModelPredicate {
		val json = decoder.decodeSerializableValue(delegateSerializer).intoGson() as JsonObject
		return AndPredicate(CustomModelOverrideParser.parsePredicates(json).toTypedArray())
	}

	override fun serialize(encoder: Encoder, value: FirmodModelPredicate) {
		TODO("Cannot serialize firmod predicates")
	}
}
