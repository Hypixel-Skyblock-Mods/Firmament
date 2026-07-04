package moe.nea.firmod.gui

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext

class EmptyComponent : GuiComponent() {
	override fun getWidth(): Int {
		return 0
	}

	override fun getHeight(): Int {
		return 0
	}

	override fun render(context: GuiImmediateContext) {
	}
}
