

package moe.nea.firmod.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moe.nea.firmod.features.fixes.Fixes;
import net.minecraft.client.gui.components.ChatComponent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class ChatPeekingPatch {

	@ModifyVariable(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent$DisplayMode;showRestrictedPrompt:Z", opcode = Opcodes.GETFIELD), name = "isForeground")
	public boolean onGetChatHud(boolean isForeground) {
		if (Fixes.INSTANCE.shouldPeekChat())
			return true; // TODO: displayMode is final, uff
		return isForeground;
	}

	@ModifyExpressionValue(method = "getHeight()I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z"))
	public boolean onGetChatHudHeight(boolean old) {
		return old || Fixes.INSTANCE.shouldPeekChat();
	}

}
