package moe.nea.firmod.features.items.recipes

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.entity.LivingEntity
import moe.nea.firmod.gui.entity.EntityRenderer

class EntityWidget(
	override var position: Point,
	override val size: Dimension,
	val entity: LivingEntity
) : RecipeWidget() {
	override fun extractRenderState(
		GuiGraphicsExtractor: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		EntityRenderer.renderEntity(
			entity, GuiGraphicsExtractor,
			rect.x, rect.y,
			rect.width.toDouble(), rect.height.toDouble(),
			mouseX.toDouble(), mouseY.toDouble()
		)
	}
}
