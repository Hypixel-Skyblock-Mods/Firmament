

package moe.nea.firmod.events

import net.minecraft.network.protocol.Packet

data class OutgoingPacketEvent(val packet: Packet<*>) : FirmodEvent.Cancellable() {
    companion object : FirmodEventBus<OutgoingPacketEvent>()
}
