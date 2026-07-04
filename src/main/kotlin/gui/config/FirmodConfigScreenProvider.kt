package moe.nea.firmod.gui.config

import net.minecraft.client.gui.screens.Screen
import moe.nea.firmod.util.compatloader.CompatLoader

interface FirmodConfigScreenProvider {
	val key: String
	val isEnabled: Boolean get() = true

	fun open(search: String?, parent: Screen?): Screen

	companion object : CompatLoader<FirmodConfigScreenProvider>(FirmodConfigScreenProvider::class) {
		val providers by lazy {
			allValidInstances
				.filter { it.isEnabled }
				.sortedWith(
					Comparator
						.comparing<FirmodConfigScreenProvider, Boolean>({ it.key == "builtin" })
						.reversed()
						.then(Comparator.comparing({ it.key }))
				).toList()
		}
	}
}
