package moe.nea.firmod.events

class WorldReadyEvent : FirmodEvent() {
	companion object : FirmodEventBus<WorldReadyEvent>()
//	class FullyLoaded : FirmodEvent() {
//		companion object : FirmodEventBus<FullyLoaded>() {
//			 TODO: check WorldLoadingState
//		}
//	}
}
