package moe.nea.firmod.mixins.compat.jade;

import moe.nea.firmod.compat.jade.CustomMiningHardnessProvider;
import moe.nea.firmod.util.MC;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public class OnUpdateBreakProgress {
	@Inject(method = "destroyBlockProgress", at = @At("HEAD"))
	private void replaceBreakProgress(int entityId, BlockPos pos, int stage, CallbackInfo ci) {
		if (entityId == 0 && null != MC.INSTANCE.getInteractionManager() && Objects.equals(MC.INSTANCE.getInteractionManager().destroyBlockPos, pos)) {
			CustomMiningHardnessProvider.setBreakingInfo(pos, stage);
		}
	}
}
