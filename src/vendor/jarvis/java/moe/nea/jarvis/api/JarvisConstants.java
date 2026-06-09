package moe.nea.jarvis.api;

public class JarvisConstants {
    public static final String MODID = "jarviscci";
    /**
     * Use with Forge IMC to register your plugin. This is not useful on fabric, use the {@code jarvis} entrypoint instead.
     * <p>
     * {@code InterModComms.sendTo(JarvisConstants.MODID, JarvisConstants.IMC_METHOD, () -> MyPlugin.class);}
     */
    public static final String IMC_REGISTER_PLUGIN = "registerPlugin";
}
