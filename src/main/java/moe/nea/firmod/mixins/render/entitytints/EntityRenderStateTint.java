package moe.nea.firmod.mixins.render.entitytints;

import moe.nea.firmod.events.EntityRenderTintEvent;
import moe.nea.firmod.util.render.TintedOverlayTexture;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateTint implements EntityRenderTintEvent.HasTintRenderState {
	@Unique
	int tint = -1;
	@Unique
	TintedOverlayTexture overlayTexture;
	@Unique
	boolean hasTintOverride = false;

	@Override
	public int getTint_firmod() {
		return tint;
	}

	@Override
	public void setTint_firmod(int i) {
		tint = i;
		hasTintOverride = true;
	}

	@Override
	public boolean getHasTintOverride_firmod() {
		return hasTintOverride;
	}

	@Override
	public void setHasTintOverride_firmod(boolean b) {
		hasTintOverride = b;
	}

	@Override
	public void reset_firmod() {
		hasTintOverride = false;
		overlayTexture = null;
	}

	@Override
	public @Nullable TintedOverlayTexture getOverlayTexture_firmod() {
		return overlayTexture;
	}

	@Override
	public void setOverlayTexture_firmod(@Nullable TintedOverlayTexture tintedOverlayTexture) {
		this.overlayTexture = tintedOverlayTexture;
	}
}
