package moe.nea.firmod.events

import io.github.moulberry.repo.NEURepository

data class ReloadRegistrationEvent(val repo: NEURepository) : FirmodEvent() {
    companion object : FirmodEventBus<ReloadRegistrationEvent>()
}
