package moe.nea.jarvis.impl;

import moe.nea.jarvis.api.JarvisConfigOption;
import moe.nea.jarvis.api.JarvisPlugin;

public record ConfigOptionWithCustody(JarvisPlugin plugin, JarvisConfigOption option) {
}
