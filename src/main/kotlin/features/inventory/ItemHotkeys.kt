package moe.nea.firmod.features.inventory

import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.HandledScreenKeyPressedEvent
import moe.nea.firmod.repo.ExpensiveItemCacheApi
import moe.nea.firmod.repo.HypixelStaticData
import moe.nea.firmod.repo.ItemCache.asItemStack
import moe.nea.firmod.repo.ItemCache.isBroken
import moe.nea.firmod.repo.RepoManager
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.asBazaarStock
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig
import moe.nea.firmod.util.focusedItemStack
import moe.nea.firmod.util.mc.LazyItemStack
import moe.nea.firmod.util.mc.RequiresComponents
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.mc.lazy
import moe.nea.firmod.util.skyBlockId
import moe.nea.firmod.util.skyblock.SBItemUtil.getSearchName

object ItemHotkeys {
	@Config
	object TConfig : ManagedConfig("item-hotkeys", Category.INVENTORY) {
		val openGlobalTradeInterface by keyBindingWithDefaultUnbound("global-trade-interface")
	}

	@OptIn(ExpensiveItemCacheApi::class, RequiresComponents::class)
	@Subscribe
	fun onHandledInventoryPress(event: HandledScreenKeyPressedEvent) {
		if (!event.matches(TConfig.openGlobalTradeInterface)) {
			return
		}
		var item = event.screen.focusedItemStack?.lazy() ?: return
		val skyblockId = item.skyBlockId ?: return
		item = RepoManager.getNEUItem(skyblockId)?.asItemStack()?.takeIf { !it.isBroken } ?: item
		if (HypixelStaticData.hasBazaarStock(skyblockId.asBazaarStock)) {
			MC.sendCommand("bz ${item.getSearchName()}")
		} else if (HypixelStaticData.hasAuctionHouseOffers(skyblockId)) {
			MC.sendCommand("ahs ${item.getSearchName()}")
		} else {
			return
		}
		event.cancel()
	}

}
