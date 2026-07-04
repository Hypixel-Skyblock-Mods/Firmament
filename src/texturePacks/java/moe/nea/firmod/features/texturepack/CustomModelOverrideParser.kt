package moe.nea.firmod.features.texturepack

import com.google.gson.JsonObject
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier
import moe.nea.firmod.Firmod
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.FinalizeResourceManagerEvent
import moe.nea.firmod.features.texturepack.predicates.AndPredicate
import moe.nea.firmod.features.texturepack.predicates.CastPredicate
import moe.nea.firmod.features.texturepack.predicates.DisplayNamePredicate
import moe.nea.firmod.features.texturepack.predicates.ExtraAttributesPredicate
import moe.nea.firmod.features.texturepack.predicates.GenericComponentPredicate
import moe.nea.firmod.features.texturepack.predicates.ItemPredicate
import moe.nea.firmod.features.texturepack.predicates.LorePredicate
import moe.nea.firmod.features.texturepack.predicates.NotPredicate
import moe.nea.firmod.features.texturepack.predicates.OrPredicate
import moe.nea.firmod.features.texturepack.predicates.PetPredicate
import moe.nea.firmod.features.texturepack.predicates.PullingPredicate
import moe.nea.firmod.features.texturepack.predicates.SkullPredicate
import moe.nea.firmod.util.json.KJsonOps

object CustomModelOverrideParser {

	val LEGACY_CODEC: Codec<FirmodModelPredicate> =
		Codec.of(
			Encoder.error("cannot encode legacy firmod model predicates"),
			object : Decoder<FirmodModelPredicate> {
				override fun <T : Any?> decode(
					ops: DynamicOps<T>,
					input: T
				): DataResult<Pair<FirmodModelPredicate, T>> {
					try {
						val pred = Firmod.json.decodeFromJsonElement(
							FirmodRootPredicateSerializer,
							ops.convertTo(KJsonOps.INSTANCE, input))
						return DataResult.success(Pair.of(pred, ops.empty()))
					} catch (ex: Exception) {
						return DataResult.error { "Could not deserialize ${ex.message}" }
					}
				}
			}
		)

	val predicateParsers = mutableMapOf<Identifier, FirmodModelPredicateParser>()


	fun registerPredicateParser(name: String, parser: FirmodModelPredicateParser) {
		predicateParsers[Identifier.fromNamespaceAndPath("firmod", name)] = parser
	}

	init {
		registerPredicateParser("display_name", DisplayNamePredicate.Parser)
		registerPredicateParser("lore", LorePredicate.Parser)
		registerPredicateParser("all", AndPredicate.Parser)
		registerPredicateParser("any", OrPredicate.Parser)
		registerPredicateParser("not", NotPredicate.Parser)
		registerPredicateParser("item", ItemPredicate.Parser)
		registerPredicateParser("extra_attributes", ExtraAttributesPredicate.Parser)
		registerPredicateParser("pet", PetPredicate.Parser)
		registerPredicateParser("component", GenericComponentPredicate.Parser)
		registerPredicateParser("skull", SkullPredicate.Parser)
	}

	private val neverPredicate = listOf(
		object : FirmodModelPredicate {
			override fun test(stack: ItemStack): Boolean {
				return false
			}
		}
	)

	fun parsePredicates(predicates: JsonObject?): List<FirmodModelPredicate> {
		if (predicates == null) return neverPredicate
		val parsedPredicates = mutableListOf<FirmodModelPredicate>()
		for (predicateName in predicates.keySet()) {
			if (predicateName == "cast") { // 1.21.4
				parsedPredicates.add(CastPredicate.Parser.parse(predicates[predicateName]) ?: return neverPredicate)
			}
			if (predicateName == "pull") {
				parsedPredicates.add(PullingPredicate.Parser.parse(predicates[predicateName]) ?: return neverPredicate)
			}
			if (predicateName == "pulling") {
				parsedPredicates.add(PullingPredicate.AnyPulling)
			}
			if (!predicateName.startsWith("firmod:")) continue
			val identifier = Identifier.parse(predicateName)
			val parser = predicateParsers[identifier] ?: return neverPredicate
			val parsedPredicate = parser.parse(predicates[predicateName]) ?: return neverPredicate
			parsedPredicates.add(parsedPredicate)
		}
		return parsedPredicates
	}

	@JvmStatic
	fun parseCustomModelOverrides(jsonObject: JsonObject): Array<FirmodModelPredicate>? {
		val predicates = (jsonObject["predicate"] as? JsonObject) ?: return null
		val parsedPredicates = parsePredicates(predicates)
		if (parsedPredicates.isEmpty())
			return null
		return parsedPredicates.toTypedArray()
	}

	@Subscribe
	fun finalizeResources(event: FinalizeResourceManagerEvent) {
		ItemModels.ID_MAPPER.put(
			Firmod.identifier("predicates/legacy"),
			PredicateModel.Unbaked.CODEC
		)
		ItemModels.ID_MAPPER.put(
			Firmod.identifier("head_model"),
			HeadModelChooser.Unbaked.CODEC
		)
	}

}
