

package moe.nea.firmod.events

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.inventory.Slot
import net.minecraft.resources.Identifier
import moe.nea.firmod.util.render.drawGuiTexture

interface SlotRenderEvents {
    val context: GuiGraphicsExtractor
    val slot: Slot

	fun highlight(sprite: Identifier) {
		context.drawGuiTexture(
			slot.x, slot.y, 0, 16, 16,
			sprite
		)
	}

    data class Before(
        override val context: GuiGraphicsExtractor, override val slot: Slot,
    ) : FirmodEvent(),
        SlotRenderEvents {
        companion object : FirmodEventBus<Before>()
    }

    data class After(
        override val context: GuiGraphicsExtractor, override val slot: Slot,
    ) : FirmodEvent(),
        SlotRenderEvents {
        companion object : FirmodEventBus<After>()
    }
}
