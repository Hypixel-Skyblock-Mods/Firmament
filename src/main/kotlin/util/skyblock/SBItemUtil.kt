package moe.nea.firmod.util.skyblock

import moe.nea.firmod.util.mc.DataComponentAccessor
import moe.nea.firmod.util.mc.RequiresComponents
import moe.nea.firmod.util.mc.loreAccordingToNbt
import moe.nea.firmod.util.renderingName
import moe.nea.firmod.util.unformattedString

object SBItemUtil {
	@RequiresComponents
	fun DataComponentAccessor.getSearchName(): String {
		val name = this.renderingName.unformattedString
		if (name.contains("Enchanted Book")) {
			val enchant = this.loreAccordingToNbt.firstOrNull()?.unformattedString
			if (enchant != null) return enchant
		}
		if (name.startsWith("[Lvl")) {
			val closing = name.indexOf(']')
			if (closing > 0)
				return name.substring(closing)
		}
		return name
	}
}
