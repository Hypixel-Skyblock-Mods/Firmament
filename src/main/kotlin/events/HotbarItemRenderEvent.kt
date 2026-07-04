

package moe.nea.firmod.events

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.DeltaTracker
import net.minecraft.world.item.ItemStack

data class HotbarItemRenderEvent(
    val item: ItemStack,
    val context: GuiGraphicsExtractor,
    val x: Int,
    val y: Int,
    val tickDelta: DeltaTracker,
) : FirmodEvent() {
    companion object : FirmodEventBus<HotbarItemRenderEvent>()
}
