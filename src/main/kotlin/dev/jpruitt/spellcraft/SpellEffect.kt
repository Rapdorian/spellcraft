package dev.jpruitt.spellcraft

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

open class SpellEffect(name: String) {
	private val name = name
	private var charges = 2
	private var cost = 1

	fun getName(): String {
		return name
	}

	fun getCharges(): Int {
		return charges
	}

	fun getCost(): Int {
		return cost
	}

	fun charges(new_charges: Int): SpellEffect {
		charges = new_charges
		return this
	}

	fun cost(new_cost: Int): SpellEffect {
		cost = new_cost
		return this
	}

	open fun effect_entity(world: Level, user: LivingEntity, target: Entity): Boolean {
		return false
	}
	
	open fun effect_block(world: Level, user: LivingEntity, block: BlockPos): Boolean {
		return false
	}
}