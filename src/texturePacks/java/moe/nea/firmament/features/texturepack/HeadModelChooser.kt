package moe.nea.firmament.features.texturepack

import com.google.gson.JsonObject
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.Optional
import org.joml.Matrix4fc
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object HeadModelChooser {
	val IS_CHOOSING_HEAD_MODEL = ThreadLocal.withInitial { false }

	interface HasExplicitHeadModelMarker {
		fun markExplicitHead_Firmament()
		fun isExplicitHeadModel_Firmament(): Boolean

		companion object {
			@JvmStatic
			fun cast(state: ItemStackRenderState) = state as HasExplicitHeadModelMarker
		}
	}

	data class Baked(val head: ItemModel, val regular: ItemModel) : ItemModel {

		override fun update(
			state: ItemStackRenderState,
			stack: ItemStack,
			resolver: ItemModelResolver,
			displayContext: ItemDisplayContext,
			world: ClientLevel?,
			heldItemContext: ItemOwner?,
			seed: Int
		) {
			val instance =
				if (IS_CHOOSING_HEAD_MODEL.get()) {
					HasExplicitHeadModelMarker.cast(state).markExplicitHead_Firmament()
					head
				} else {
					regular
				}
			instance.update(state, stack, resolver, displayContext, world, heldItemContext, seed)
		}
	}

	data class Unbaked(
		val head: ItemModel.Unbaked,
		val regular: ItemModel.Unbaked,
	) : ItemModel.Unbaked {
		override fun type(): MapCodec<out ItemModel.Unbaked> {
			return CODEC
		}

		override fun bake(
			context: ItemModel.BakingContext,
			transformation: Matrix4fc
		): ItemModel {
			return Baked(
				head.bake(context, transformation),
				regular.bake(context, transformation)
			)
		}

		override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
			head.resolveDependencies(resolver)
			regular.resolveDependencies(resolver)
		}

		companion object {
			@JvmStatic
			fun fromLegacyJson(jsonObject: JsonObject, unbakedModel: ItemModel.Unbaked): ItemModel.Unbaked {
				val model = jsonObject["firmament:head_model"] ?: return unbakedModel
				val modelUrl = model.asJsonPrimitive.asString
				val headModel = CuboidItemModelWrapper.Unbaked(Identifier.parse(modelUrl), Optional.empty(), listOf())
				return Unbaked(headModel, unbakedModel)
			}

			val CODEC = RecordCodecBuilder.mapCodec {
				it.group(
					ItemModels.CODEC.fieldOf("head")
						.forGetter(Unbaked::head),
					ItemModels.CODEC.fieldOf("regular")
						.forGetter(Unbaked::regular),
				).apply(it, ::Unbaked)
			}
		}
	}
}
