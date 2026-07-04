package moe.nea.firmod.events

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.Connection

data class ServerConnectedEvent(
    val connection: Connection
) : FirmodEvent() {
    companion object : FirmodEventBus<ServerConnectedEvent>() {
        init {
            ClientPlayConnectionEvents.INIT.register(ClientPlayConnectionEvents.Init { clientPlayNetworkHandler: ClientPacketListener, minecraftClient: Minecraft ->
                publishSync(ServerConnectedEvent(clientPlayNetworkHandler.connection))
            })
        }
    }
}
