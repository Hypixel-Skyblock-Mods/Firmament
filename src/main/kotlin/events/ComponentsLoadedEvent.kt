package moe.nea.firmod.events

import moe.nea.firmod.annotations.Subscribe

class ComponentsLoadedEvent : FirmodEvent() {
	companion object : FirmodEventBus<ComponentsLoadedEvent>() {
		var generation = -1

		@Subscribe
		fun onComponentsLoaded(event: ComponentsLoadedEvent) {
			generation++
		}
	}
}
