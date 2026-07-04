

package moe.nea.firmod.events

import net.minecraft.client.gui.screens.Screen

data class ScreenChangeEvent(val old: Screen?, val new: Screen?) : FirmodEvent.Cancellable() {
    var overrideScreen: Screen? = null
    companion object : FirmodEventBus<ScreenChangeEvent>()
}
