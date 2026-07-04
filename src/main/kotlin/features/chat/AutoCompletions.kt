package moe.nea.firmod.features.chat

import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.BuiltInExceptions
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import kotlin.concurrent.thread
import net.minecraft.SharedConstants
import net.minecraft.commands.BrigadierExceptions
import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.commands.get
import moe.nea.firmod.commands.suggestsList
import moe.nea.firmod.commands.thenArgument
import moe.nea.firmod.commands.thenExecute
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.events.MaskCommands
import moe.nea.firmod.repo.RepoManager
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig
import moe.nea.firmod.util.tr

object AutoCompletions {

	@Config
	object TConfig : ManagedConfig(identifier, Category.CHAT) {
		val provideWarpTabCompletion by toggle("warp-complete") { true }
		val replaceWarpIsByWarpIsland by toggle("warp-is") { true }
	}

	val identifier: String
		get() = "auto-completions"

	@Subscribe
	fun onMaskCommands(event: MaskCommands) {
		if (TConfig.provideWarpTabCompletion) {
			event.mask("warp")
		}
	}

	@Subscribe
	fun onCommandEvent(event: CommandEvent) {
		if (!TConfig.provideWarpTabCompletion) return
		event.deleteCommand("warp")
		event.register("warp") {
			thenArgument("to", string()) { toArg ->
				suggestsList {
					RepoManager.neuRepo.constants?.islands?.warps?.flatMap { listOf(it.warp) + it.aliases } ?: listOf()
				}
				thenExecute {
					val warpName = get(toArg)
					if (warpName == "is" && TConfig.replaceWarpIsByWarpIsland) {
						MC.sendCommand("warp island")
					} else {
						redirectToServer()
					}
				}
			}
		}
	}

	fun CommandContext<*>.redirectToServer() {
		val message = tr(
			"firmod.warp.auto-complete.internal-throw",
			"This is an internal syntax exception that should not show up in gameplay, used to pass on a command to the server"
		)
		throw CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand(), message)
	}
}
