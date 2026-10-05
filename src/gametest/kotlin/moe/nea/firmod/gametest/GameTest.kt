package moe.nea.firmod.gametest

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext
import org.junit.jupiter.api.Assertions
import org.spongepowered.asm.mixin.MixinEnvironment
import moe.nea.firmod.init.MixinPlugin
import moe.nea.firmod.events.IsSlotProtectedEvent
import moe.nea.firmod.gui.config.AllConfigsGui
import moe.nea.firmod.gui.config.FirmodConfigScreenProvider
import moe.nea.firmod.util.MC
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.concurrent.atomic.AtomicBoolean

class GameTest : FabricClientGameTest {
	override fun runTest(ctx: ClientGameTestContext) {
		// These checks use only a disposable local world, without an account login.
		ctx.worldBuilder().create().use { world ->
			world.connection.waitForChunksRender()
			ctx.waitTicks(20)
			val protectDrop = AtomicBoolean(true)
			val sawDrop = AtomicBoolean(false)
			ctx.runOnClient<RuntimeException> { mc ->
				IsSlotProtectedEvent.subscribe("gametest:drop-from-hotbar") { event ->
					if (event.origin == IsSlotProtectedEvent.MoveOrigin.DROP_FROM_HOTBAR) {
						sawDrop.set(true)
						if (protectDrop.get()) event.protectSilent()
					}
				}
				val player = requireNotNull(mc.player)
				player.inventory.setSelectedSlot(0)
				player.inventory.setItem(0, ItemStack(Items.STONE, 10))
				requireNotNull(mc.gameMode).dropItem(player, false)
				Assertions.assertTrue(sawDrop.get(), "Dropping a single item must publish the protection event")
				Assertions.assertEquals(10, player.mainHandItem.count, "Protected single-item drop changed inventory")
				sawDrop.set(false)
				mc.gameMode!!.dropItem(player, true)
				Assertions.assertTrue(sawDrop.get(), "Dropping a stack must publish the protection event")
				Assertions.assertEquals(10, player.mainHandItem.count, "Protected stack drop changed inventory")
				protectDrop.set(false)
				mc.gameMode!!.dropItem(player, false)
				Assertions.assertEquals(9, player.mainHandItem.count, "Unprotected drop was unexpectedly blocked")
				Assertions.assertTrue(FirmodConfigScreenProvider.providers.any { it.key == "moulconfig" },
					"The production MoulConfig integration must be available")
			}
			ctx.setScreen { AllConfigsGui.makeScreen() }
			ctx.waitTicks(20)
			ctx.runOnClient<RuntimeException> { Assertions.assertNotNull(MC.screen, "The config screen did not open") }
			ctx.takeScreenshot("firmament-moulconfig")
			ctx.runOnClient<RuntimeException> { MC.screen = null }
			ctx.setScreen { AllConfigsGui.makeBuiltInScreen() }
			ctx.waitTicks(20)
			ctx.takeScreenshot("firmament-builtin-config")
			ctx.runOnClient<RuntimeException> { MC.screen = null }
			ctx.waitTicks(40)
		}
		MixinEnvironment.getCurrentEnvironment().audit()
		for (mp in MixinPlugin.instances) {
			Assertions.assertEquals(mp.expectedFullPathMixins, mp.appliedFullPathMixins)
			Assertions.assertNotEquals(0, mp.mixins.size)
		}
		println("FIRMOD_NATIVE_SMOKE_PASSED local world, item protection, MoulConfig, built-in config")
	}
}
