package moe.nea.firmod.events

data class WorldMouseMoveEvent(val deltaX: Double, val deltaY: Double) : FirmodEvent.Cancellable() {
	companion object : FirmodEventBus<WorldMouseMoveEvent>()
}
