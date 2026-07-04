
package moe.nea.firmod.util

import moe.nea.firmod.repo.HypixelStaticData

enum class BazaarPriceStrategy {
    BUY_ORDER,
    SELL_ORDER,
    NPC_SELL;

    fun getSellPrice(skyblockId: SkyblockId): Double {
        val bazaarEntry = HypixelStaticData.bazaarData[skyblockId.asBazaarStock] ?: return 0.0
        return when (this) {
            BUY_ORDER -> bazaarEntry.quickStatus.sellPrice
            SELL_ORDER -> bazaarEntry.quickStatus.buyPrice
            NPC_SELL -> TODO()
        }
    }
}
