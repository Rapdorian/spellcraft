package dev.jpruitt.spellcraft

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import eu.pb4.polymer.core.api.item.SimplePolymerItem
import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.core.api.item.PolymerItemUtils

import net.minecraft.world.item.Item
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Block

class Scroll(props: Properties) : Item(props), PolymerItem {
    private val logger = LoggerFactory.getLogger("spellcraft")

	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		var scroll_inst = user.getItemInHand(hand)
		if (!world.isClientSide()) {
			val range = 20.0
			val min = user.getEyePosition(1.0f)
			val rot = user.getViewVector(1.0f)
			val max = min.add(rot.scale(range))


			val box = AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5).expandTowards(rot.scale(range)).move(min)
			//val ray_result = user.raycast(20.0, 1.0f, true)
			val ray_result = ProjectileUtil.getHitResultOnViewVector(user, {e -> true}, 20.0)
			val loc = ray_result.getLocation()
			val ty = ray_result.type
			logger.info("Hit Result found: ${ty} at ${loc.x()}, ${loc.y()} ${loc.z()}")

			logger.info("Damagable: ${scroll_inst.isDamageableItem()}")
			logger.info("Durability: ${scroll_inst.getDamageValue()}/${scroll_inst.getMaxDamage()}")
			scroll_inst.hurtAndBreak(1, user, user.getEquipmentSlotForItem(scroll_inst))

			if (ray_result is EntityHitResult){
				val ent = ray_result.entity
				if (ent is LivingEntity) {
					ent.addEffect(MobEffectInstance(MobEffects.GLOWING, 10))
				}
			} else if (ray_result is BlockHitResult) {
				world.setBlock(ray_result.getBlockPos(), Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL)
			}
		}

		return InteractionResultHolder.success(scroll_inst)
	}
	
	override fun getPolymerItem(itemStack: ItemStack, player: ServerPlayer?): Item {
		return if (itemStack.getCount() > 32) Items.DIAMOND_BLOCK else Items.DIAMOND
	}
}

object Spellcraft : ModInitializer {
    private val logger = LoggerFactory.getLogger("spellcraft")
	private val scroll = Scroll(Item.Properties().durability(20).stacksTo(1))

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("spellcraft", "scroll"), scroll)
	}
}