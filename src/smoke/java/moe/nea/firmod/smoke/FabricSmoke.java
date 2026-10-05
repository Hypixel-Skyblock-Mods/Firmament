package moe.nea.firmod.smoke;

import java.util.Set;
import java.util.TreeSet;
import moe.nea.firmod.init.MixinPlugin;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

/** Applies dynamically discovered production mixins without initializing Minecraft. */
public final class FabricSmoke implements PreLaunchEntrypoint {
    @Override
    @SuppressWarnings("unchecked")
    public void onPreLaunch() {
        if (!Boolean.getBoolean("firmod.smoke")) throw new IllegalStateException("Development checks only");
        try {
            ClassLoader loader = getClass().getClassLoader();
            Set<String> targets = new TreeSet<>();
            Set<String> expected = new TreeSet<>();
            for (MixinPlugin plugin : MixinPlugin.instances) expected.addAll(plugin.getExpectedFullPathMixins());
            if (expected.isEmpty()) throw new AssertionError("No production mixins discovered");
            for (String mixin : expected) {
                ClassNode node = new ClassNode();
                try (var input = loader.getResourceAsStream(mixin.replace('.', '/') + ".class")) {
                    if (input == null) throw new AssertionError("Missing mixin " + mixin);
                    new ClassReader(input).accept(node, ClassReader.SKIP_CODE);
                }
                var annotations = new java.util.ArrayList<org.objectweb.asm.tree.AnnotationNode>();
                if (node.visibleAnnotations != null) annotations.addAll(node.visibleAnnotations);
                if (node.invisibleAnnotations != null) annotations.addAll(node.invisibleAnnotations);
                for (var annotation : annotations) {
                    if (!annotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) continue;
                    for (int i = 0; i < annotation.values.size(); i += 2) {
                        String key = (String) annotation.values.get(i);
                        if (key.equals("value")) for (Type type : (java.util.List<Type>) annotation.values.get(i + 1)) targets.add(type.getClassName());
                        if (key.equals("targets")) targets.addAll((java.util.List<String>) annotation.values.get(i + 1));
                    }
                }
            }
            for (String target : targets) {
                Class<?> transformed = Class.forName(target, false, loader);
                transformed.getDeclaredMethods();
                transformed.getDeclaredFields();
                System.out.println("FIRMOD_SMOKE_TRANSFORMED " + target);
            }
            Set<String> applied = new TreeSet<>();
            for (MixinPlugin plugin : MixinPlugin.instances) applied.addAll(plugin.getAppliedFullPathMixins());
            expected.removeAll(applied);
            if (!expected.isEmpty()) throw new AssertionError("Unapplied production mixins: " + expected);
            System.out.println("FIRMOD_SMOKE_PASSED " + targets.size() + " targets, " + applied.size() + " mixins");
            System.exit(0);
        } catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }
    }
}
