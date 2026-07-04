package moe.nea.firmod.compat.jade

import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.SkyblockServerUpdateEvent
import moe.nea.firmod.repo.MiningRepoData
import moe.nea.firmod.repo.RepoManager
import moe.nea.firmod.util.ErrorUtil
import net.minecraft.world.level.block.Block
import moe.nea.firmod.events.ReloadRegistrationEvent
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig

object JadeIntegration {
	@Config
	object TConfig : ManagedConfig("jade-integration", Category.INTEGRATIONS) {
		val miningProgress by toggle("progress") { true }
		val blockDetection by toggle("blocks") { true }
	}

	var customBlocks: Map<Block, MiningRepoData.CustomMiningBlock> = mapOf()

	fun refreshBlockInfo() {
		if (!isOnMiningIsland()) {
			customBlocks = mapOf()
			return
		}
		val blocks = RepoManager.miningData.customMiningBlocks
			.flatMap { customBlock ->
				// TODO: add a lifted helper method for this
				customBlock.blocks189.filter { it.isCurrentlyActive }
					.mapNotNull { it.block }
					.map { customBlock to it }
			}
			.groupBy { it.second }
		customBlocks = blocks.mapNotNull { (block, customBlocks) ->
			val singleMatch =
				ErrorUtil.notNullOr(customBlocks.singleOrNull()?.first,
				                    "Two custom blocks both want to supply custom mining behaviour for $block.") { return@mapNotNull null }
			block to singleMatch
		}.toMap()
	}

	@Subscribe
	fun onRepoReload(event: ReloadRegistrationEvent) {
		event.repo.registerReloadListener { refreshBlockInfo() }
	}

	@Subscribe
	fun onWorldSwap(event: SkyblockServerUpdateEvent) {
		refreshBlockInfo()
	}
}
