package moe.nea.firmod.gui.config

import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.xml.Bind
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.commands.RestArgumentType
import moe.nea.firmod.commands.get
import moe.nea.firmod.commands.thenArgument
import moe.nea.firmod.commands.thenExecute
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.MoulConfigUtils
import moe.nea.firmod.util.ScreenUtil.setScreenLater
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig

object AllConfigsGui {
//
//	val allConfigs
//		get() = listOf(
//			RepoManager.Config
//		) + FeatureManager.allFeatures.mapNotNull { it.config }

	@Config
	object ConfigConfig : ManagedConfig("configconfig", Category.META) {
		val enableYacl by toggle("enable-yacl") { false }
		val enableMoulConfig by toggle("enable-moulconfig") { true }
		val enableWideMC by toggle("wide-moulconfig") { false }
	}

	fun <T> List<T>.toObservableList(): ObservableList<T> = ObservableList(this)

	class CategoryMapping(val category: ManagedConfig.Category) {
		@get:Bind("configs")
		val configs = category.configs.map { EntryMapping(it) }.toObservableList()

		@Bind
		fun name() = category.labelText

		@Bind
		fun close() {
			MC.screen?.onClose()
		}

		class EntryMapping(val config: ManagedConfig) {
			@Bind
			fun name() = Component.translatable("firmod.config.${config.name}")

			@Bind
			fun openEditor() {
				config.showConfigEditor(MC.screen)
			}
		}
	}

	class CategoryView {
		@get:Bind("categories")
		val categories = ManagedConfig.Category.entries
			.map { CategoryEntry(it) }
			.toObservableList()

		class CategoryEntry(val category: ManagedConfig.Category) {
			@Bind
			fun name() = category.labelText

			@Bind
			fun open() {
				MC.screen = MoulConfigUtils.loadScreen("config/category", CategoryMapping(category), MC.screen)
			}
		}
	}

	fun makeBuiltInScreen(parent: Screen? = null): Screen {
		return MoulConfigUtils.loadScreen("config/main", CategoryView(), parent)
	}

	fun makeScreen(search: String? = null, parent: Screen? = null): Screen {
		val wantedKey = when {
			ConfigConfig.enableMoulConfig -> "moulconfig"
			ConfigConfig.enableYacl -> "yacl"
			else -> "builtin"
		}
		val provider = FirmodConfigScreenProvider.providers.find { it.key == wantedKey }
			?: FirmodConfigScreenProvider.providers.first()
		return provider.open(search, parent)
	}

	fun showAllGuis() {
		setScreenLater(makeScreen())
	}

	@Subscribe
	fun registerCommands(event: CommandEvent.SubCommand) {
		event.subcommand("search") {
			thenArgument("search", RestArgumentType) { search ->
				thenExecute {
					val search = this[search]
					setScreenLater(makeScreen(search = search))
				}
			}
		}
	}

}
