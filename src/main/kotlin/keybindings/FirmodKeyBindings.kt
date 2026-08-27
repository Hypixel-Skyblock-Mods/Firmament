package moe.nea.firmod.keybindings

import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import moe.nea.firmod.Firmod
import moe.nea.firmod.gui.config.ManagedOption
import moe.nea.firmod.util.TestUtil
import moe.nea.firmod.util.data.ManagedConfig

object FirmodKeyBindings {
	val cats = mutableMapOf<ManagedConfig.Category, KeyMapping.Category>()


	fun registerKeyBinding(name: String, config: ManagedOption<SavedKeyBinding>) {
		val vanillaKeyBinding = KeyMapping(
			name,
			InputConstants.Type.KEYSYM,
			-1,
			cats.computeIfAbsent(config.element.category) {
				KeyMapping.Category.register(Firmod.identifier(it.name.lowercase()))
			}
		)
		if (!TestUtil.isInTest) {
			KeyMappingHelper.registerKeyMapping(vanillaKeyBinding)
		}
		keyBindings[vanillaKeyBinding] = config
	}

	val keyBindings = mutableMapOf<KeyMapping, ManagedOption<SavedKeyBinding>>()

}
