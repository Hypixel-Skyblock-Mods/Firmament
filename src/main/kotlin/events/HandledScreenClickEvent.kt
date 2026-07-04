package moe.nea.firmod.events

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenClickEvent(
	val screen: AbstractContainerScreen<*>,
	val mouseX: Double, val mouseY: Double, val button: Int
) :
	FirmodEvent.Cancellable() {
	companion object : FirmodEventBus<HandledScreenClickEvent>()
}
