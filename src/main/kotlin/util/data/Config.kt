package moe.nea.firmod.util.data

import moe.nea.firmod.util.compatloader.CompatLoader

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Config(val prefix: String = "")


interface IConfigProvider {
	val configs: List<IDataHolder<*>>
	companion object {
		val providers = CompatLoader(IConfigProvider::class)
	}
}
