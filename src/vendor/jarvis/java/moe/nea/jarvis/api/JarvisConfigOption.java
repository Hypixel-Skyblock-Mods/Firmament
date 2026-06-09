package moe.nea.jarvis.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * A config option in a Jarvis enabled mod.
 */
public interface JarvisConfigOption {
    /**
     * Test whether a string matches this config option.
     * This method does not necessarily return true if the {@link #title()} or {@link #description()} match, it is
     * intended for bonus search tags.
     *
     * @param phrase the phrase that is being searched for in this option
     * @return whether this config option matches the phrase
     */
    default boolean match(String phrase) {
        return false;
    }

    /**
     * @return the title of this option
     */
    @NotNull Component title();

    /**
     * @return the category of this option
     */
    default @Nullable Component category() {
        return null;
    }

    /**
     * @return the description of this option
     */
    @NotNull @Unmodifiable List<@NotNull Component> description();

    /**
     * Calling this function should return a screen for editing this config option. It may also jump to a page containing
     * multiple options, including this one, in which case it is recommended to scroll towards this option.
     *
     * @param parentScreen the screen that should be returned to once the returned screen is closed.
     */
    @NotNull Screen jumpTo(@Nullable Screen parentScreen);
}
