
package moe.nea.firmod.events

import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

data class AttackBlockEvent(
    val player: Player,
    val world: Level,
    val hand: InteractionHand,
    val blockPos: BlockPos,
    val direction: Direction
) : FirmodEvent.Cancellable() {
    companion object : FirmodEventBus<AttackBlockEvent>()
}
