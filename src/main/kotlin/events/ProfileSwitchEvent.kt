package moe.nea.firmod.events

import java.util.UUID

data class ProfileSwitchEvent(val oldProfile: UUID?, val newProfile: UUID?) : FirmodEvent() {
	companion object : FirmodEventBus<ProfileSwitchEvent>()
}
