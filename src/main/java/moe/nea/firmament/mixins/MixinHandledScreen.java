

package moe.nea.firmament.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moe.nea.firmament.events.*;
import moe.nea.firmament.events.HandledScreenClickEvent;
import moe.nea.firmament.keybindings.GenericInputAction;
import moe.nea.firmament.keybindings.InputModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractContainerScreen.class, priority = 990)
public abstract class MixinHandledScreen<T extends AbstractContainerMenu> {

	@Shadow
	@Final
	protected T menu;

	@Shadow
	public abstract T getMenu();

	@Shadow
	protected int topPos;
	@Shadow
	protected int leftPos;
	@Unique
	Inventory playerInventory;

	@Inject(method = "<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V", at = @At("TAIL"))
	public void savePlayerInventory(AbstractContainerMenu menu, Inventory inventory, Component title, int imageWidth, int imageHeight, CallbackInfo ci) {
		this.playerInventory = inventory;
	}

	@Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
	private void onMouseReleased(MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir) {
		var self = (AbstractContainerScreen<?>) (Object) this;
		var clickEvent = new HandledScreenClickEvent(self, click.x(), click.y(), click.button());
		var keyEvent = new HandledScreenKeyReleasedEvent(self, GenericInputAction.mouse(click), InputModifiers.of(click.modifiers()));
		if (HandledScreenClickEvent.Companion.publish(clickEvent).getCancelled()
			|| HandledScreenKeyReleasedEvent.Companion.publish(keyEvent).getCancelled()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "extractContents", at = @At("HEAD"))
	public void onAfterRenderForeground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		HandledScreenForegroundEvent.Companion.publish(new HandledScreenForegroundEvent((AbstractContainerScreen<?>) (Object) this, context, mouseX, mouseY, delta));
	}

	@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
	public void onMouseClickedSlot(Slot slot, int slotId, int button, ContainerInput actionType, CallbackInfo ci) {
		if (slotId == -999 && getMenu() != null && actionType == ContainerInput.PICKUP) { // -999 is code for "clicked outside the main window"
			ItemStack cursorStack = getMenu().getCarried();
			if (cursorStack != null && IsSlotProtectedEvent.shouldBlockInteraction(slot, ContainerInput.THROW, IsSlotProtectedEvent.MoveOrigin.INVENTORY_MOVE, cursorStack)) {
				ci.cancel();
				return;
			}
		}
		if (IsSlotProtectedEvent.shouldBlockInteraction(slot, actionType, IsSlotProtectedEvent.MoveOrigin.INVENTORY_MOVE)) {
			ci.cancel();
			return;
		}
		if (actionType == ContainerInput.SWAP && 0 <= button && button < 9) {
			if (IsSlotProtectedEvent.shouldBlockInteraction(new Slot(playerInventory, button, 0, 0), actionType, IsSlotProtectedEvent.MoveOrigin.INVENTORY_MOVE)) {
				ci.cancel();
			}
		}
	}


	@WrapOperation(method = "extractSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V"))
	public void onDrawSlots(AbstractContainerScreen instance, GuiGraphicsExtractor graphics, Slot slot, int i, int j, Operation<Void> original) {
		var before = new SlotRenderEvents.Before(graphics, slot);
		SlotRenderEvents.Before.Companion.publish(before);
		original.call(instance, graphics, slot, i, j);
		var after = new SlotRenderEvents.After(graphics, slot);
		SlotRenderEvents.After.Companion.publish(after);
	}
}
