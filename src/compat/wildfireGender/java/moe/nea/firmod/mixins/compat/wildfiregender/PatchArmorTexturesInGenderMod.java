package moe.nea.firmod.mixins.compat.wildfiregender;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.wildfire.client.render.GenderArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import moe.nea.firmod.features.texturepack.CustomGlobalArmorOverrides;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GenderArmorLayer.class)
@Pseudo
public class PatchArmorTexturesInGenderMod {
	@ModifyExpressionValue(method = "renderArmor",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
	private Object replaceArmorMaterial(Object original, @Local(argsOnly = true) HumanoidRenderState state) {
		var overrides = CustomGlobalArmorOverrides.overrideArmor(state.chestEquipment, EquipmentSlot.CHEST);
		return overrides.orElse((Equippable) original);
	}
}
