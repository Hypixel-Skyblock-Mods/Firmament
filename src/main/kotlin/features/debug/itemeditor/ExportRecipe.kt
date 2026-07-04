package moe.nea.firmod.features.debug.itemeditor

import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.entity.decoration.ArmorStand
import moe.nea.firmod.Firmod
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.HandledScreenKeyPressedEvent
import moe.nea.firmod.events.WorldKeyboardEvent
import moe.nea.firmod.features.debug.PowerUserTools
import moe.nea.firmod.repo.ItemNameLookup
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.SBData
import moe.nea.firmod.util.SHORT_NUMBER_FORMAT
import moe.nea.firmod.util.SkyblockId
import moe.nea.firmod.util.async.waitForTextInput
import moe.nea.firmod.util.ifDropLast
import moe.nea.firmod.util.mc.ScreenUtil.getSlotByIndex
import moe.nea.firmod.util.mc.accessor
import moe.nea.firmod.util.mc.displayNameAccordingToNbt
import moe.nea.firmod.util.mc.isEmpty
import moe.nea.firmod.util.mc.loreAccordingToNbt
import moe.nea.firmod.util.parseShortNumber
import moe.nea.firmod.util.red
import moe.nea.firmod.util.removeColorCodes
import moe.nea.firmod.util.skyBlockId
import moe.nea.firmod.util.skyblock.SkyBlockItems
import moe.nea.firmod.util.tr
import moe.nea.firmod.util.unformattedString
import moe.nea.firmod.util.useMatch

object ExportRecipe {


	val xNames = "123"
	val yNames = "ABC"

	val slotIndices = (0..<9).map {
		val x = it % 3
		val y = it / 3

		(yNames[y].toString() + xNames[x].toString()) to x + y * 9 + 10
	}
	val resultSlot = 25
	val craftingTableSlut = resultSlot - 2

	@Subscribe
	fun exportNpcLocation(event: WorldKeyboardEvent) {
		if (!event.matches(PowerUserTools.TConfig.exportNpcLocation)) {
			return
		}
		val entity = MC.instance.crosshairPickEntity
		if (entity == null) {
			MC.sendChat(tr("firmod.repo.export.npc.noentity", "Could not find entity to export"))
			return
		}
		Firmod.coroutineScope.launch {
			val guessName = entity.level.getEntitiesOfClass(
				ArmorStand::class.java,
				entity.boundingBox.inflate(0.1),
				{ !it.name.string.contains("CLICK") })
				.firstOrNull()?.customName?.string
				?: ""
			val reply = waitForTextInput("$guessName (NPC)", "Export stub")
			val id = generateName(reply)
			ItemExporter.exportStub(id, "§9$reply", entity)
			ItemExporter.modifyJson(id) {
				val mutJson = it.toMutableMap()
				mutJson["island"] = JsonPrimitive(SBData.skyblockLocation?.locrawMode ?: "unknown")
				mutJson["x"] = JsonPrimitive(entity.blockX)
				mutJson["y"] = JsonPrimitive(entity.blockY)
				mutJson["z"] = JsonPrimitive(entity.blockZ)
				JsonObject(mutJson)
			}
		}
	}

	@Subscribe
	fun onRecipeKeyBind(event: HandledScreenKeyPressedEvent) {
		if (!event.matches(PowerUserTools.TConfig.exportUIRecipes)) {
			return
		}
		if (event.screen !is ContainerScreen) return
		val title = event.screen.title.string
		val sellSlot = event.screen.getSlotByIndex(49, false)?.item?.accessor()
		val craftingTableSlot = event.screen.getSlotByIndex(craftingTableSlut, false)
		if (craftingTableSlot?.item?.accessor()?.displayNameAccordingToNbt?.unformattedString == "Crafting Table") {
			slotIndices.forEach { (_, index) ->
				event.screen.getSlotByIndex(index, false)?.item?.let(ItemExporter::ensureExported)
			}
			val inputs = slotIndices.associate { (name, index) ->
				val id = event.screen.getSlotByIndex(index, false)?.item?.accessor()?.takeIf { !it.isEmpty() }?.let {
					"${it.skyBlockId?.neuItem}:${it.count}"
				} ?: ""
				name to JsonPrimitive(id)
			}
			val output = event.screen.getSlotByIndex(resultSlot, false)?.item!!.accessor()
			val overrideOutputId = output.skyBlockId!!.neuItem
			val count = output.count
			val recipe = JsonObject(
				inputs + mapOf(
					"type" to JsonPrimitive("crafting"),
					"count" to JsonPrimitive(count),
					"overrideOutputId" to JsonPrimitive(overrideOutputId)
				)
			)
			ItemExporter.appendRecipe(output.skyBlockId!!, recipe)
			MC.sendChat(tr("firmod.repo.export.recipe", "Recipe for ${output.skyBlockId} exported."))
			return
		} else if (sellSlot?.displayNameAccordingToNbt?.string == "Sell Item" || (sellSlot?.loreAccordingToNbt
				?: listOf()).any { it.string == "Click to buyback!" } || title.endsWith("Shop")
		) {
			val shopId = SkyblockId(title.uppercase().replace(" ", "_") + "_NPC")
			if (!ItemExporter.isExported(shopId)) {
				ItemExporter.exportStub(shopId, "§9$title (NPC)", MC.instance.crosshairPickEntity)
			}
			for (index in (9..9 * 5)) {
				val item = event.screen.getSlotByIndex(index, false)?.item?.accessor() ?: continue
				val skyblockId = item.skyBlockId ?: continue
				val costLines = item.loreAccordingToNbt
					.map { it.string.trim() }
					.dropWhile { !it.startsWith("Cost") }
					.dropWhile { it == "Cost" }
					.takeWhile { it != "Click to trade!" }
					.takeWhile { it != "Stock" }
					.takeWhile { !it.startsWith("Annual Stock Year ") }
					.filter { !it.isBlank() }
					.map { it.removePrefix("Cost: ") }


				val costs = costLines.mapNotNull { lineText ->
					val line = findStackableItemByName(lineText)
					if (line == null) {
						MC.sendChat(
							tr(
								"firmod.repo.itemshop.fail",
								"Could not parse cost item ${lineText} for ${item.displayNameAccordingToNbt}"
							).red()
						)
					}
					line
				}


				ItemExporter.appendRecipe(
					shopId, JsonObject(
						mapOf(
							"type" to JsonPrimitive("npc_shop"),
							"cost" to JsonArray(costs.map { JsonPrimitive("${it.first.neuItem}:${it.second}") }),
							"result" to JsonPrimitive("${skyblockId.neuItem}:${item.count}"),
						)
					)
				)
			}
			MC.sendChat(tr("firmod.repo.export.itemshop", "Item Shop export for ${title} complete."))
		} else {
			MC.sendChat(tr("firmod.repo.export.recipe.fail", "No Recipe found"))
		}
	}

	private val coinRegex = "(?<amount>$SHORT_NUMBER_FORMAT) Coins?".toPattern()
	private val stackedItemRegex = "(?<name>.*) x(?<count>$SHORT_NUMBER_FORMAT)".toPattern()
	private val reverseStackedItemRegex = "(?<count>$SHORT_NUMBER_FORMAT)x (?<name>.*)".toPattern()
	private val essenceRegex = "(?<essence>.*) Essence x(?<count>$SHORT_NUMBER_FORMAT)".toPattern()
	private val numberedItemRegex = "(?<count>$SHORT_NUMBER_FORMAT) (?<what>.*)".toPattern()

	private val etherialRewardPattern = "\\+?(?<amount>${SHORT_NUMBER_FORMAT})x? (?<what>.*)".toPattern()

	fun findForName(name: String, fallbackToGenerated: Boolean = true): SkyblockId? {
		var id = ItemNameLookup.guessItemByName(name, true)
		if (id == null && fallbackToGenerated) {
			id = generateName(name)
		}
		return id
	}

	fun skill(name: String): SkyblockId {
		return SkyblockId("SKYBLOCK_SKILL_${name}")
	}

	fun generateName(name: String): SkyblockId {
		return SkyblockId(name.uppercase().replace(" ", "_").replace(Regex("[^A-Z_]+"), ""))
	}

	fun findStackableItemByName(name: String, fallbackToGenerated: Boolean = false): Pair<SkyblockId, Double>? {
		val properName = name.removeColorCodes().trim()
		if (properName == "FREE" || properName == "This Chest is Free!") {
			return Pair(SkyBlockItems.COINS, 0.0)
		}
		coinRegex.useMatch(properName) {
			return Pair(SkyBlockItems.COINS, parseShortNumber(group("amount")))
		}
		etherialRewardPattern.useMatch(properName) {
			val id = when (val id = group("what")) {
				"Copper" -> SkyblockId("SKYBLOCK_COPPER")
				"Bits" -> SkyblockId("SKYBLOCK_BIT")
				"Garden Experience" -> SkyblockId("SKYBLOCK_SKILL_GARDEN")
				"Farming XP" -> SkyblockId("SKYBLOCK_SKILL_FARMING")
				"Gold Essence" -> SkyblockId("ESSENCE_GOLD")
				"Gemstone Powder" -> SkyblockId("SKYBLOCK_POWDER_GEMSTONE")
				"Mithril Powder" -> SkyblockId("SKYBLOCK_POWDER_MITHRIL")
				"Pelts" -> SkyblockId("SKYBLOCK_PELT")
				"Pests" -> SkyblockId("SKYBLOCK_PEST")
				"Fine Flour" -> SkyblockId("FINE_FLOUR")
				else -> {
					id.ifDropLast(" Experience") {
						skill(generateName(it).neuItem)
					} ?: id.ifDropLast(" XP") {
						skill(generateName(it).neuItem)
					} ?: id.ifDropLast(" Powder") {
						SkyblockId("SKYBLOCK_POWDER_${generateName(it).neuItem}")
					} ?: id.ifDropLast(" Essence") {
						SkyblockId("ESSENCE_${generateName(it).neuItem}")
					} ?: generateName(id)
				}
			}
			return Pair(id, parseShortNumber(group("amount")))
		}
		essenceRegex.useMatch(properName) {
			return Pair(
				SkyblockId("ESSENCE_${group("essence").uppercase()}"),
				parseShortNumber(group("count"))
			)
		}
		stackedItemRegex.useMatch(properName) {
			val item = findForName(group("name"), fallbackToGenerated)
			if (item != null) {
				val count = parseShortNumber(group("count"))
				return Pair(item, count)
			}
		}
		reverseStackedItemRegex.useMatch(properName) {
			val item = findForName(group("name"), fallbackToGenerated)
			if (item != null) {
				val count = parseShortNumber(group("count"))
				return Pair(item, count)
			}
		}
		numberedItemRegex.useMatch(properName) {
			val item = findForName(group("what"), fallbackToGenerated)
			if (item != null) {
				val count = parseShortNumber(group("count"))
				return Pair(item, count)
			}
		}

		return findForName(properName, fallbackToGenerated)?.let { Pair(it, 1.0) }
	}

}
