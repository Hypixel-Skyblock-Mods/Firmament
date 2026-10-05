

package moe.nea.firmod.mixins;

import moe.nea.firmod.events.IsSlotProtectedEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class PlayerDropEventPatch {
	@Inject(method = "dropItem(Lnet/minecraft/client/player/LocalPlayer;Z)V", at = @At("HEAD"), cancellable = true)
	public void onDropSelectedItem(LocalPlayer player, boolean all, CallbackInfo ci) {
		Slot fakeSlot = new Slot(player.getInventory(), player.getInventory().getSelectedSlot(), 0, 0);
		if (IsSlotProtectedEvent.shouldBlockInteraction(fakeSlot, ContainerInput.THROW, IsSlotProtectedEvent.MoveOrigin.DROP_FROM_HOTBAR)) {
			ci.cancel();
		}
	}
}
