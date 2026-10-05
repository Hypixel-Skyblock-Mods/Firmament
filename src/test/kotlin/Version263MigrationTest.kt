package moe.nea.firmod.test

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.serialization.JsonOps
import kotlinx.serialization.json.Json
import moe.nea.firmod.keybindings.GenericInputButton
import moe.nea.firmod.keybindings.InputModifiers
import moe.nea.firmod.util.mc.toBuilder
import moe.nea.firmod.util.mc.TolerantRegistriesOps
import moe.nea.firmod.util.MC
import net.minecraft.core.Holder
import net.minecraft.core.HolderOwner
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.core.registries.Registries
import net.minecraft.core.registries.codec.RegistryFixedCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Version263MigrationTest {
    @Test fun foreignRegistryOwnersCanStillEncodeSkyblockItems() {
        FirmTestBootstrap.bootstrapMinecraft()
        val foreignOwner = object : HolderOwner<Item> {}
        val foreign = object : Holder.Reference<Item>(
            Holder.Reference.Type.STAND_ALONE, foreignOwner,
            Items.STONE.builtInRegistryHolder().key(), Items.STONE
        ) {}
        val registries = MC.currentOrDefaultRegistries
        val codec = RegistryFixedCodec.create(Registries.ITEM)
        assertTrue(codec.encodeStart(RegistryOps.create(JsonOps.INSTANCE, registries), foreign).error().isPresent)
        val result = codec.encodeStart(TolerantRegistriesOps(JsonOps.INSTANCE, registries), foreign).getOrThrow()
        assertEquals("minecraft:stone", result.asString)
    }
    @Test fun savedGlfwBindingsRetainTheirMeaning() {
        assertEquals(GenericInputButton.ofKeyCode(InputConstants.KEY_A), Json.decodeFromString<GenericInputButton>("""{"keyCode":65}"""))
        assertEquals(GenericInputButton.ofKeyCode(InputConstants.KEY_RSHIFT), Json.decodeFromString<GenericInputButton>("""{"keyCode":344}"""))
        assertEquals(GenericInputButton.mouse(InputConstants.MOUSE_BUTTON_RIGHT), Json.decodeFromString<GenericInputButton>("""{"mouse":1}"""))
        assertEquals(InputModifiers(InputConstants.MOD_SHIFT or InputConstants.MOD_CONTROL), Json.decodeFromString<InputModifiers>("""{"modifiers":3}"""))
        assertEquals(GenericInputButton.Unbound, Json.decodeFromString<GenericInputButton>("""{"scanCode":16}"""))
    }

    @Test fun sdlBindingsRoundtripWithoutMigratingAgain() {
        val key: GenericInputButton = GenericInputButton.ofKeyCode(InputConstants.KEY_F)
        assertEquals(key, Json.decodeFromString<GenericInputButton>(Json.encodeToString(key)))
        val modifiers = InputModifiers(InputConstants.MOD_ALT)
        assertEquals(modifiers, Json.decodeFromString<InputModifiers>(Json.encodeToString(modifiers)))
    }

    @Test fun copyingComponentPatchesPreservesRemovedAndAddedComponents() {
        FirmTestBootstrap.bootstrapMinecraft()
        val patch = DataComponentPatch.builder()
            .set(DataComponents.CUSTOM_NAME, Component.literal("Skyblock item"))
            .remove(DataComponents.LORE)
            .build()
        val copy = patch.toBuilder().build()
        assertEquals(patch, copy)
        assertTrue(copy.split().removed().contains(DataComponents.LORE))
        assertEquals("Skyblock item", copy.split().added().get(DataComponents.CUSTOM_NAME)?.string)
    }
}
