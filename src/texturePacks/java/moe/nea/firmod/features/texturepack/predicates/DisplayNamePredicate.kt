
package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser
import moe.nea.firmod.features.texturepack.StringMatcher
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.mc.displayNameAccordingToNbt

data class DisplayNamePredicate(val stringMatcher: StringMatcher) : FirmodModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        val display = stack.accessor().displayNameAccordingToNbt
        return stringMatcher.matches(display)
    }

    object Parser : FirmodModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmodModelPredicate {
            return DisplayNamePredicate(StringMatcher.parse(jsonElement))
        }
    }
}
