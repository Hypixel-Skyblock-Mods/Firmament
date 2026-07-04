
package moe.nea.firmod.events

import net.minecraft.world.entity.Entity

data class EntityDespawnEvent(
    val entity: Entity?, val entityId: Int,
    val reason: Entity.RemovalReason,
) : FirmodEvent() {
    companion object: FirmodEventBus<EntityDespawnEvent>()
}
