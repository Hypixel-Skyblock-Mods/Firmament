
package moe.nea.firmod.util

import me.shedaniel.math.Color
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.events.FirmodEvent
import moe.nea.firmod.events.FirmodEventBus

data class DurabilityBarEvent(
    val item: ItemStack,
) : FirmodEvent() {
    data class DurabilityBar(
        val color: Color,
        val percentage: Float,
    )

    var barOverride: DurabilityBar? = null

    companion object : FirmodEventBus<DurabilityBarEvent>()
}
