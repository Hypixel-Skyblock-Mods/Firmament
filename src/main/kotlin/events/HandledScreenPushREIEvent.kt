

package moe.nea.firmod.events

import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenPushREIEvent(
    val screen: AbstractContainerScreen<*>,
    val rectangles: MutableList<Rectangle> = mutableListOf()
) : FirmodEvent() {

    fun block(rectangle: Rectangle) {
        rectangles.add(rectangle)
    }

    companion object : FirmodEventBus<HandledScreenPushREIEvent>()
}
