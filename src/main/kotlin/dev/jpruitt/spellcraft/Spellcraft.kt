package dev.jpruitt.spellcraft

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import net.minecraft.item.Item
import net.minecraft.world.World
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.item.ItemStack
import net.minecraft.util.TypedActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ActionResult
import eu.pb4.polymer.core.api.item.SimplePolymerItem
import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.item.Items
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import eu.pb4.polymer.core.api.item.PolymerItemUtils

class Scroll(settings: Settings) : Item(settings), PolymerItem {

    private val logger = LoggerFactory.getLogger("spellcraft")

	override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		val target = user.getBlockPos().offset(user.getHorizontalFacing(), 10).toCenterPos()
		var bolt = LightningEntity(EntityType.LIGHTNING_BOLT, world)
		bolt.setPosition(target)
		world.spawnEntity(bolt)
		return TypedActionResult.success(user.getStackInHand(hand))
	}
	
	override fun getPolymerItem(itemStack: ItemStack, player: ServerPlayerEntity?): Item {
		return if (itemStack.getCount() > 32) Items.DIAMOND_BLOCK else Items.DIAMOND
	}
}

object Spellcraft : ModInitializer {
    private val logger = LoggerFactory.getLogger("spellcraft")
	private val item_settings = Item.Settings()
	private val scroll = Scroll(item_settings)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		Registry.register(Registries.ITEM, Identifier.of("spellcraft", "scroll"), scroll)
	}
}