package moe.nea.firmod.features.debug.itemeditor

import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.io.path.writeText
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.ClientAsset
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import moe.nea.firmod.Firmod
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.commands.RestArgumentType
import moe.nea.firmod.commands.get
import moe.nea.firmod.commands.thenArgument
import moe.nea.firmod.commands.thenExecute
import moe.nea.firmod.commands.thenLiteral
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.events.HandledScreenKeyPressedEvent
import moe.nea.firmod.events.SlotRenderEvents
import moe.nea.firmod.features.debug.DeveloperFeatures
import moe.nea.firmod.features.debug.ExportedTestConstantMeta
import moe.nea.firmod.features.debug.PowerUserTools
import moe.nea.firmod.repo.RepoDownloadManager
import moe.nea.firmod.repo.RepoManager
import moe.nea.firmod.util.LegacyTagParser
import moe.nea.firmod.util.LegacyTagWriter.Companion.toLegacyString
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.SkyblockId
import moe.nea.firmod.util.focusedItemStack
import moe.nea.firmod.util.mc.LazyItemStack
import moe.nea.firmod.util.mc.SNbtFormatter.Companion.toPrettyString
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.mc.displayNameAccordingToNbt
import moe.nea.firmod.util.mc.isItem
import moe.nea.firmod.util.mc.loreAccordingToNbt
import moe.nea.firmod.util.mc.setSkullOwner
import moe.nea.firmod.util.mc.toNbtList
import moe.nea.firmod.util.render.drawGuiTexture
import moe.nea.firmod.util.setSkyBlockId
import moe.nea.firmod.util.skyBlockId
import moe.nea.firmod.util.tr
import moe.nea.firmod.util.unformattedString

object ItemExporter {

	fun exportItem(itemStack: ItemStack): Component = exportItem(LazyItemStack.fromItemStack(itemStack))
	fun exportItem(itemStack: LazyItemStack): Component {
		nonOverlayCache.clear()
		val exporter = LegacyItemExporter.createExporter(itemStack)
		var json = exporter.exportJson()
		val fileName = json.jsonObject["internalname"]?.jsonPrimitive?.takeIf { it.isString }?.content
		if (fileName == null) {
			return tr(
				"firmod.repoexport.nointernalname",
				"Could not find internal name to export for this item (null.json)"
			)
		}
		val itemFile = RepoDownloadManager.repoSavedLocation.resolve("items").resolve("${fileName}.json")
		itemFile.createParentDirectories()
		if (itemFile.exists()) {
			val existing = try {
				Firmod.json.decodeFromString<JsonObject>(itemFile.readText())
			} catch (ex: Exception) {
				ex.printStackTrace()
				JsonObject(mapOf())
			}
			val mut = json.jsonObject.toMutableMap()
			for (prop in existing) {
				if (prop.key !in mut || mut[prop.key]!!.let {
						(it is JsonPrimitive && it.content.isEmpty())
							|| (it is JsonArray && it.isEmpty())
							|| (it is JsonObject && it.isEmpty())
					})
					mut[prop.key] = prop.value
			}
			json = JsonObject(mut)
		}
		val jsonFormatted = Firmod.twoSpaceJson.encodeToString(json)
		itemFile.writeText(jsonFormatted)
		val overlayFile = RepoDownloadManager.repoSavedLocation.resolve("itemsOverlay")
			.resolve(ExportedTestConstantMeta.current.dataVersion.toString())
			.resolve("${fileName}.snbt")
		overlayFile.createParentDirectories()
		overlayFile.writeText(exporter.exportModernSnbt().toPrettyString())
		return tr(
			"firmod.repoexport.success",
			"Exported item to ${itemFile.relativeTo(RepoDownloadManager.repoSavedLocation)}${
				exporter.warnings.joinToString(
					""
				) { "\nWarning: $it" }
			}"
		)
	}

	fun pathFor(skyBlockId: SkyblockId) =
		RepoManager.neuRepo.baseFolder.resolve("items/${skyBlockId.neuItem}.json")

	fun isExported(skyblockId: SkyblockId) =
		pathFor(skyblockId).exists()

	fun ensureExported(itemStack: ItemStack) {
		if (!isExported(itemStack.accessor().skyBlockId ?: return))
			MC.sendChat(exportItem(LazyItemStack.fromItemStack(itemStack)))
	}

	fun modifyJson(skyblockId: SkyblockId, modify: (JsonObject) -> JsonObject) {
		val oldJson = Firmod.json.decodeFromString<JsonObject>(pathFor(skyblockId).readText())
		val newJson = modify(oldJson)
		pathFor(skyblockId).writeText(Firmod.twoSpaceJson.encodeToString(JsonObject(newJson)))
	}

	fun appendRecipe(skyblockId: SkyblockId, recipe: JsonObject) {
		modifyJson(skyblockId) { oldJson ->
			val mutableJson = oldJson.toMutableMap()
			val recipes = ((mutableJson["recipes"] as JsonArray?) ?: listOf()).toMutableList()
			recipes.add(recipe)
			mutableJson["recipes"] = JsonArray(recipes)
			JsonObject(mutableJson)
		}
	}

	@Subscribe
	fun onCommand(event: CommandEvent.SubCommand) {
		event.subcommand(DeveloperFeatures.DEVELOPER_SUBCOMMAND) {
			thenLiteral("reexportlore") {
				thenArgument("itemid", RestArgumentType) { itemid ->
					suggests { ctx, builder ->
						val spaceIndex = builder.remaining.lastIndexOf(" ")
						val (before, after) =
							if (spaceIndex < 0) Pair("", builder.remaining)
							else Pair(
								builder.remaining.substring(0, spaceIndex + 1),
								builder.remaining.substring(spaceIndex + 1)
							)
						RepoManager.neuRepo.items.items.keys
							.asSequence()
							.filter { it.startsWith(after, ignoreCase = true) }
							.forEach {
								builder.suggest(before + it)
							}

						builder.buildFuture()
					}
					thenExecute {
						for (itemid in get(itemid).split(" ").map { SkyblockId(it) }) {
							if (pathFor(itemid).notExists()) {
								MC.sendChat(
									tr(
										"firmod.repo.export.relore.fail",
										"Could not find json file to relore for ${itemid}"
									)
								)
							}
							fixLoreNbtFor(itemid)
							MC.sendChat(
								tr(
									"firmod.repo.export.relore",
									"Updated lore / display name for $itemid"
								)
							)
						}
					}
				}
				thenLiteral("all") {
					thenExecute {
						var i = 0
						val chunkSize = 100
						val items = RepoManager.neuRepo.items.items.keys
						Firmod.coroutineScope.launch {
							items.chunked(chunkSize).forEach { key ->
								MC.sendChat(
									tr(
										"firmod.repo.export.relore.progress",
										"Updated lore / display for ${i * chunkSize} / ${items.size}."
									)
								)
								i++
								key.forEach {
									fixLoreNbtFor(SkyblockId(it))
								}
							}
							MC.sendChat(tr("firmod.repo.export.relore.alldone", "All lores updated."))
						}
					}
				}
			}
		}
	}

	fun fixLoreNbtFor(itemid: SkyblockId) {
		modifyJson(itemid) {
			val mutJson = it.toMutableMap()
			val legacyTag = LegacyTagParser.parse(mutJson["nbttag"]!!.jsonPrimitive.content)
			val display = legacyTag.getCompoundOrEmpty("display")
			legacyTag.put("display", display)
			display.putString("Name", mutJson["displayname"]!!.jsonPrimitive.content)
			display.put(
				"Lore",
				(mutJson["lore"] as JsonArray).map { StringTag.valueOf(it.jsonPrimitive.content) }
					.toNbtList()
			)
			mutJson["nbttag"] = JsonPrimitive(legacyTag.toLegacyString())
			JsonObject(mutJson)
		}
	}

	@Subscribe
	fun onKeyBind(event: HandledScreenKeyPressedEvent) {
		if (!event.matches(PowerUserTools.TConfig.exportItemStackToRepo)) return

		val itemStack = event.screen.focusedItemStack ?: return
		val displayName = itemStack.displayName?.string ?: return
		val skyblockID = itemStack.accessor().skyBlockId.toString()
		val vanillaItem = itemStack.item
		val lore = itemStack.accessor().loreAccordingToNbt

		val warn = { reason: String ->
			MC.sendChat(
				tr(
					"firmod.repo.modified.item",
					"§cThis Item could be modified ($reason§c), please make sure to export a default item."
				)
			)
		}

		if (displayName.isBlank()) return

		if (itemStack.count > 1) {
			warn("item count above 1")
			return
		}
		if (skyblockID.contains("SACK") || skyblockID.contains("FISHING_NET")) {
			warn("modified by attributes")
		}
		if (vanillaItem is AxeItem && lore.any {
				it.unformattedString.contains("Damage") || it.unformattedString.contains("Strength") }) {
			warn("modified by essence perk")
		}

		PowerUserTools.lastCopiedStack = itemStack to exportItem(itemStack)
	}

	val nonOverlayCache = mutableMapOf<SkyblockId, Boolean>()

	@Subscribe
	fun onRender(event: SlotRenderEvents.Before) {
		if (!PowerUserTools.TConfig.highlightNonOverlayItems) {
			return
		}
		val stack = event.slot.item ?: return
		val id = event.slot.item.accessor().skyBlockId?.neuItem
		if (PowerUserTools.TConfig.dontHighlightSemicolonItems && id != null && id.contains(";")) return
		val sbId = stack.accessor().skyBlockId ?: return
		val isExported = nonOverlayCache.getOrPut(sbId) {
			RepoManager.overlayData.getOverlayFiles(sbId).isNotEmpty() || // This extra case is here so that an export works immediately, without repo reload
				RepoDownloadManager.repoSavedLocation.resolve("itemsOverlay")
					.resolve(ExportedTestConstantMeta.current.dataVersion.toString())
					.resolve("${stack.accessor().skyBlockId}.snbt")
					.exists()
		}
		if (!isExported)
			event.context.drawGuiTexture(
				Firmod.identifier("selected_pet_background"),
				event.slot.x, event.slot.y, 16, 16,
			)
	}

	fun getItemForEntity(entity: Entity?): Item {
		if (entity == null) return Items.BARRIER
		if (entity is AbstractClientPlayer) return Items.PLAYER_HEAD
		return entity.pickResult?.item ?: Items.BARRIER
	}

	fun exportStub(skyblockId: SkyblockId, title: String, entity: Entity?) {
		val exportText = exportItem(LazyItemStack.build(getItemForEntity(entity)) {
			displayNameAccordingToNbt = Component.literal(title)
			loreAccordingToNbt = listOf(Component.literal(""))
			setSkyBlockId(skyblockId)
			if (isItem(Items.PLAYER_HEAD)) {
				val playerEntity = entity as? AbstractClientPlayer
				val textureUrl = (playerEntity?.skin?.body as? ClientAsset.DownloadedTexture)?.url
				if (textureUrl != null)
					setSkullOwner(playerEntity.uuid, textureUrl)
			}
		})
		MC.sendChat(exportText)
		MC.sendChat(tr("firmod.repo.export.stub", "Exported a stub item for $skyblockId"))
	}
}
