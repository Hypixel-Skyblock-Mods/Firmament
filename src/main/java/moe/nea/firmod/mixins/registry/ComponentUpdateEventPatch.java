package moe.nea.firmod.mixins.registry;

import moe.nea.firmod.events.ComponentsLoadedEvent;
import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryDataCollector.class)
public class ComponentUpdateEventPatch {
	@Inject(method = "updateComponents", at = @At("RETURN"))
	private static void onAfterUpdateComponents(RegistryAccess.Frozen frozenRegistries, boolean includeSharedRegistries, CallbackInfo ci) {
		ComponentsLoadedEvent.Companion.publish(new ComponentsLoadedEvent());
	}
}
