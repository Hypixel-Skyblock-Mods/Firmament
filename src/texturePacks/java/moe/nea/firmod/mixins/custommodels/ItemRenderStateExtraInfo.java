package moe.nea.firmod.mixins.custommodels;

import moe.nea.firmod.features.texturepack.HeadModelChooser;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public class ItemRenderStateExtraInfo implements HeadModelChooser.HasExplicitHeadModelMarker {
	@Unique
	boolean hasExplicitHead_firmod = false;

	@Inject(method = "clear", at = @At("HEAD"))
	private void clear(CallbackInfo ci) {
		hasExplicitHead_firmod = false;
	}

	@Override
	public void markExplicitHead_Firmod() {
		hasExplicitHead_firmod = true;
	}

	@Override
	public boolean isExplicitHeadModel_Firmod() {
		return hasExplicitHead_firmod;
	}
}
