package moe.nea.firmod

import com.google.gson.Gson
import com.mojang.brigadier.CommandDispatcher
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.ModMetadata
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlin.coroutines.EmptyCoroutineContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.resources.Identifier
import moe.nea.firmod.commands.registerFirmodCommand
import moe.nea.firmod.events.ClientInitEvent
import moe.nea.firmod.events.ClientStartedEvent
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.events.ItemTooltipEvent
import moe.nea.firmod.events.ScreenRenderPostEvent
import moe.nea.firmod.events.TickEvent
import moe.nea.firmod.events.registration.registerFirmodEvents
import moe.nea.firmod.features.FeatureManager
import moe.nea.firmod.gui.config.storage.FirmodConfigLoader
import moe.nea.firmod.impl.v1.FirmodAPIImpl
import moe.nea.firmod.repo.HypixelStaticData
import moe.nea.firmod.repo.RepoManager
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.SBData
import moe.nea.firmod.util.mc.InitLevel
import moe.nea.firmod.util.tr

object Firmod {
	val modContainer by lazy { FabricLoader.getInstance().getModContainer(MOD_ID).get() }
	const val MOD_ID = "firmmod"
	const val RESOURCE_NS = "firmod"

	val DEBUG = System.getProperty("firmod.debug") == "true"
	val DATA_DIR: Path = Path.of(".firmod").also { Files.createDirectories(it) }
	val logger: Logger = LogManager.getLogger("Firmod")
	private val metadata: ModMetadata by lazy {
		FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata
	}
	val version: Version by lazy { metadata.version }

	private val DEFAULT_JSON_INDENT = "    "

	@OptIn(ExperimentalSerializationApi::class)
	val json = Json {
		prettyPrint = DEBUG
		isLenient = true
		allowTrailingComma = true
		allowComments = true
		ignoreUnknownKeys = true
		encodeDefaults = true
		prettyPrintIndent = if (prettyPrint) "\t" else DEFAULT_JSON_INDENT
	}

	/**
	 * FUCK two space indentation
	 */
	val twoSpaceJson = Json(from = json) {
		prettyPrint = true
		prettyPrintIndent = "  "
	}
	val gson = Gson()
	val tightJson = Json(from = json) {
		prettyPrint = false
		// Reset pretty print indent back to default to prevent getting yelled at by json
		prettyPrintIndent = DEFAULT_JSON_INDENT
		explicitNulls = false
	}

	val globalJob = Job()
	val coroutineScope =
		CoroutineScope(EmptyCoroutineContext + CoroutineName("Firmod")) + SupervisorJob(globalJob)

	private fun registerCommands(
		dispatcher: CommandDispatcher<FabricClientCommandSource>,
		@Suppress("UNUSED_PARAMETER")
		ctx: CommandBuildContext
	) {
		registerFirmodCommand(dispatcher, ctx)
		CommandEvent.publish(CommandEvent(dispatcher, ctx, MC.networkHandler?.commands))
	}

	@JvmStatic
	fun onInitialize() {
	}

	@JvmStatic
	fun onClientInitialize() {
		InitLevel.bump(InitLevel.MC_INIT)
		FeatureManager.subscribeEvents()
		FirmodConfigLoader.loadConfig()
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { instance ->
			TickEvent.publish(TickEvent(MC.currentTick++))
		})
		RepoManager.initialize()
		SBData.init()
		HypixelStaticData.spawnDataCollectionLoop()
		ClientCommandRegistrationCallback.EVENT.register(this::registerCommands)
		ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted {
			ClientStartedEvent.publish(ClientStartedEvent())
		})
		ClientLifecycleEvents.CLIENT_STOPPING.register(ClientLifecycleEvents.ClientStopping {
			logger.info("Shutting down Firmod coroutines")
			globalJob.cancel()
		})
		registerFirmodEvents()
		FirmodAPIImpl.loadExtensions()
		ItemTooltipCallback.EVENT.register { stack, context, type, lines ->
			ItemTooltipEvent.publish(ItemTooltipEvent(stack, context, type, lines))
		}
		ScreenEvents.AFTER_INIT.register(ScreenEvents.AfterInit { client, screen, scaledWidth, scaledHeight ->
			ScreenEvents.afterExtract(screen)
				.register(ScreenEvents.AfterExtract { screen, drawContext, mouseX, mouseY, tickDelta ->
					ScreenRenderPostEvent.publish(ScreenRenderPostEvent(screen, mouseX, mouseY, tickDelta, drawContext))
				})
		})
		ClientInitEvent.publish(ClientInitEvent())
		ResourceLoader.registerBuiltinPack(
			identifier("transparent_overlay"),
			modContainer,
			tr("firmod.resourcepack.transparentoverlay", "Transparent Firmod Overlay"),
			PackActivationType.NORMAL
		)
	}


	fun identifier(path: String) = Identifier.fromNamespaceAndPath(RESOURCE_NS, path)
	inline fun <reified T : Any> tryDecodeJsonFromStream(inputStream: InputStream): Result<T> {
		return runCatching {
			json.decodeFromStream<T>(inputStream)
		}
	}
}
