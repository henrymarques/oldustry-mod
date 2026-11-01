package com.oldustry.machine;

import com.oldustry.energy.OldustryDamage;
import com.oldustry.item.equipment.OldustryHazmatItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.state.BlockState;

/** JAR {@code TileEntityTesla}. No GUI. The coil never calls {@code setActive}. */
final class OldustryTeslaBlockEntity extends OldustryMachineBlockEntity {
    /** Not saved. The JAR only writes {@code energy}. */
    private int ticker;

    OldustryTeslaBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.TESLA, pos, state);
    }

    boolean tickTesla(ServerLevel level, BlockPos pos) {
        OldustryTesla.Tick step = OldustryTesla.step(energy, ticker, level.hasNeighborSignal(pos));
        energy = step.energy();
        ticker = step.ticker();
        if (step.shockDamage() > 0 && shock(level, pos, step.shockDamage())) {
            energy = 0;
        }
        return false;
    }

    private static boolean shock(ServerLevel level, BlockPos pos, int damage) {
        boolean hit = false;
        DustParticleOptions dust = new DustParticleOptions(OldustryTesla.PARTICLE_RGB, 1.0F);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, OldustryTesla.box(pos))) {
            // 1.5.2 EntityLiving has no armor stand. A complete hazmat set is skipped, not damaged.
            if (victim instanceof ArmorStand || OldustryHazmatItem.blocksFallout(victim)) {
                continue;
            }
            hit = true;
            victim.hurtServer(level, OldustryDamage.electricity(level), damage);
            for (int i = 0; i < damage; i++) {
                level.sendParticles(
                    dust,
                    victim.getX() + level.getRandom().nextFloat(),
                    victim.getY() + level.getRandom().nextFloat() * 2.0F,
                    victim.getZ() + level.getRandom().nextFloat(),
                    1, 0.0, 0.0, 0.0, 0.0
                );
            }
        }
        return hit;
    }
}
