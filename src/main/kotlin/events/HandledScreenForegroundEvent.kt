

package moe.nea.firmament.events

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenForegroundEvent(
    val screen: AbstractContainerScreen<*>,
    val context: GuiGraphicsExtractor,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : FirmamentEvent() {
    companion object : FirmamentEventBus<HandledScreenForegroundEvent>()
}
