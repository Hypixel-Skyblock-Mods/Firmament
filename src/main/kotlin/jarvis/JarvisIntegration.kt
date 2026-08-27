

package moe.nea.firmod.jarvis

import moe.nea.jarvis.api.Jarvis
import moe.nea.jarvis.api.JarvisConfigOption
import moe.nea.jarvis.api.JarvisHud
import moe.nea.jarvis.api.JarvisPlugin
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import moe.nea.firmod.Firmod
import moe.nea.firmod.gui.config.HudMeta
import moe.nea.firmod.gui.config.HudMetaHandler
import moe.nea.firmod.gui.config.storage.FirmodConfigLoader
import moe.nea.firmod.util.data.ManagedConfig

class JarvisIntegration : JarvisPlugin {
    override fun getModId(): String =
        Firmod.MOD_ID

    companion object {
        lateinit var jarvis: Jarvis
    }

    override fun onInitialize(jarvis: Jarvis) {
        Companion.jarvis = jarvis
    }

    val configs
        get() = FirmodConfigLoader.allConfigs.filterIsInstance<ManagedConfig>()


    override fun getAllHuds(): List<JarvisHud> {
        return configs.flatMap { config ->
            config.sortedOptions.mapNotNull { if (it.handler is HudMetaHandler) it.value as HudMeta else null }
        }
    }

    override fun onHudEditorClosed() {
        configs.forEach { it.markDirty() }
    }

    override fun getAllConfigOptions(): List<JarvisConfigOption> {
        return configs.flatMap { config ->
            config.sortedOptions.map {
                object : JarvisConfigOption {
                    override fun title(): Component {
                        return it.labelText
                    }

                    override fun description(): List<Component> {
                        return emptyList()
                    }

                    override fun jumpTo(parentScreen: Screen?): Screen {
                        return config.getConfigEditor(parentScreen)
                    }
                }
            }
        }
    }
}
