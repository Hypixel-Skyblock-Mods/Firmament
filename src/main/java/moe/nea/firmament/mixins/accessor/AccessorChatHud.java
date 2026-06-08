package moe.nea.firmament.mixins.accessor;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface AccessorChatHud {
	@Accessor("allMessages")
	List<GuiMessage> getMessages_firmament();

	@Accessor("trimmedMessages")
	List<GuiMessage.Line> getVisibleMessages_firmament();

	@Accessor("chatScrollbarPos")
	int getScrolledLines_firmament();
}
