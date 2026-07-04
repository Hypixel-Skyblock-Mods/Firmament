package moe.nea.firmod.features.misc

import util.render.CustomRenderPipelines
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import net.minecraft.client.player.AbstractClientPlayer
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.world.entity.player.PlayerSkin
import net.minecraft.core.ClientAsset
import net.minecraft.resources.Identifier
import moe.nea.firmod.Firmod
import moe.nea.firmod.util.MC
import moe.nea.firmod.util.TimeMark
import moe.nea.firmod.util.data.Config
import moe.nea.firmod.util.data.ManagedConfig
import moe.nea.firmod.util.mc.CustomRenderPassHelper

object CustomCapes {
	val identifier: String
		get() = "developer-capes"

	@Config
	object TConfig : ManagedConfig(identifier, Category.DEV) {
		val showCapes by toggle("show-cape") { true }
	}

	interface CustomCapeRenderer {
		fun replaceRender(
            renderLayer: RenderType,
            vertexConsumerProvider: MultiBufferSource,
            matrixStack: PoseStack,
            model: (VertexConsumer) -> Unit
		)
	}

	data class TexturedCapeRenderer(
		val location: Identifier
	) : CustomCapeRenderer {
		override fun replaceRender(
            renderLayer: RenderType,
            vertexConsumerProvider: MultiBufferSource,
            matrixStack: PoseStack,
            model: (VertexConsumer) -> Unit
		) {
//			model(vertexConsumerProvider.getBuffer(RenderType.entitySolid(location)))
		}
	}

	data class ParallaxedHighlightCapeRenderer(
        val template: Identifier,
        val background: Identifier,
        val overlay: Identifier,
        val animationSpeed: Duration,
	) : CustomCapeRenderer {
		override fun replaceRender(
            renderLayer: RenderType,
            vertexConsumerProvider: MultiBufferSource,
            matrixStack: PoseStack,
            model: (VertexConsumer) -> Unit
		) {
			val animationValue = (startTime.passedTime() / animationSpeed).mod(1F)
			CustomRenderPassHelper(
				{ "Firmod Cape Renderer" },
				renderLayer.mode(),
				renderLayer.format(),
				MC.instance.mainRenderTarget,
				true,
			).use { renderPass ->
				renderPass.setPipeline(CustomRenderPipelines.PARALLAX_CAPE_SHADER)
				renderPass.setAllDefaultUniforms()
				renderPass.setUniform("Animation", 4) {
					it.putFloat(animationValue.toFloat())
				}
				renderPass.bindSampler("Sampler0", template)
				renderPass.bindSampler("Sampler1", background)
				renderPass.bindSampler("Sampler3", overlay)
				renderPass.uploadVertices(2048, model)
				renderPass.draw()
			}
		}
	}

	interface CapeStorage {
		companion object {
			@JvmStatic
			fun cast(playerEntityRenderState: AvatarRenderState) =
				playerEntityRenderState as CapeStorage

		}

		var cape_firmod: CustomCape?
	}

	data class CustomCape(
		val id: String,
		val label: String,
		val render: CustomCapeRenderer,
	)

	enum class AllCapes(val label: String, val render: CustomCapeRenderer) {
		FIRMAMENT_ANIMATED(
			"Animated Firmod", ParallaxedHighlightCapeRenderer(
				Firmod.identifier("textures/cape/parallax_template.png"),
				Firmod.identifier("textures/cape/parallax_background.png"),
				Firmod.identifier("textures/cape/firmod_star.png"),
				110.seconds
			)
		),
		UNPLEASANT_GRADIENT(
			"unpleasant_gradient",
			TexturedCapeRenderer(Firmod.identifier("textures/cape/unpleasant_gradient.png"))
		),
		FURFSKY_STATIC(
			"FurfSky",
			TexturedCapeRenderer(Firmod.identifier("textures/cape/fsr_static.png"))
		),

		FIRMAMENT_STATIC(
			"Firmod",
			TexturedCapeRenderer(Firmod.identifier("textures/cape/firm_static.png"))
		),
		HYPIXEL_PLUS(
			"Hypixel+",
			TexturedCapeRenderer(Firmod.identifier("textures/cape/h_plus.png"))
		),
		;

		val cape = CustomCape(name, label, render)
	}

	val byId = AllCapes.entries.associateBy { it.cape.id }
	val byUuid =
		listOf(
			listOf(
				Devs.nea to AllCapes.UNPLEASANT_GRADIENT,
				Devs.kath to AllCapes.FIRMAMENT_STATIC,
				Devs.jani to AllCapes.FIRMAMENT_ANIMATED,
				Devs.nat to AllCapes.FIRMAMENT_ANIMATED,
				Devs.HPlus.ic22487 to AllCapes.HYPIXEL_PLUS,
			),
			Devs.FurfSky.all.map { it to AllCapes.FURFSKY_STATIC },
		).flatten().flatMap { (dev, cape) -> dev.uuids.map { it to cape.cape } }.toMap()

	@JvmStatic
	fun addCapeData(
        player: AbstractClientPlayer,
        playerEntityRenderState: AvatarRenderState
	) {
		if (true) return // TODO: see capefeaturerenderer mixin
		val cape = if (TConfig.showCapes) byUuid[player.uuid] else null
		val capeStorage = CapeStorage.cast(playerEntityRenderState)
		if (cape == null) {
			capeStorage.cape_firmod = null
		} else {
			capeStorage.cape_firmod = cape
			playerEntityRenderState.skin = PlayerSkin(
				playerEntityRenderState.skin.body,
				ClientAsset.ResourceTexture(Firmod.identifier("placeholder/fake_cape"), Firmod.identifier("placeholder/fake_cape")),
				playerEntityRenderState.skin.elytra,
				playerEntityRenderState.skin.model,
				playerEntityRenderState.skin.secure,
			)
			playerEntityRenderState.showCape = true
		}
	}

	val startTime = TimeMark.now()
}
