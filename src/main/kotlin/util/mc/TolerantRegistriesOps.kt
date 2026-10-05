package moe.nea.firmod.util.mc

import com.mojang.serialization.DynamicOps
import java.util.Optional
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.RegistryOps
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderGetter
import net.minecraft.core.HolderOwner

class TolerantRegistriesOps<T : Any>(
	delegate: DynamicOps<T>,
	registryInfoGetter: RegistryInfoLookup
) : RegistryOps<T>(delegate, registryInfoGetter) {
	constructor(delegate: DynamicOps<T>, registry: HolderLookup.Provider) :
		this(delegate, HolderLookupAdapter(registry))

	class TolerantGetter<E : Any>(delegate: HolderGetter<E>) : HolderGetter<E> by delegate {
		override fun canSerialize(other: HolderOwner<E>): Boolean {
			return true
		}
	}

	override fun <E : Any> getter(registryRef: ResourceKey<out Registry<out E>>): Optional<HolderGetter<E>> {
		return super.getter(registryRef).map {
			TolerantGetter(it)
		}
	}
}
