package moe.nea.jarvis.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;

public class JarvisUtil {

    public static boolean isTest = Boolean.getBoolean("jarvis.test");

    public static double coerce(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static int lerp(int start, int end, double progress) {
        return (int) ((end - start) * progress + start);
    }

    public static double lerp(double start, double end, double progress) {
        return (end - start) * progress + start;
    }

    public static void drawOutlineTrans(
        GuiGraphicsExtractor g, int l, int t, int r, int b, int col
    ) {
        var tl = g.pose().transformPosition(new Vector2f(l, t));
        var br = g.pose().transformPosition(new Vector2f(r, b));
        g.outline((int) (tl.x), (int) (tl.y), (int) (br.x - tl.x), (int) (br.y - tl.y), col);
    }

    public static Color lerpColor(Color startC, Color endC, double progress) {
        return new Color(
            lerp(startC.getRed(), endC.getRed(), progress),
            lerp(startC.getGreen(), endC.getGreen(), progress),
            lerp(startC.getBlue(), endC.getBlue(), progress),
            lerp(startC.getAlpha(), endC.getAlpha(), progress)
        );
    }
}
