package moe.nea.firmod.features.items.recipes

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import net.minecraft.client.gui.GuiGraphicsExtractor

class FireWidget(override var position: Point, val animationTicks: Int) : RecipeWidget() {
	override val size: Dimension
		get() = Dimension(10, 10)

	override fun extractRenderState(
		GuiGraphicsExtractor: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		TODO("Not yet implemented")
	}
}
