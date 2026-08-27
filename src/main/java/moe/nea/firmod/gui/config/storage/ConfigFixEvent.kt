package moe.nea.firmod.gui.config.storage

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.firmod.events.FirmodEvent
import moe.nea.firmod.events.FirmodEventBus

data class ConfigFixEvent(
	val storageClass: ConfigStorageClass,
	val toVersion: Int,
	var data: JsonObject,
) : FirmodEvent() {
	companion object : FirmodEventBus<ConfigFixEvent>() {

	}
	fun on(
		toVersion: Int,
		storageClass: ConfigStorageClass,
		block: ConfigEditor.() -> Unit
	) {
		require(toVersion <= FirmodConfigLoader.currentConfigVersion)
		if (this.toVersion == toVersion && this.storageClass == storageClass) {
			block(ConfigEditor(listOf(object : JsonPointer {
				override fun get(): JsonObject {
					return data
				}

				override fun set(value: JsonElement) {
					data = value as JsonObject
				}

				override fun toString(): String {
					return "ConfigRoot($storageClass)"
				}
			})))
		}
	}
}
