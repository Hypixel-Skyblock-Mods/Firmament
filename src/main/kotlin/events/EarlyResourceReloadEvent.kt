
package moe.nea.firmod.events

import java.util.concurrent.Executor
import net.minecraft.server.packs.resources.ResourceManager

data class EarlyResourceReloadEvent(val resourceManager: ResourceManager, val preparationExecutor: Executor) :
    FirmodEvent() {
    companion object : FirmodEventBus<EarlyResourceReloadEvent>()
}
