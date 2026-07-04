package moe.nea.firmod.events

import moe.nea.firmod.keybindings.GenericInputAction
import moe.nea.firmod.keybindings.InputModifiers
import moe.nea.firmod.keybindings.SavedKeyBinding

data class WorldKeyboardEvent(val action: GenericInputAction, val modifiers: InputModifiers) : FirmodEvent.Cancellable() {
	fun matches(keyBinding: SavedKeyBinding, atLeast: Boolean = false): Boolean {
		return keyBinding.matches(action, modifiers, atLeast)
	}

	companion object : FirmodEventBus<WorldKeyboardEvent>()
}
