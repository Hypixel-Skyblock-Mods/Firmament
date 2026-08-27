

package moe.nea.firmod.events

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenForegroundEvent(
    val screen: AbstractContainerScreen<*>,
    val context: GuiGraphicsExtractor,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : FirmodEvent() {
    companion object : FirmodEventBus<HandledScreenForegroundEvent>()
}
