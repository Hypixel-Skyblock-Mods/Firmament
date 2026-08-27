package moe.nea.firmod.features.debug

import net.minecraft.network.chat.Component
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.commands.thenExecute
import moe.nea.firmod.commands.thenLiteral
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.events.SoundReceiveEvent
import moe.nea.firmod.events.WorldReadyEvent
import moe.nea.firmod.events.WorldRenderLastEvent
import moe.nea.firmod.util.red
import moe.nea.firmod.util.render.RenderInWorldContext

object SoundVisualizer {

	var showSounds = false

	var sounds = mutableListOf<SoundReceiveEvent>()


	@Subscribe
	fun onSubCommand(event: CommandEvent.SubCommand) {
		event.subcommand(DeveloperFeatures.DEVELOPER_SUBCOMMAND) {
			thenLiteral("sounds") {
				thenExecute {
					showSounds = !showSounds
					if (!showSounds) {
						sounds.clear()
					}
				}
			}
		}
	}

	@Subscribe
	fun onWorldSwap(event: WorldReadyEvent) {
		sounds.clear()
	}

	@Subscribe
	fun onRender(event: WorldRenderLastEvent) {
		RenderInWorldContext.renderInWorld(event) {
			sounds.forEach { event ->
				withFacingThePlayer(event.position) {
					text(
						Component.literal(event.sound.value().location.toString()).also {
							if (event.cancelled)
								it.red()
						},
						verticalAlign = RenderInWorldContext.VerticalAlign.CENTER,
					)
				}
			}
		}
	}

	@Subscribe
	fun onSoundReceive(event: SoundReceiveEvent) {
		if (!showSounds) return
		if (sounds.size > 1000) {
			sounds.subList(0, 200).clear()
		}
		sounds.add(event)
	}
}
