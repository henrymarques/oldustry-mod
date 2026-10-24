package com.oldustry.machine;

import com.oldustry.energy.EnergyNet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

final class OldustryLuminatorBlockEntity extends OldustryMachineBlockEntity {
    OldustryLuminatorBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.LUMINATOR, pos, state);
    }

    boolean tickLuminator(ServerLevel level, BlockPos pos) {
        // JAR TileEntityLuminator: buf 10k, drain 1 EU every 4 ticks.
        if (energy <= 0) {
            return false;
        }
        if (level.getGameTime() % 4 == 0) {
            energy--;
        }
        AABB box = new AABB(pos);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (entity.is(EntityTypeTags.UNDEAD)) {
                entity.igniteForSeconds(20);
            } else if (entity instanceof net.minecraft.world.entity.monster.Enemy) {
                entity.igniteForSeconds(10);
            }
        }
        return true;
    }

}
