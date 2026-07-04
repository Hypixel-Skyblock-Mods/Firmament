package moe.nea.firmod.compat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import moe.nea.firmod.gui.config.AllConfigsGui

class FirmodModMenuPlugin : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { AllConfigsGui.makeScreen(parent = it) }
    }
}
