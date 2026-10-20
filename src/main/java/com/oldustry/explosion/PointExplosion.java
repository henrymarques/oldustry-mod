package com.oldustry.explosion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jspecify.annotations.Nullable;

/** IC2's PointExplosion: the 3x3x3 neighborhood, not a vanilla radial blast. */
public final class PointExplosion extends ServerExplosion {
    private final ServerLevel level;
    private final @Nullable Entity source;
    private final Vec3 center;

    public PointExplosion(ServerLevel level, @Nullable Entity source, Vec3 center) {
        super(level, source, null, null, center, 1.0F, false, Explosion.BlockInteraction.DESTROY);
        this.level = level;
        this.source = source;
        this.center = center;
    }

    public void detonate() {
        if (EventHooks.onExplosionStart(level, this)) {
            return;
        }
        explode();
        level.gameEvent(source, GameEvent.EXPLODE, center);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, 1.0F);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 1, 0, 0, 0, 0);
    }

    @Override
    public int explode() {
        List<BlockPos> blocks = new ArrayList<>();
        for (BlockPos pos : candidatePositions(center)) {
            if (!level.getBlockState(pos).isAir() && level.getBlockState(pos).getExplosionResistance(level, pos, this) < 10.0F) {
                blocks.add(pos);
            }
        }

        List<Entity> entities = level.getEntities(source, new AABB(
            center.x - 2, center.y - 2, center.z - 2,
            center.x + 2, center.y + 2, center.z + 2
        ));
        EventHooks.onExplosionDetonate(level, this, entities, blocks);
        for (Entity entity : entities) {
            entity.hurtServer(level, level.damageSources().explosion(this), 20.0F);
        }
        for (BlockPos pos : blocks) {
            level.getBlockState(pos).onExplosionHit(level, pos, this, (stack, dropPos) -> Block.popResource(level, dropPos, stack));
        }
        return blocks.size();
    }

    static List<BlockPos> candidatePositions(Vec3 center) {
        BlockPos middle = BlockPos.containing(center);
        List<BlockPos> positions = new ArrayList<>(27);
        for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-1, -1, -1), middle.offset(1, 1, 1))) {
            positions.add(pos.immutable());
        }
        return positions;
    }
}
