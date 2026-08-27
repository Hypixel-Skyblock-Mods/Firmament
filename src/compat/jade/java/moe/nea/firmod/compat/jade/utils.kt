package moe.nea.firmod.compat.jade

import moe.nea.firmod.util.SBData

fun isOnMiningIsland(): Boolean =
	SBData.skyblockLocation?.hasCustomMining ?: false
