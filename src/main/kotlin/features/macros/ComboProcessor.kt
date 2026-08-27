package moe.nea.firmod.features.macros

import kotlin.time.Duration.Companion.seconds
import net.minecraft.network.chat.Component
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.HudRenderEvent
import moe.nea.firmod.events.TickEvent
import moe.nea.firmod.events.WorldKeyboardEvent
import moe.nea.firmod.keybindings.SavedKeyBinding
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.TimeMark
import moe.nea.firmod.util.tr

object ComboProcessor {

	var rootTrie: Branch = Branch(mapOf())
		private set

	var activeTrie: Branch = rootTrie
		private set

	var isInputting = false
	var lastInput = TimeMark.farPast()
	val breadCrumbs = mutableListOf<SavedKeyBinding>()

	fun setActions(actions: List<ComboKeyAction>) {
		rootTrie = KeyComboTrie.fromComboList(actions)
		reset()
	}

	fun reset() {
		activeTrie = rootTrie
		lastInput = TimeMark.now()
		isInputting = false
		breadCrumbs.clear()
	}

	@Subscribe
	fun onTick(event: TickEvent) {
		if (isInputting && lastInput.passedTime() > 3.seconds)
			reset()
	}


	@Subscribe
	fun onRender(event: HudRenderEvent) {
		if (!isInputting) return
		if (!event.isRenderingHud) return
		event.context.pose().pushMatrix()
		val width = 120
		event.context.pose().translate(
			(MC.window.guiScaledWidth - width) / 2F,
			(MC.window.guiScaledHeight) / 2F + 8
		)
		val breadCrumbText = breadCrumbs.joinToString(" > ")
		event.context.text(
			MC.font,
			tr("firmod.combo.active", "Current Combo: ").append(breadCrumbText),
			0,
			0,
			-1,
			true
		)
		event.context.pose().translate(0F, MC.font.lineHeight + 2F)
		for ((key, value) in activeTrie.nodes) {
			event.context.text(
				MC.font,
				Component.literal("$breadCrumbText > $key: ").append(value.label),
				0,
				0,
				-1,
				true
			)
			event.context.pose().translate(0F, MC.font.lineHeight + 1F)
		}
		event.context.pose().popMatrix()
	}

	@Subscribe
	fun onKeyBinding(event: WorldKeyboardEvent) {
		val nextEntry = activeTrie.nodes.entries
			.find { event.matches(it.key) }
		if (nextEntry == null) {
			reset()
			return
		}
		event.cancel()
		breadCrumbs.add(nextEntry.key)
		lastInput = TimeMark.now()
		isInputting = true
		val value = nextEntry.value
		when (value) {
			is Branch -> {
				activeTrie = value
			}

			is Leaf -> {
				value.execute()
				reset()
			}
		}.let { }
	}
}
