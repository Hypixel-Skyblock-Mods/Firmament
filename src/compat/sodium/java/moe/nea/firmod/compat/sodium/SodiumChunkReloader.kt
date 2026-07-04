package moe.nea.firmod.compat.sodium

import moe.nea.firmod.mixins.sodium.accessor.AccessorSodiumWorldRenderer
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer

class SodiumChunkReloader : Runnable {
    override fun run() {
        (SodiumWorldRenderer.instanceNullable() as? AccessorSodiumWorldRenderer)
            ?.renderSectionManager_firmod
            ?.markGraphDirty()
    }
}
