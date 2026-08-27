package moe.nea.firmod.mixins;

import moe.nea.firmod.events.HandledScreenKeyPressedEvent;
import moe.nea.firmod.keybindings.GenericInputAction;
import moe.nea.firmod.keybindings.InputModifiers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenInputEvents {
	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	public void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (HandledScreenKeyPressedEvent.Companion.publish(new HandledScreenKeyPressedEvent(
			(Screen) (Object) this,
			GenericInputAction.of(event),
			InputModifiers.of(event))).getCancelled()) {
			cir.setReturnValue(true);
		}
	}

	@SuppressWarnings("MissingUnique")
	public boolean onMouseClicked_firmod_generic(MouseButtonEvent click, boolean doubled) {
		return HandledScreenKeyPressedEvent.Companion.publish(
			new HandledScreenKeyPressedEvent((Screen) (Object) this,
				GenericInputAction.mouse(click), InputModifiers.current())).getCancelled();
	}
}
