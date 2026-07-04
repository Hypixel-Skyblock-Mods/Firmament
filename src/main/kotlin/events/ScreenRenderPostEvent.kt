

package moe.nea.firmod.events

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen

data class ScreenRenderPostEvent(
    val screen: Screen,
    val mouseX: Int,
    val mouseY: Int,
    val tickDelta: Float,
    val drawContext: GuiGraphicsExtractor
) : FirmodEvent() {
    companion object : FirmodEventBus<ScreenRenderPostEvent>()
}
