package moe.nea.firmod.gui.config

import com.google.auto.service.AutoService
import net.minecraft.client.gui.screens.Screen

@AutoService(FirmodConfigScreenProvider::class)
class BuiltInConfigScreenProvider : FirmodConfigScreenProvider {
    override val key: String
        get() = "builtin"

    override fun open(search: String?, parent: Screen?): Screen {
        return AllConfigsGui.makeBuiltInScreen(parent)
    }
}
