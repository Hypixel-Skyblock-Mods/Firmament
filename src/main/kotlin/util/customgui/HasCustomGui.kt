
package moe.nea.firmod.util.customgui

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

@Suppress("FunctionName")
interface HasCustomGui {
    fun getCustomGui_Firmod(): CustomGui?
    fun setCustomGui_Firmod(gui: CustomGui?)
}

var <T : AbstractContainerScreen<*>> T.customGui: CustomGui?
    get() = (this as HasCustomGui).getCustomGui_Firmod()
    set(value) {
        (this as HasCustomGui).setCustomGui_Firmod(value)
    }

