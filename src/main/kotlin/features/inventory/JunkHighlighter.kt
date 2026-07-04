package moe.nea.firmod.features.inventory

import org.lwjgl.glfw.GLFW
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.SlotRenderEvents
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig
import moe.nea.firmod.util.mc.RequiresComponents
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.skyblock.SBItemUtil.getSearchName
import moe.nea.firmod.util.useMatch

object JunkHighlighter {
	val identifier: String
		get() = "junk-highlighter"

	@Config
	object TConfig : ManagedConfig(identifier, Category.INVENTORY) {
		val junkRegex by string("regex") { "" }
		val highlightBind by keyBinding("highlight") { GLFW.GLFW_KEY_LEFT_CONTROL }
	}

	@OptIn(RequiresComponents::class)
	@Subscribe
	fun onDrawSlot(event: SlotRenderEvents.After) {
		if (!TConfig.highlightBind.isPressed() || TConfig.junkRegex.isEmpty()) return
		val junkRegex = TConfig.junkRegex.toPattern()
		val slot = event.slot
		junkRegex.useMatch(slot.item.accessor().getSearchName()) {
			event.context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xffff0000.toInt())
		}
	}
}
