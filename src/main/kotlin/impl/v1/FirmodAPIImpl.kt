package moe.nea.firmod.impl.v1

import java.util.Collections
import java.util.Optional
import net.fabricmc.loader.api.FabricLoader
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.item.ItemStack
import moe.nea.firmod.Firmod
import moe.nea.firmod.api.v1.FirmodAPI
import moe.nea.firmod.api.v1.FirmodExtension
import moe.nea.firmod.api.v1.FirmodItemWidget
import moe.nea.firmod.features.items.recipes.ItemList
import moe.nea.firmod.repo.ExpensiveItemCacheApi
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.intoOptional
import moe.nea.firmod.util.mc.RequiresComponents

object FirmodAPIImpl : FirmodAPI() {
	@JvmField
	val INSTANCE: FirmodAPI = FirmodAPIImpl

	private val _extensions = mutableListOf<FirmodExtension>()
	override fun getExtensions(): List<FirmodExtension> {
		return Collections.unmodifiableList(_extensions)
	}

	@OptIn(ExpensiveItemCacheApi::class)
	override fun getHoveredItemWidget(): Optional<FirmodItemWidget> {
		val mouse = MC.instance.mouseHandler
		val window = MC.window
		val xpos = mouse.getScaledXPos(window)
		val ypos = mouse.getScaledYPos(window)
		val widget = MC.screen
			?.getChildAt(xpos, ypos)
			?.getOrNull()
		if (widget is FirmodItemWidget) return widget.intoOptional()
		val itemListStack = ItemList.findStackUnder(xpos.toInt(), ypos.toInt())
		if (itemListStack != null)
			return object : FirmodItemWidget {
				override fun getPlacement(): FirmodItemWidget.Placement {
					return FirmodItemWidget.Placement.ITEM_LIST
				}

				@RequiresComponents
				override fun getItemStack(): ItemStack {
					return itemListStack.second.asImmutableItemStack().upgrade()
				}

				override fun getSkyBlockId(): String {
					return itemListStack.second.skyblockId.neuItem
				}

			}.intoOptional()
		return Optional.empty()
	}

	fun loadExtensions() {
		for (container in FabricLoader.getInstance()
			.getEntrypointContainers(FirmodExtension.ENTRYPOINT_NAME, FirmodExtension::class.java)) {
			Firmod.logger.info("Loading extension ${container.entrypoint} from ${container.provider.metadata.name}")
			loadExtension(container.entrypoint)
		}
		extensions.forEach { it.onLoad() }
	}

	fun loadExtension(entrypoint: FirmodExtension) {
		_extensions.add(entrypoint)
	}
}
