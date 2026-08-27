package moe.nea.firmod.features.world

import moe.nea.firmod.annotations.Subscribe
import moe.nea.firmod.commands.thenExecute
import moe.nea.firmod.events.CommandEvent
import moe.nea.firmod.events.ReloadRegistrationEvent
import moe.nea.firmod.util.MoulConfigUtils
import moe.nea.firmod.util.ScreenUtil

object NPCWaypoints {

    var allNpcWaypoints = listOf<NavigableWaypoint>()

    @Subscribe
    fun onRepoReloadRegistration(event: ReloadRegistrationEvent) {
        event.repo.registerReloadListener {
            allNpcWaypoints = it.items.items.values
                .asSequence()
                .filter { !it.island.isNullOrBlank() }
                .map {
                    NavigableWaypoint.NPCWaypoint(it)
                }
                .toList()
        }
    }

    @Subscribe
    fun onOpenGui(event: CommandEvent.SubCommand) {
        event.subcommand("npcs") {
            thenExecute {
                ScreenUtil.setScreenLater(MoulConfigUtils.loadScreen(
                    "npc_waypoints",
                    NpcWaypointGui(allNpcWaypoints),
                    null))
            }
        }
    }


}
