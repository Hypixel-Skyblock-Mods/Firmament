package moe.nea.firmod.mixins;

import moe.nea.firmod.util.mc.TolerantRegistriesOps;
import net.minecraft.core.HolderOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HolderOwner.class)
public interface TolerateFirmodTolerateRegistryOwners<T> {
	@Inject(method = "canSerializeIn", at = @At("HEAD"), cancellable = true)
	private void equalTolerantRegistryOwners(HolderOwner<T> context, CallbackInfoReturnable<Boolean> cir) {
		if (context instanceof TolerantRegistriesOps.TolerantOwner<?>) {
			cir.setReturnValue(true);
		}
	}
}
