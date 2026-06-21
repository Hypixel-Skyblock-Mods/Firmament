package moe.nea.firmament.util.mc

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import moe.nea.firmament.events.ComponentsLoadedEvent

@RequiresOptIn
annotation class RequiresComponents

class LazyItemStack {
	companion object {
		fun fromItemStack(itemStack: ItemStack) = LazyItemStack().also { it._itemStack = itemStack }
		fun fromTemplate(template: ItemStackTemplate?) = LazyItemStack().also { it._template = template }
		fun empty() = LazyItemStack()
		val CODEC =
			object : Codec<LazyItemStack> {
				override fun <T> encode(
					input: LazyItemStack,
					ops: DynamicOps<T>,
					prefix: T
				): DataResult<T> {
					return input.template()?.let {
						ItemStackTemplate.CODEC.encode(it, ops, prefix)
					} ?: DataResult.success(ops.emptyMap())
				}

				override fun <T> decode(
					ops: DynamicOps<T>,
					input: T
				): DataResult<Pair<LazyItemStack, T>> {
					return ops.getMap(input)
						.flatMap { map ->
							if (map.entries().findAny().isEmpty)
								DataResult.success(Pair(empty(), ops.empty()))
							else
								ItemStackTemplate.CODEC.decode(ops, input)
									.map { it.mapFirst(::fromTemplate) }
						}
				}
			}
	}

	private var _itemStack: ItemStack? = null
	private var _template: ItemStackTemplate? = null
	private var generation = -1

	fun template(): ItemStackTemplate? {
		if (_template == null)
			_template = _itemStack
				?.takeIf { !it.isEmpty }
				?.let { ItemStackTemplate.fromNonEmptyStack(it) }
		return _template
	}

	@RequiresComponents
	fun recreate(): ItemStack {
		return (_template?.create() ?: ItemStack.EMPTY)
			.also { this._itemStack = it }
	}

	@RequiresComponents
	fun upgrade(): ItemStack {
		tryDecay()
		return _itemStack ?: recreate()
	}

	fun tryDecay() {
		if (ComponentsLoadedEvent.generation != generation)
			decay()
	}


	fun decay() {
		template()
		_itemStack = null
	}
}

