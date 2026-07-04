
package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import moe.nea.firmod.util.MC

class ItemPredicate(
    val item: Item
) : FirmodModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        return stack.`is`(item)
    }

    object Parser : FirmodModelPredicateParser {
        override fun parse(jsonElement: JsonElement): ItemPredicate? {
            if (jsonElement is JsonPrimitive && jsonElement.isString) {
                val itemKey = ResourceKey.create(Registries.ITEM,
                                             Identifier.tryParse(jsonElement.asString)
                                                 ?: return null)
                return ItemPredicate(MC.defaultItems.get(itemKey).getOrNull()?.value() ?: return null)
            }
            return null
        }
    }
}
