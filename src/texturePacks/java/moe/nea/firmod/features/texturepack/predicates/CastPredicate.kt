package moe.nea.firmod.features.texturepack.predicates

import com.google.gson.JsonElement
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.features.texturepack.FirmodModelPredicate
import moe.nea.firmod.features.texturepack.FirmodModelPredicateParser

class CastPredicate : FirmodModelPredicate {
	object Parser : FirmodModelPredicateParser {
		override fun parse(jsonElement: JsonElement): FirmodModelPredicate? {
			if (jsonElement.asDouble >= 1) return CastPredicate()
			return NotPredicate(arrayOf(CastPredicate()))
		}
	}

	override fun test(stack: ItemStack, holder: LivingEntity?): Boolean {
		return (holder as? Player)?.fishing != null && holder.mainHandItem === stack
	}

	override fun test(stack: ItemStack): Boolean {
		return false
	}
}
