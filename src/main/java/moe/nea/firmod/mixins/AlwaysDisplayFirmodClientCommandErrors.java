package moe.nea.firmod.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientCommandInternals.class)
public class AlwaysDisplayFirmodClientCommandErrors {
	@ModifyExpressionValue(method = "executeCommand", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/command/client/ClientCommandInternals;isIgnoredException(Lcom/mojang/brigadier/exceptions/CommandExceptionType;)Z"))
	private static boolean markFirmodExceptionsAsNotIgnores(boolean original, @Local(argsOnly = true) String command) {
		if (command.startsWith("firm ") || command.equals("firm") || command.startsWith("firmod ") || command.equals("firmod")) {
			return false;
		}
		return original;
	}
}
