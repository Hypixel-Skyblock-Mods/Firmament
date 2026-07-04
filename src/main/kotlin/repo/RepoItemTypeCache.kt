package moe.nea.firmod.repo

import io.github.moulberry.repo.IReloadable
import io.github.moulberry.repo.NEURepository
import io.github.moulberry.repo.data.NEUItem
import moe.nea.firmod.util.skyblock.ItemType

object RepoItemTypeCache : IReloadable {

	var byItemType: Map<ItemType?, List<NEUItem>> = mapOf()

	override fun reload(repository: NEURepository) {
		byItemType = repository.items.items.values.groupBy { ItemType.fromEscapeCodeLore(it.lore.lastOrNull() ?: "") }
	}
}
