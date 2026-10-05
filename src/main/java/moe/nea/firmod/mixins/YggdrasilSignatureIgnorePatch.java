

package moe.nea.firmod.mixins;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.services.MinecraftServicesKeyInfo;
import moe.nea.firmod.features.fixes.Fixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftServicesKeyInfo.class, remap = false)
public class YggdrasilSignatureIgnorePatch {
    @Inject(method = "validateProperty", at = @At("HEAD"), cancellable = true, remap = false)
    public void validate(Property property, CallbackInfoReturnable<Boolean> cir) {
        if (Fixes.TConfig.INSTANCE.getFixUnsignedPlayerSkins()) {
            cir.setReturnValue(true);
        }
    }
}
