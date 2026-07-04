

package moe.nea.firmod.util.accessors

import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import moe.nea.firmod.mixins.accessor.AccessorHandledScreen

fun AbstractContainerScreen<*>.getProperRectangle(): Rectangle {
    this.castAccessor()
    return Rectangle(
        getX_Firmod(),
        getY_Firmod(),
        getBackgroundWidth_Firmod(),
        getBackgroundHeight_Firmod()
    )
}
