package moe.nea.firmod.compat.yacl

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import moe.nea.firmod.gui.config.ManagedOption
import moe.nea.firmod.keybindings.SavedKeyBinding

class KeybindingBuilder(
	val option: Option<SavedKeyBinding>,
	val managedOption: ManagedOption<SavedKeyBinding>
) : ControllerBuilder<SavedKeyBinding> {
	override fun build(): Controller<SavedKeyBinding> {
		return KeybindingController(option, managedOption)
	}
}
