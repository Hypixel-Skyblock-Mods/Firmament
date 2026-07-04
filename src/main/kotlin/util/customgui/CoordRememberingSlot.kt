
package moe.nea.firmod.util.customgui

import net.minecraft.world.inventory.Slot

interface CoordRememberingSlot {
    fun rememberCoords_firmod()
    fun restoreCoords_firmod()
    fun getOriginalX_firmod(): Int
    fun getOriginalY_firmod(): Int
}

val Slot.originalX get() = (this as CoordRememberingSlot).getOriginalX_firmod()
val Slot.originalY get() = (this as CoordRememberingSlot).getOriginalY_firmod()
