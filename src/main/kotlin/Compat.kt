package moe.nea.firmod

import moe.nea.firmod.util.compatloader.CompatMeta
import moe.nea.firmod.util.compatloader.ICompatMeta

@CompatMeta
object Compat : ICompatMeta {
	override fun shouldLoad(): Boolean {
		return true
	}
}
