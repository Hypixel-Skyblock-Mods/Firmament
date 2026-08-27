package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser

class PullingPredicate(val percentage: Double) : FirmodModelPredicate {
	companion object {
		val AnyPulling = PullingPredicate(0.1)
	}

	object Parser : FirmodModelPredicateParser {
		override fun parse(jsonElement: JsonElement): FirmodModelPredicate? {
			return PullingPredicate(jsonElement.asDouble)
		}
	}

	override fun test(stack: ItemStack, holder: LivingEntity?): Boolean {
		if (holder == null) return false
		return BowItem.getPowerForTime(holder.ticksUsingItem) >= percentage
	}

}
