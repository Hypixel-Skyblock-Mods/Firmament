package moe.nea.firmod.features.inventory

import java.net.URI
import net.fabricmc.loader.api.FabricLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import net.minecraft.SharedConstants
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import moe.nea.firmod.Firmod
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.commands.thenExecute
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.events.SkyblockServerUpdateEvent
import moe.nea.firmod.repo.RepoManager
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.SBData
import moe.nea.firmod.util.aqua
import moe.nea.firmod.util.bold
import moe.nea.firmod.util.clickCommand
import moe.nea.firmod.util.grey
import moe.nea.firmod.util.lime
import moe.nea.firmod.util.red
import moe.nea.firmod.util.white
import moe.nea.firmod.util.yellow

object REIDependencyWarner {
	val reiModId = "roughlyenoughitems"
	val hasREI = FabricLoader.getInstance().isModLoaded(reiModId)
	var sentWarning = false

	fun modrinthLink(slug: String) =
		"https://modrinth.com/mod/$slug/versions?g=${SharedConstants.getCurrentVersion().name()}&l=fabric"

	fun downloadButton(modName: String, modId: String, slug: String): Component {
		val alreadyDownloaded = FabricLoader.getInstance().isModLoaded(modId)
		return Component.literal(" - ")
			.white()
			.append(Component.literal("[").aqua())
			.append(Component.translatable("firmod.download", modName)
				        .withStyle { it.withClickEvent(ClickEvent.OpenUrl(URI (modrinthLink(slug)))) }
				        .yellow()
				        .also {
					        if (alreadyDownloaded)
						        it.append(Component.translatable("firmod.download.already", modName)
							                  .lime())
				        })
			.append(Component.literal("]").aqua())
	}

	@Subscribe
	fun checkREIDependency(event: SkyblockServerUpdateEvent) {
		if (!SBData.isOnSkyblock) return
		if (!RepoManager.TConfig.warnForMissingItemListMod) return
		if (hasREI) return
		if (sentWarning) return
		sentWarning = true
		Firmod.coroutineScope.launch {
			delay(2.seconds)
			// TODO: should we offer an automatic install that actually downloads the JARs and places them into the mod folder?
			MC.sendChat(
				Component.translatable("firmod.reiwarning").red().bold().append("\n")
					.append(downloadButton("RoughlyEnoughItems", reiModId, "rei")).append("\n")
					.append(downloadButton("Architectury API", "architectury", "architectury-api")).append("\n")
					.append(downloadButton("Cloth Config API", "cloth-config", "cloth-config")).append("\n")
					.append(Component.translatable("firmod.reiwarning.disable")
						        .clickCommand("/firm disablereiwarning")
						        .grey())
			)
		}
	}

	@Subscribe
	fun onSubcommand(event: CommandEvent.SubCommand) {
		if (hasREI) return
		event.subcommand("disablereiwarning") {
			thenExecute {
				RepoManager.TConfig.warnForMissingItemListMod = false
				RepoManager.TConfig.markDirty()
				MC.sendChat(Component.translatable("firmod.reiwarning.disabled").yellow())
			}
		}
	}
}
