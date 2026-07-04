package moe.nea.firmod.features.texturepack

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

@Serializable(with = FirmodRootPredicateSerializer::class)
interface FirmodModelPredicate {
	fun test(stack: ItemStack, holder: LivingEntity?): Boolean = test(stack)
	fun test(stack: ItemStack): Boolean = test(stack, null)
}
