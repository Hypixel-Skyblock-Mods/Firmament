package moe.nea.firmod.test

import net.minecraft.server.Bootstrap
import net.minecraft.SharedConstants
import net.minecraft.core.component.DataComponentInitializers
import net.minecraft.core.registries.BuiltInRegistries
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.TimeMark

object FirmTestBootstrap {
	val loadStart = TimeMark.now()

	init {
		println("Bootstrap started at $loadStart")
	}

	init {
		SharedConstants.tryDetectVersion()
		Bootstrap.bootStrap()
		BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(MC.currentOrDefaultRegistries).forEach { it.apply() }
	}

	val loadEnd = TimeMark.now()

	val loadDuration = loadStart.passedAt(loadEnd)

	init {
		println("Bootstrap completed at $loadEnd after $loadDuration")
	}

	@JvmStatic
	fun bootstrapMinecraft() {
	}
}
