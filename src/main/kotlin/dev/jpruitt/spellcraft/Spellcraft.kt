package dev.jpruitt.spellcraft

import net.fabricmc.api.ModInitializer
import dev.jpruitt.spellcraft.Scroll
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.Registry

object SpellcraftItems {
	public val EMPTY_SCROLL = Scroll(SpellEffects.NONE)
	public val TEST_SCROLL = Scroll(SpellEffects.TEST)

	fun register_scroll(scroll: Scroll) {
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("spellcraft", "${scroll.getEffect().getName()}_scroll"), scroll)
	}

	fun register() {
		register_scroll(EMPTY_SCROLL)
		register_scroll(TEST_SCROLL)
	}
}

object Spellcraft : ModInitializer {

	override fun onInitialize() {
		SpellcraftItems.register()
	}
}