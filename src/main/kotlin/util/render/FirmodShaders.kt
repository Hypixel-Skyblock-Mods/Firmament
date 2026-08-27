package moe.nea.firmod.util.render

import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.events.DebugInstantiateEvent

object FirmodShaders {

	@Subscribe
	fun debugLoad(event: DebugInstantiateEvent) {
		// TODO: do i still need to work with shaders like this?
	}
}
