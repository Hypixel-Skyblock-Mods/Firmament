package moe.nea.firmod.events

data class PartyMessageReceivedEvent(
	val from: ProcessChatEvent,
	val message: String,
	val name: String,
) : FirmodEvent() {
	companion object : FirmodEventBus<PartyMessageReceivedEvent>()
}
