package dev.jpruitt.spellcraft

import dev.jpruitt.spellcraft.SpellEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Block

object SpellEffects {
    public val NONE = SpellEffect("empty")
    public val TEST = TestSpell()
}

class TestSpell: SpellEffect("test") {
    override fun effect_entity(world: Level, user: LivingEntity, target: Entity): Boolean {
        if (target is LivingEntity) {
            target.addEffect(MobEffectInstance(MobEffects.GLOWING, 10))
            return true
        }
        return false
    }
    
    override fun effect_block(world: Level, user: LivingEntity, pos: BlockPos): Boolean {
        world.setBlock(pos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL)
        return true
    }
}