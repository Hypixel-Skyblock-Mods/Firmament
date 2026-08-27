
package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser
import moe.nea.firmod.features.texturepack.StringMatcher
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.mc.loreAccordingToNbt

class LorePredicate(val matcher: StringMatcher) : FirmodModelPredicate {
    object Parser : FirmodModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmodModelPredicate {
            return LorePredicate(StringMatcher.parse(jsonElement))
        }
    }

    override fun test(stack: ItemStack): Boolean {
        val lore = stack.accessor().loreAccordingToNbt
        return lore.any { matcher.matches(it) }
    }
}
