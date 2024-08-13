package dev.jpruitt.spellcraft

import dev.jpruitt.spellcraft.SpellEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.core.component.DataComponents
import net.minecraft.world.level.block.Blocks
import org.slf4j.LoggerFactory

import eu.pb4.polymer.core.api.item.PolymerItem


class Scroll(effect: SpellEffect) : Item(Item.Properties().durability(effect.getCharges()).stacksTo(1)), PolymerItem {
	private val effect = effect
    private val logger = LoggerFactory.getLogger("spellcraft")

	fun getEffect(): SpellEffect {
		return effect
	}

	fun isInert(item: ItemStack): Boolean {
		return item.getMaxDamage() - item.damageValue < getEffect().getCost()
	}

	override fun isFoil(item: ItemStack): Boolean {
		return !isInert(item)
	}

    fun setMaxDamage(item: ItemStack, max: Int) {
        item.set(DataComponents.MAX_DAMAGE, max)
    }

    fun restoreCharges(user: Player, item: ItemStack, charges: Int) {
        if (user.experienceLevel < charges) {
            logger.info("Player does'nt have enough XP to restore charges")
            return Unit
        }

        val remainder = item.getMaxDamage() - item.damageValue
        if (item.getMaxDamage() < remainder + charges) {
            setMaxDamage(item, remainder + charges)
            item.damageValue = 0
            user.giveExperienceLevels(0-charges)
        }else if (item.damageValue > 0){
            item.damageValue -= charges
            user.giveExperienceLevels(0-charges)
        }
    }

	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		var scroll = user.getItemInHand(hand)
		if (!world.isClientSide()) {
            val range = 20.0
            val ray_result = ProjectileUtil.getHitResultOnViewVector(user, {e -> true}, range)

            // check if we are trying to restore charges
            if (ray_result is BlockHitResult) {
                // check if the block is an altar
                val block = world.getBlockState(ray_result.getBlockPos())
                logger.info("Found a ${block.getBlock()} block")
                if (block.getBlock() == Blocks.ENCHANTING_TABLE) {
                    logger.info("Trying to restore charges")
                    restoreCharges(user, scroll, 1)
                }else if (!isInert(scroll)){
                    if (getEffect().effect_block(world, user, ray_result.getBlockPos())) {
                        scroll.damageValue += getEffect().getCost()
                    }
                }
            } else if (ray_result is EntityHitResult) {
                if (!isInert(scroll)) {
                    if (getEffect().effect_entity(world, user, ray_result.entity)) {
                        scroll.damageValue += getEffect().getCost()
                    }
                }
            }
		}
		return InteractionResultHolder.success(scroll)
	}
	
	override fun getPolymerItem(itemStack: ItemStack, player: ServerPlayer?): Item {
		return Items.PAPER 
	}
}