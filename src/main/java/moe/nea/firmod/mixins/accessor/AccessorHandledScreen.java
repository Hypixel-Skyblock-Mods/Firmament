package moe.nea.firmod.mixins.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AccessorHandledScreen {
    @Accessor("hoveredSlot")
    @Nullable
	Slot getFocusedSlot_Firmod();

    @Accessor("imageWidth")
    int getBackgroundWidth_Firmod();

    @Accessor("imageWidth")
    void setBackgroundWidth_Firmod(int newBackgroundWidth);

    @Accessor("imageHeight")
    int getBackgroundHeight_Firmod();

    @Accessor("imageHeight")
    void setBackgroundHeight_Firmod(int newBackgroundHeight);

    @Accessor("leftPos")
    int getX_Firmod();

    @Accessor("leftPos")
    void setX_Firmod(int newX);

    @Accessor("topPos")
    int getY_Firmod();

    @Accessor("topPos")
    void setY_Firmod(int newY);

}
