package moe.nea.firmod.util.mc

import net.minecraft.resources.Identifier

interface IntrospectableItemModelManager {
	fun hasModel_firmod(identifier: Identifier): Boolean
}
