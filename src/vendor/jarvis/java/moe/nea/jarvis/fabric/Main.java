package moe.nea.jarvis.fabric;

import moe.nea.jarvis.api.JarvisPlugin;
import moe.nea.jarvis.impl.JarvisContainer;
import moe.nea.jarvis.impl.JarvisUtil;
import moe.nea.jarvis.impl.LoaderSupport;
import moe.nea.jarvis.impl.test.TestPluginClass;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class Main implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        List<JarvisPlugin> jarvisPlugins = FabricLoader.getInstance().getEntrypoints("jarvis", JarvisPlugin.class);
        JarvisContainer container = JarvisContainer.init(new LoaderSupport() {
            @Override
            public Optional<Component> getModName(String modid) {
                return FabricLoader.getInstance().getModContainer(modid).map(it -> Component.literal(it.getMetadata().getName()));
            }
        });
        container.plugins.addAll(jarvisPlugins);
        var hudKeybind = KeyMappingHelper.registerKeyMapping(container.hudKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hudKeybind.consumeClick()) {
                container.hudKeyBindingPressed();
            }
        });
        if (!JarvisUtil.isTest)
            container.plugins.removeIf(it -> it instanceof TestPluginClass);
        container.finishLoading();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            container.registerCommands(dispatcher);
        });
    }
}
