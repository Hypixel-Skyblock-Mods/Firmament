
package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.firmod.features.texturepack.CustomModelOverrideParser
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser
import net.minecraft.world.item.ItemStack

class OrPredicate(val children: Array<FirmodModelPredicate>) : FirmodModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        return children.any { it.test(stack) }
    }

    object Parser : FirmodModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmodModelPredicate {
            val children =
                (jsonElement as JsonArray)
                    .flatMap {
	                    CustomModelOverrideParser.parsePredicates(it as JsonObject)
                    }
                    .toTypedArray()
            return OrPredicate(children)
        }

    }
}
