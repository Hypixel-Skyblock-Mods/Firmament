package moe.nea.jarvis.impl;

import java.awt.*;

public class BinaryInterpolator {
    private final double universeLength;
    private double progressStart = 1;
    private long intervalStart = 0L;
    private double progressEnd = 0;

    public BinaryInterpolator(double universeLength) {
        this.universeLength = universeLength;
    }


    public double getCurrentProgress() {
        return JarvisUtil.coerce(JarvisUtil.lerp(progressStart, progressEnd, (System.currentTimeMillis() - intervalStart) / universeLength), 0, 1);
    }

    public void lerpTo(double target) {
        progressStart = getCurrentProgress();
        intervalStart = System.currentTimeMillis();
        progressEnd = target;
    }

    public Color lerp(Color start, Color end) {
        return JarvisUtil.lerpColor(start, end, getCurrentProgress());
    }
}
