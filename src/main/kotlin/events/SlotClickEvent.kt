
package moe.nea.firmod.events

import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

data class SlotClickEvent(
    val slot: Slot,
    val stack: ItemStack,
    val button: Int,
    val actionType: ContainerInput,
) : FirmodEvent() {
    companion object : FirmodEventBus<SlotClickEvent>()
}
