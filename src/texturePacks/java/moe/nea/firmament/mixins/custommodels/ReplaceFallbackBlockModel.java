package moe.nea.firmament.mixins.custommodels;

import moe.nea.firmament.features.texturepack.CustomBlockTextures;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateModelSet.class)
public class ReplaceFallbackBlockModel {
    // TODO: add check to BlockDustParticle
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void getModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir) {
        var replacement = CustomBlockTextures.getReplacementModel(state, null);
        if (replacement != null)
            cir.setReturnValue(replacement);
    }
}
