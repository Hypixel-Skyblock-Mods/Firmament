package moe.nea.firmament.mixins.custommodels.screenlayouts;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.firmament.features.texturepack.CustomScreenLayouts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSignEditScreen.class)
public class MoveSignElements {
	@WrapWithCondition(
		method = "extractSign",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractSignEditScreen;extractSignBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
	private boolean onDrawBackgroundSign(AbstractSignEditScreen instance, GuiGraphicsExtractor drawContext) {
		final var override = CustomScreenLayouts.getActiveScreenOverride();
		if (override == null || override.getBackground() == null) return true;
		override.getBackground().renderDirect(drawContext);
		return false;
	}

	@WrapOperation(method = "extractSignText", at = {
		@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textHighlight(IIIIZ)V")}
	)
	private void onRenderSignTextSelection(
		GuiGraphicsExtractor instance, int i, int j, int k, int l, boolean invertText, Operation<Void> original,
		@Local(index = 9) int cursorY) {
		instance.pose().pushMatrix();
		final var override = CustomScreenLayouts.getSignTextMover(cursorY);
		if (override != null) {
			instance.pose().translate(override.getX(), override.getY());
		}
		original.call(instance, i, j, k, l, invertText);
		instance.pose().popMatrix();
	}
	@WrapOperation(method = "extractSignText", at = {
		@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/TextCursorUtils;extractAppendCursor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIIZ)V")}
	)
	private void onRenderSignTextAppendCursor(
		GuiGraphicsExtractor instance, Font font, int x, int y, int color, boolean shadow, Operation<Void> original,
		@Local(index = 9) int cursorY) {
		instance.pose().pushMatrix();
		final var override = CustomScreenLayouts.getSignTextMover(cursorY);
		if (override != null) {
			instance.pose().translate(override.getX(), override.getY());
		}
		original.call(instance, font, x, y, color, shadow);
		instance.pose().popMatrix();
	}
	@WrapOperation(method = "extractSignText", at = {
		@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/TextCursorUtils;extractInsertCursor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIII)V")}
	)
	private void onRenderSignTextInsertCursor(
		GuiGraphicsExtractor instance, int x, int y, int color, int lineHeight, Operation<Void> original,
		@Local(index = 9) int cursorY
		) {
		instance.pose().pushMatrix();
		final var override = CustomScreenLayouts.getSignTextMover(cursorY);
		if (override != null) {
			instance.pose().translate(override.getX(), override.getY());
		}
		original.call(instance, x, y, color, lineHeight);
		instance.pose().popMatrix();
	}

	@WrapOperation(method = "extractSignText", at = {
		@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V")},
		expect = 2)
	private void onRenderSignTextRendering(GuiGraphicsExtractor instance, Font textRenderer, String text, int x, int y, int color, boolean shadow, Operation<Void> original, @Local(index = 9) int cursorY) {
		instance.pose().pushMatrix();
		final var override = CustomScreenLayouts.getSignTextMover(cursorY);
		if (override != null) {
			instance.pose().translate(override.getX(), override.getY());
		}
		original.call(instance, textRenderer, text, x, y, color, shadow);
		instance.pose().popMatrix();
	}

}
