package moe.nea.firmod.util.render;

import moe.nea.firmod.events.WorldRenderLastEvent;
import moe.nea.firmod.util.render.gui.pip.RenderCircleProgressRenderer;
import moe.nea.firmod.util.render.gui.pip.RenderLineRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelTerrainRenderContext;

public class FirmodRenderSystem {

	public static void init() {
		LevelRenderEvents.START_MAIN.register(FirmodRenderSystem::startMainRendering);
		LevelRenderEvents.END_MAIN.register(FirmodRenderSystem::endMainRendering);

		RenderCircleProgressRenderer.init();
		RenderLineRenderer.init();
	}

	public static void close() {
		CustomRenderer.close();
	}

	private static void startMainRendering(LevelTerrainRenderContext context) {
		CustomRenderer.prepare();
	}

	private static void endMainRendering(LevelRenderContext context) {
		WorldRenderLastEvent event = new WorldRenderLastEvent(context.poseStack(), context.levelState().cameraRenderState);
		WorldRenderLastEvent.Companion.publish(event);

		CustomRenderer.executeDraws();
	}
}
