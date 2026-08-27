package moe.nea.firmod.compat.jade

import net.fabricmc.loader.api.FabricLoader
import moe.nea.firmod.util.compatloader.CompatMeta
import moe.nea.firmod.util.compatloader.ICompatMeta

@CompatMeta
object Compat : ICompatMeta {
	override fun shouldLoad(): Boolean {
		return FabricLoader.getInstance().isModLoaded("jade")
	}
}
