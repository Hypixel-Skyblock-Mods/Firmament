

package moe.nea.firmod.events

import com.mojang.brigadier.CommandDispatcher

data class MaskCommands(val dispatcher: CommandDispatcher<*>) : FirmodEvent() {
    companion object : FirmodEventBus<MaskCommands>()

    fun mask(name: String) {
        dispatcher.root.children.removeIf { it.name.equals(name, ignoreCase = true) }
    }
}
