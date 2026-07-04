package moe.nea.firmod.compat.rei

import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry
import me.shedaniel.rei.api.common.plugins.REICommonPlugin
import moe.nea.firmod.repo.RepoManager

class FirmodReiCommonPlugin : REICommonPlugin {
	override fun registerEntryTypes(registry: EntryTypeRegistry) {
		if (!RepoManager.shouldLoadREI()) return
		registry.register(FirmodReiPlugin.SKYBLOCK_ITEM_TYPE_ID, SBItemEntryDefinition)
	}
}
