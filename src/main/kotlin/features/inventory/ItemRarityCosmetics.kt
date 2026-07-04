package moe.nea.firmod.features.inventory

import java.awt.Color
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.HotbarItemRenderEvent
import moe.nea.firmod.events.SlotRenderEvents
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.skyblock.Rarity

object ItemRarityCosmetics {
	val identifier: String
		get() = "item-rarity-cosmetics"

	@Config
	object TConfig : ManagedConfig(identifier, Category.INVENTORY) {
		val showItemRarityBackground by toggle("background") { false }
		val showItemRarityInHotbar by toggle("background-hotbar") { false }
	}

	private val rarityToColor = Rarity.colourMap.mapValues {
		val c = Color(it.value.color!!)
		c.rgb
	}

	fun drawItemStackRarity(drawContext: GuiGraphicsExtractor, x: Int, y: Int, item: ItemStack) {
		val rarity = Rarity.fromItem(item.accessor()) ?: return
		val rgb = rarityToColor[rarity] ?: 0xFF00FF80.toInt()
		drawContext.blitSprite(
			RenderPipelines.GUI_TEXTURED,
			Identifier.parse("firmod:item_rarity_background"),
			x, y,
			16, 16,
			rgb
		)
	}


	@Subscribe
	fun onRenderSlot(it: SlotRenderEvents.Before) {
		if (!TConfig.showItemRarityBackground) return
		val stack = it.slot.item ?: return
		drawItemStackRarity(it.context, it.slot.x, it.slot.y, stack)
	}

	@Subscribe
	fun onRenderHotbarItem(it: HotbarItemRenderEvent) {
		if (!TConfig.showItemRarityInHotbar) return
		val stack = it.item
		drawItemStackRarity(it.context, it.x, it.y, stack)
	}
}
