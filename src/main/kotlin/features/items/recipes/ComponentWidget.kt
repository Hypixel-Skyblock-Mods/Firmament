package moe.nea.firmod.features.items.recipes

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import moe.nea.firmod.repo.recipes.RecipeLayouter
import moe.nea.firmod.util.MC

class ComponentWidget(override var position: Point, var text: Component) : RecipeWidget(), RecipeLayouter.Updater<Component> {
	override fun update(newValue: Component) {
		this.text = newValue
	}

	override val size: Dimension
		get() = Dimension(MC.font.width(text), MC.font.lineHeight)

	override fun extractRenderState(
		GuiGraphicsExtractor: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		GuiGraphicsExtractor.text(MC.font, text, position.x, position.y, -1)
	}
}
