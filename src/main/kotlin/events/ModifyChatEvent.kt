

package moe.nea.firmod.events

import net.minecraft.network.chat.Component
import moe.nea.firmod.util.unformattedString

/**
 * Allow modification of a chat message before it is sent off to the user. Intended for display purposes.
 */
data class ModifyChatEvent(val originalText: Component) : FirmodEvent() {
    var unformattedString = originalText.unformattedString
        private set
    var replaceWith: Component = originalText
        set(value) {
            field = value
            unformattedString = value.unformattedString
        }

    companion object : FirmodEventBus<ModifyChatEvent>()
}
