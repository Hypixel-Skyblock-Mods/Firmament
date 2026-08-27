package moe.nea.firmod.test.util.skyblock

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import moe.nea.firmod.test.testutil.ItemResources
import moe.nea.firmod.util.skyblock.SackUtil
import moe.nea.firmod.util.skyblock.SkyBlockItems

class SackUtilTest {
	@Test
	fun testOneRottenFlesh() {
		Assertions.assertEquals(
			listOf(
				SackUtil.SackUpdate(SkyBlockItems.ROTTEN_FLESH, "Rotten Flesh", 1)
			),
			SackUtil.getUpdatesFromMessage(ItemResources.loadText("sacks/gain-rotten-flesh"))
		)
	}

	@Test
	fun testAFewRegularItems() {
		Assertions.assertEquals(
			listOf(
				SackUtil.SackUpdate(SkyBlockItems.ROTTEN_FLESH, "Rotten Flesh", 1)
			),
			SackUtil.getUpdatesFromMessage(ItemResources.loadText("sacks/gain-and-lose-regular"))
		)
	}
}
