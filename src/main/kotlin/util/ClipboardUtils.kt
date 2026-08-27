

package moe.nea.firmod.util

import moe.nea.firmod.Firmod

object ClipboardUtils {
    fun setTextContent(string: String) {
        try {
            MC.keyboard.clipboard = string.ifEmpty { " " }
        } catch (e: Exception) {
            Firmod.logger.error("Could not write clipboard", e)
        }
    }

    fun getTextContents(): String {
        try {
            return MC.keyboard.clipboard ?: ""
        } catch (e: Exception) {
            Firmod.logger.error("Could not read clipboard", e)
            return ""
        }
    }
}
