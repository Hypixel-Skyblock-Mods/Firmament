
package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.firmod.features.texturepack.CustomModelOverrideParser
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser
import net.minecraft.world.item.ItemStack

class NotPredicate(val children: Array<FirmodModelPredicate>) : FirmodModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        return children.none { it.test(stack) }
    }

    object Parser : FirmodModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmodModelPredicate {
            return NotPredicate(CustomModelOverrideParser.parsePredicates(jsonElement as JsonObject).toTypedArray())
        }
    }
}
