package moe.nea.firmod.keybindings

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.put
import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.platform.MacosUtil
import net.minecraft.network.chat.Component
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.mc.InitLevel

@Serializable(with = GenericInputButton.Serializer::class)
sealed interface GenericInputButton {

	object Serializer : KSerializer<GenericInputButton> {
		override val descriptor: SerialDescriptor
			get() = SerialDescriptor("Firmod:GenericInputButton", JsonElement.serializer().descriptor)

		override fun serialize(
			encoder: Encoder,
			value: GenericInputButton
		) {
			JsonElement.serializer().serialize(
				encoder,
				when (value) {
					is KeyCodeButton -> buildJsonObject { put("keyCode", value.keyCode); put("backend", "sdl") }
					is MouseButton -> buildJsonObject { put("mouse", value.mouseButton); put("backend", "sdl") }
					is ScanCodeButton -> buildJsonObject { put("scanCode", value.scanCode); put("backend", "sdl") }
					Unbound -> JsonNull
				})
		}

		override fun deserialize(decoder: Decoder): GenericInputButton {
			val element = JsonElement.serializer().deserialize(decoder)
			if (element is JsonNull)
				return Unbound
			require(element is JsonObject)
			val sdl = (element["backend"] as? JsonPrimitive)?.content == "sdl"
			(element["keyCode"] as? JsonPrimitive)?.let {
				val key = if (sdl) it.int else LegacyInputMigration.key(it.int)
				return if (key == -1) Unbound else KeyCodeButton(key)
			}
			(element["mouse"] as? JsonPrimitive)?.let {
				return MouseButton(if (sdl) it.int else LegacyInputMigration.mouse(it.int))
			}
			(element["scanCode"] as? JsonPrimitive)?.let {
				// GLFW native scan codes differ between Windows, X11 and macOS and
				// have no portable SDL equivalent. Rebind rather than activate another key.
				return if (sdl) ScanCodeButton(it.int) else Unbound
			}
			error("Could not parse GenericInputButton: $element")
		}
	}

	companion object {

		fun of(event: KeyEvent) = ofKeyAndScan(event.input(), event.key)
		fun escape() = ofKeyCode(InputConstants.KEY_ESCAPE)
		fun ofKeyCode(keyCode: Int): GenericInputButton = KeyCodeButton(keyCode)
		fun ofScanCode(scanCode: Int): GenericInputButton = ScanCodeButton(scanCode)
		fun ofScanCodeFromKeyCode(keyCode: Int): GenericInputButton = ScanCodeButton(keyCode)
		fun unbound(): GenericInputButton = Unbound
		fun mouse(mouseButton: Int): GenericInputButton = MouseButton(mouseButton)
		fun ofKeyAndScan(keyCode: Int, scanCode: Int): GenericInputButton {
			if (keyCode == -1)
				return ofScanCode(scanCode)
			return ofKeyCode(keyCode) // TODO: should i always upgrade to a scanCode?
		}
	}

	data object Unbound : GenericInputButton {
		override fun toInputKey(): InputConstants.Key {
			return InputConstants.UNKNOWN
		}

		override fun isBound(): Boolean {
			return false
		}

		override fun isPressed(): Boolean {
			return false
		}
	}

	data class MouseButton(
		val mouseButton: Int
	) : GenericInputButton {
		override fun toInputKey(): InputConstants.Key {
			return InputConstants.Type.MOUSE.getOrCreate(mouseButton)
		}

		override fun isPressed(): Boolean {
			return org.lwjgl.sdl.SDLMouse.nSDL_GetMouseState(0L, 0L) and (1 shl (mouseButton - 1)) != 0
		}
	}

	data class KeyCodeButton(
		val keyCode: Int
	) : GenericInputButton {
		override fun toInputKey(): InputConstants.Key {
			return InputConstants.Type.KEYBOARD.getOrCreate(keyCode)
		}

		override fun isPressed(): Boolean {
			return InputConstants.isKeyDown(keyCode)
		}

		override fun isCtrl(): Boolean {
			return keyCode in InputModifiers.controlKeys
		}

		override fun isAlt(): Boolean {
			return keyCode in InputModifiers.altKeys
		}

		override fun isShift(): Boolean {
			return keyCode in InputModifiers.shiftKeys
		}

		override fun isSuper(): Boolean {
			return keyCode in InputModifiers.superKeys
		}
	}

	data class ScanCodeButton(
		val scanCode: Int
	) : GenericInputButton {
		override fun toInputKey(): InputConstants.Key {
			return InputConstants.Type.KEYBOARD.getOrCreate(scanCode)
		}

		override fun isPressed(): Boolean {
			return FirmodKeyboardState.isScancodeDown(scanCode)
		}
	}

	fun isBound() = true

	fun isModifier() = isCtrl() || isAlt() || isSuper() || isShift()
	fun isCtrl() = false
	fun isAlt() = false
	fun isSuper() = false
	fun isShift() = false

	fun toInputKey(): InputConstants.Key
	fun format(): Component =
		if (InitLevel.isAtLeast(InitLevel.RENDER_INIT)) {
			toInputKey().displayName
		} else {
			Component.nullToEmpty(toString())
		}

	fun matches(inputAction: GenericInputAction) = inputAction.matches(this)
	fun isPressed(): Boolean
}

sealed interface GenericInputAction {
	fun matches(inputButton: GenericInputButton): Boolean

	data class MouseInput(
		val mouseButton: Int
	) : GenericInputAction {
		override fun matches(inputButton: GenericInputButton): Boolean {
			return inputButton is GenericInputButton.MouseButton && inputButton.mouseButton == mouseButton
		}
	}

	data class KeyboardInput(
		val keyCode: Int,
		val scanCode: Int,
	) : GenericInputAction {
		override fun matches(inputButton: GenericInputButton): Boolean {
			return when (inputButton) {
				is GenericInputButton.KeyCodeButton -> inputButton.keyCode == keyCode
				is GenericInputButton.ScanCodeButton -> inputButton.scanCode == scanCode
				else -> false
			}
		}
	}

	companion object {
		@JvmStatic
		fun mouse(mouseButton: Int): GenericInputAction = MouseInput(mouseButton)

		@JvmStatic
		fun mouse(click: MouseButtonEvent): GenericInputAction = mouse(click.button())

		@JvmStatic
		fun of(input: net.minecraft.client.input.MouseButtonInfo): GenericInputAction = mouse(input.button)
		@JvmStatic
		fun of(input: KeyEvent): GenericInputAction = key(input.input(), input.key)

		@JvmStatic
		fun key(keyCode: Int, scanCode: Int): GenericInputAction = KeyboardInput(keyCode, scanCode)
	}
}

@Serializable(with = InputModifiers.Serializer::class)
data class InputModifiers(
	val modifiers: Int
) {
	object Serializer : KSerializer<InputModifiers> {
		override val descriptor = SerialDescriptor("Firmod:InputModifiers", JsonElement.serializer().descriptor)
		override fun serialize(encoder: Encoder, value: InputModifiers) {
			JsonElement.serializer().serialize(encoder, buildJsonObject {
				put("modifiers", value.modifiers)
				put("backend", "sdl")
			})
		}
		override fun deserialize(decoder: Decoder): InputModifiers {
			val element = JsonElement.serializer().deserialize(decoder) as JsonObject
			val flags = (element.getValue("modifiers") as JsonPrimitive).int
			val sdl = (element["backend"] as? JsonPrimitive)?.content == "sdl"
			return InputModifiers(if (sdl) flags else LegacyInputMigration.modifiers(flags))
		}
	}
	companion object {
		@JvmStatic
		fun current(): InputModifiers {
			val h = MC.window
			val ctrl = if (MacosUtil.IS_MACOS) {
				InputConstants.isKeyDown( InputConstants.KEY_LGUI)
					|| InputConstants.isKeyDown( InputConstants.KEY_RGUI)
			} else InputConstants.isKeyDown( InputConstants.KEY_LCONTROL)
				|| InputConstants.isKeyDown( InputConstants.KEY_RCONTROL)
			val shift = InputConstants.isKeyDown( InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(
				InputConstants.KEY_RSHIFT
			)
			val alt = InputConstants.isKeyDown( InputConstants.KEY_LALT)
				|| InputConstants.isKeyDown( InputConstants.KEY_RALT)
			val `super` = InputConstants.isKeyDown( InputConstants.KEY_LGUI)
				|| InputConstants.isKeyDown( InputConstants.KEY_RGUI)
			return of(
				ctrl = ctrl,
				shift = shift,
				alt = alt,
				`super` = `super`,
			)
		}


		val superKeys = listOf(InputConstants.KEY_LGUI, InputConstants.KEY_RGUI)
		val controlKeys = if (MacosUtil.IS_MACOS) {
			listOf(InputConstants.KEY_LGUI, InputConstants.KEY_RGUI)
		} else {
			listOf(InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL)
		}
		val shiftKeys = listOf(InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT)
		val altKeys = listOf(InputConstants.KEY_LALT, InputConstants.KEY_RALT)

		fun of(
			vararg useNamedArgs: Boolean,
			ctrl: Boolean = false,
			shift: Boolean = false,
			alt: Boolean = false,
			`super`: Boolean = false
		): InputModifiers {
			require(useNamedArgs.isEmpty())
			return InputModifiers(
				(if (ctrl) InputConstants.MOD_CONTROL else 0)
					or (if (shift) InputConstants.MOD_SHIFT else 0)
					or (if (alt) InputConstants.MOD_ALT else 0)
					or (if (`super`) InputConstants.MOD_SUPER else 0)
			)
		}

		fun ofKeyCodes(vararg keys: Int): InputModifiers {
			var mods = 0
			for (key in keys) {
				if (key in superKeys)
					mods = mods or InputConstants.MOD_SUPER
				if (key in controlKeys)
					mods = mods or InputConstants.MOD_CONTROL
				if (key in altKeys)
					mods = mods or InputConstants.MOD_ALT
				if (key in shiftKeys)
					mods = mods or InputConstants.MOD_SHIFT
			}
			return of(mods)
		}

		@JvmStatic
		fun of(modifiers: Int) = InputModifiers(modifiers)

		@JvmStatic
		fun of(input: InputWithModifiers) = InputModifiers(input.modifiers())

		fun none(): InputModifiers {
			return InputModifiers(0)
		}

		fun ofKey(button: GenericInputButton): InputModifiers {
			return when (button) {
				is GenericInputButton.KeyCodeButton -> ofKeyCodes(button.keyCode)
				else -> none()
			}
		}
	}

	fun isAtLeast(other: InputModifiers): Boolean {
		return this.modifiers and other.modifiers == this.modifiers
	}

	fun without(other: InputModifiers): InputModifiers {
		return InputModifiers(this.modifiers and other.modifiers.inv())
	}

	fun isEmpty() = modifiers == 0

	fun getFlag(flag: Int) = modifiers and flag != 0
	val ctrl get() = getFlag(InputConstants.MOD_CONTROL) // TODO: consult someone on control vs command again
	val shift get() = getFlag(InputConstants.MOD_SHIFT)
	val alt get() = getFlag(InputConstants.MOD_ALT)
	val `super` get() = getFlag(InputConstants.MOD_SUPER)

	override fun toString(): String {
		return listOfNotNull(
			if (ctrl) "CTRL" else null,
			if (shift) "SHIFT" else null,
			if (alt) "ALT" else null,
			if (`super`) "SUPER" else null,
		).joinToString(" + ")
	}

	fun matches(other: InputModifiers, atLeast: Boolean): Boolean {
		if (atLeast)
			return isAtLeast(other)
		return this == other
	}

	fun format(): Component { // TODO: translation for mods
		return Component.nullToEmpty(toString())
	}

}
