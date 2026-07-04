package moe.nea.firmod.compat.sodium

import moe.nea.firmod.util.compatloader.CompatMeta
import moe.nea.firmod.util.compatloader.ICompatMeta
import net.fabricmc.loader.api.FabricLoader

@CompatMeta
object Compat : ICompatMeta {
	override fun shouldLoad(): Boolean {
		return FabricLoader.getInstance().isModLoaded("sodium")
	}
}
