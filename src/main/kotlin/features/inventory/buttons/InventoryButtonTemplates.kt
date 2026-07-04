package moe.nea.firmod.features.inventory.buttons

import net.minecraft.network.chat.Component
import moe.nea.firmod.Firmod
import moe.nea.firmod.util.ErrorUtil
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.TemplateUtil

object InventoryButtonTemplates {

	val legacyPrefix = "NEUBUTTONS/"
	val modernPrefix = "MAYBEONEDAYIWILLHAVEMYOWNFORMAT"

	fun loadTemplate(t: String): List<InventoryButton>? {
		val buttons = TemplateUtil.maybeDecodeTemplate<List<String>>(legacyPrefix, t) ?: return null
		return buttons.mapNotNull {
			ErrorUtil.catch<InventoryButton?>("Could not import button") {
				Firmod.json.decodeFromString<InventoryButton>(it).also {
					if (it.icon?.startsWith("extra:") == true) {
						MC.sendChat(Component.translatable("firmod.inventory-buttons.import-failed"))
					}
				}
			}.or {
				null
			}
		}
	}

	fun saveTemplate(buttons: List<InventoryButton>): String {
		return TemplateUtil.encodeTemplate(legacyPrefix, buttons.map { Firmod.json.encodeToString(it) })
	}
}
