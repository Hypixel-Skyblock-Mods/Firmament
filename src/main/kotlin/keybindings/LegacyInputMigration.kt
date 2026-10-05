package moe.nea.firmod.keybindings

import com.mojang.blaze3d.platform.InputConstants

/** Preserves saved logical bindings when moving from GLFW to SDL in 26.3. */
internal object LegacyInputMigration {
    fun key(code: Int): Int = when (code) {
        in 65..90 -> code - 65 + InputConstants.KEY_A
        in 49..57 -> code - 49 + InputConstants.KEY_1
        48 -> InputConstants.KEY_0
        in 290..301 -> code - 290 + InputConstants.KEY_F1
        in 302..313 -> code - 302 + InputConstants.KEY_F13
        in 321..329 -> code - 321 + InputConstants.KEY_NUMPAD1
        320 -> InputConstants.KEY_NUMPAD0
        32 -> InputConstants.KEY_SPACE
        39 -> InputConstants.KEY_APOSTROPHE
        44 -> InputConstants.KEY_COMMA
        45 -> InputConstants.KEY_MINUS
        46 -> InputConstants.KEY_PERIOD
        47 -> InputConstants.KEY_SLASH
        59 -> InputConstants.KEY_SEMICOLON
        61 -> InputConstants.KEY_EQUALS
        91 -> InputConstants.KEY_LBRACKET
        92 -> InputConstants.KEY_BACKSLASH
        93 -> InputConstants.KEY_RBRACKET
        96 -> InputConstants.KEY_GRAVE
        256 -> InputConstants.KEY_ESCAPE
        257 -> InputConstants.KEY_RETURN
        258 -> InputConstants.KEY_TAB
        259 -> InputConstants.KEY_BACKSPACE
        260 -> InputConstants.KEY_INSERT
        261 -> InputConstants.KEY_DELETE
        262 -> InputConstants.KEY_RIGHT
        263 -> InputConstants.KEY_LEFT
        264 -> InputConstants.KEY_DOWN
        265 -> InputConstants.KEY_UP
        266 -> InputConstants.KEY_PAGEUP
        267 -> InputConstants.KEY_PAGEDOWN
        268 -> InputConstants.KEY_HOME
        269 -> InputConstants.KEY_END
        280 -> InputConstants.KEY_CAPSLOCK
        281 -> InputConstants.KEY_SCROLLLOCK
        282 -> InputConstants.KEY_NUMLOCK
        283 -> InputConstants.KEY_PRINTSCREEN
        284 -> InputConstants.KEY_PAUSE
        330 -> org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_KP_PERIOD
        331 -> org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_KP_DIVIDE
        332 -> InputConstants.KEY_MULTIPLY
        333 -> org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_KP_MINUS
        334 -> InputConstants.KEY_ADD
        335 -> InputConstants.KEY_NUMPADENTER
        336 -> InputConstants.KEY_NUMPADEQUALS
        340 -> InputConstants.KEY_LSHIFT
        341 -> InputConstants.KEY_LCONTROL
        342 -> InputConstants.KEY_LALT
        343 -> InputConstants.KEY_LGUI
        344 -> InputConstants.KEY_RSHIFT
        345 -> InputConstants.KEY_RCONTROL
        346 -> InputConstants.KEY_RALT
        347 -> InputConstants.KEY_RGUI
        else -> -1
    }

    fun mouse(button: Int): Int = when (button) {
        0 -> InputConstants.MOUSE_BUTTON_LEFT
        1 -> InputConstants.MOUSE_BUTTON_RIGHT
        2 -> InputConstants.MOUSE_BUTTON_MIDDLE
        else -> button + 1
    }

    fun modifiers(flags: Int): Int =
        (if (flags and 1 != 0) InputConstants.MOD_SHIFT else 0) or
        (if (flags and 2 != 0) InputConstants.MOD_CONTROL else 0) or
        (if (flags and 4 != 0) InputConstants.MOD_ALT else 0) or
        (if (flags and 8 != 0) InputConstants.MOD_SUPER else 0)
}
