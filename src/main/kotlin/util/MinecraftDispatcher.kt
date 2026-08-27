

package moe.nea.firmod.util

import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.client.Minecraft

val MinecraftDispatcher by lazy { Minecraft.getInstance().asCoroutineDispatcher() }
