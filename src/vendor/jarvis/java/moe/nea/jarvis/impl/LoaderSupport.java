package moe.nea.jarvis.impl;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface LoaderSupport {
    Optional<Component> getModName(String modid);
}
