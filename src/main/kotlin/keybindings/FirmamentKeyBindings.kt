package moe.nea.firmament.keybindings

import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import moe.nea.firmament.Firmament
import moe.nea.firmament.gui.config.ManagedOption
import moe.nea.firmament.util.TestUtil
import moe.nea.firmament.util.data.ManagedConfig

object FirmamentKeyBindings {
	val cats = mutableMapOf<ManagedConfig.Category, KeyMapping.Category>()


	fun registerKeyBinding(name: String, config: ManagedOption<SavedKeyBinding>) {
		val vanillaKeyBinding = KeyMapping(
			name,
			InputConstants.Type.KEYSYM,
			-1,
			cats.computeIfAbsent(config.element.category) {
				KeyMapping.Category.register(Firmament.identifier(it.name.lowercase()))
			}
		)
		if (!TestUtil.isInTest) {
			KeyMappingHelper.registerKeyMapping(vanillaKeyBinding)
		}
		keyBindings[vanillaKeyBinding] = config
	}

	val keyBindings = mutableMapOf<KeyMapping, ManagedOption<SavedKeyBinding>>()

}
