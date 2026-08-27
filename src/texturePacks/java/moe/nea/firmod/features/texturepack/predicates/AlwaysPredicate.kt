
package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser
import net.minecraft.world.item.ItemStack

object AlwaysPredicate : FirmodModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        return true
    }

    object Parser : FirmodModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmodModelPredicate {
            return AlwaysPredicate
        }
    }
}
