package moe.nea.firmod.util.accessors

import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.multiplayer.chat.GuiMessage
import moe.nea.firmod.mixins.accessor.AccessorChatHud

val ChatComponent.messages: MutableList<GuiMessage>
	get() = (this as AccessorChatHud).messages_firmod
