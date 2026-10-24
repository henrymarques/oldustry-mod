package com.oldustry.explosion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.oldustry.Oldustry;
import com.oldustry.energy.OldustryDamage;
import com.oldustry.registry.OldustryEffects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jspecify.annotations.Nullable;

/** IC2 {@code ExplosionIC2}: spherical rays, not a vanilla blast. */
public final class OldustryExplosion extends ServerExplosion {
    private static final Map<Block, Boolean> WHITELIST = new IdentityHashMap<>();

    static {
        // IC2.java adds bedrock so a high-resistance block stops the ray instead of being ignored.
        WHITELIST.put(Blocks.BEDROCK, Boolean.TRUE);
    }

    private final ServerLevel level;
    private final @Nullable Entity source;
    private final Vec3 center;
    private final double power;
    private final float dropRate;
    private final boolean nuclear;
    private final Random random = new Random();

    public OldustryExplosion(ServerLevel level, @Nullable Entity source, Vec3 center, float power, float dropRate) {
        this(level, source, center, power, dropRate, false);
    }

    public OldustryExplosion(ServerLevel level, @Nullable Entity source, Vec3 center, float power, float dropRate, boolean nuclear) {
        super(level, source, null, null, center, power, false, Explosion.BlockInteraction.DESTROY);
        this.level = level;
        this.source = source;
        this.center = center;
        this.power = power;
        this.dropRate = dropRate;
        this.nuclear = nuclear;
    }

    public void detonate() {
        if (power <= 0.0F || EventHooks.onExplosionStart(level, this)) {
            return;
        }
        double maxDistance = power / 0.4;
        List<Target> targets = collectTargets(maxDistance);
        Map<BlockPos, Boolean> destroyed = new HashMap<>();
        int steps = OldustryRay.raySteps(power);
        boolean hitEntities = !targets.isEmpty();
        for (int phiN = 0; phiN < 2 * steps; phiN++) {
            for (int thetaN = 0; thetaN < steps; thetaN++) {
                double phi = Math.PI * 2.0 / steps * phiN;
                double theta = Math.PI / steps * thetaN;
                boolean damage = hitEntities && phiN % 8 == 0 && thetaN % 8 == 0;
                shootRay(center.x, center.y, center.z, phi, theta, power, damage, targets, destroyed);
            }
        }
        capMotion(targets);
        if (nuclear) {
            irradiate();
        }

        List<BlockPos> blocks = new ArrayList<>(destroyed.keySet());
        List<Entity> entities = new ArrayList<>(targets.size());
        for (Target target : targets) {
            entities.add(target.entity);
        }
        EventHooks.onExplosionDetonate(level, this, entities, blocks);

        Map<Column, Map<ItemStack, Drop>> drops = new HashMap<>();
        for (BlockPos pos : blocks) {
            Boolean drop = destroyed.get(pos);
            if (drop == null) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            if (drop) {
                spawnBreakParticles(pos);
                for (ItemStack stack : Block.getDrops(state, level, pos, level.getBlockEntity(pos))) {
                    if (level.getRandom().nextFloat() <= dropRate) {
                        keepDrop(drops, pos, stack);
                    }
                }
            }
            level.removeBlock(pos, false);
            state.getBlock().wasExploded(level, pos, this);
        }
        spawnDrops(drops);
        level.gameEvent(source, GameEvent.EXPLODE, center);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, 1.0F);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 1, 0, 0, 0, 0);
    }

    private void shootRay(
        double x, double y, double z, double phi, double theta, double rayPower, boolean damage,
        List<Target> targets, Map<BlockPos, Boolean> destroyed
    ) {
        double[] dir = OldustryRay.direction(phi, theta);
        int minY = level.getMinY();
        int maxY = level.getMaxY();
        for (int step = 0; ; step++) {
            int blockY = OldustryRay.floor(y);
            if (blockY < minY || blockY >= maxY) {
                return;
            }
            int blockX = OldustryRay.floor(x);
            int blockZ = OldustryRay.floor(z);
            BlockPos pos = new BlockPos(blockX, blockY, blockZ);
            BlockState state = level.getBlockState(pos);
            boolean solid = !state.isAir();
            OldustryRay.Hit hit = OldustryRay.hit(
                solid,
                solid ? state.getExplosionResistance(level, pos, this) : 0.0,
                solid && WHITELIST.containsKey(state.getBlock()),
                rayPower
            );
            if (hit.stop()) {
                return;
            }
            if (solid && !hit.passThrough()) {
                Boolean mark = OldustryRay.drop(rayPower, destroyed.containsKey(pos));
                if (mark != null) {
                    destroyed.put(pos, mark);
                }
            }
            if (damage && (step + 4) % 8 == 0 && rayPower >= 0.25 && !targets.isEmpty()) {
                damageAnchor(targets, step, x, y, z, rayPower);
            }
            if (hit.absorption() > 10.0) {
                for (int i = 0; i < 5; i++) {
                    shootRay(
                        x, y, z,
                        random.nextDouble() * 2.0 * Math.PI,
                        random.nextDouble() * Math.PI,
                        hit.absorption() * 0.4,
                        false,
                        targets,
                        destroyed
                    );
                }
            }
            rayPower -= hit.absorption();
            x += dir[0];
            y += dir[1];
            z += dir[2];
        }
    }

    private void damageAnchor(List<Target> targets, int step, double x, double y, double z, double rayPower) {
        int[] keys = new int[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            keys[i] = targets.get(i).distanceKey;
        }
        int anchor = OldustryRay.anchorIndex(step, keys);
        int distanceMax = step * step + 25;
        for (int i = anchor; i < targets.size(); i++) {
            Target target = targets.get(anchor);
            if (target.distanceKey >= distanceMax) {
                return;
            }
            Entity entity = target.entity;
            if (entity.distanceToSqr(x, y, z) <= 25.0) {
                entity.hurtServer(level, blastDamage(), OldustryRay.entityDamage(rayPower));
                knockback(entity, rayPower);
                if (nuclear && entity instanceof ServerPlayer player && player == igniter() && !player.isAlive()) {
                    var holder = player.level().getServer().getAdvancements().get(
                        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "die_from_own_nuke"));
                    if (holder != null) {
                        player.getAdvancements().award(holder, "nuked");
                    }
                }
                if (!entity.isAlive()) {
                    targets.remove(i);
                    i--;
                }
            }
        }
    }

    private DamageSource blastDamage() {
        if (!nuclear) {
            return level.damageSources().explosion(this);
        }
        return OldustryDamage.nuke(level, source, igniter());
    }

    private @Nullable LivingEntity igniter() {
        return source instanceof PrimedTnt primed ? primed.getOwner() : null;
    }

    /** Hunger out to 100 blocks, radiation out to 30. ponytail: a full hazmat suit would skip both; nobody is immune until that armor exists. */
    private void irradiate() {
        AABB box = new AABB(center.x - 100, center.y - 100, center.z - 100, center.x + 100, center.y + 100, center.z + 100);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            double distance = Math.sqrt(entity.distanceToSqr(center.x, center.y, center.z));
            int hunger = NukeFallout.hungerTicks(distance);
            if (hunger >= 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, hunger, 0));
            }
            int radiation = NukeFallout.radiationTicks(distance);
            if (radiation >= 0) {
                entity.addEffect(new MobEffectInstance(OldustryEffects.RADIATION, radiation, 0));
            }
        }
    }

    private void knockback(Entity entity, double rayPower) {
        double dx = entity.getX() - center.x;
        double dy = entity.getY() - center.y;
        double dz = entity.getZ() - center.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0.0) {
            return;
        }
        double scale = 0.7 * rayPower / OldustryRay.DROP_POWER_LIMIT / distance;
        entity.setDeltaMovement(entity.getDeltaMovement().add(dx * scale, dy * scale, dz * scale));
    }

    private static void capMotion(List<Target> targets) {
        for (Target target : targets) {
            Vec3 motion = target.entity.getDeltaMovement();
            double motionSq = motion.lengthSqr();
            if (motionSq > 3600.0) {
                target.entity.setDeltaMovement(motion.scale(Math.sqrt(3600.0 / motionSq)));
            }
        }
    }

    private List<Target> collectTargets(double maxDistance) {
        AABB box = new AABB(
            center.x - maxDistance, center.y - maxDistance, center.z - maxDistance,
            center.x + maxDistance, center.y + maxDistance, center.z + maxDistance
        );
        List<Entity> found = level.getEntities(
            (Entity) null,
            box,
            entity -> entity instanceof LivingEntity || entity instanceof ItemEntity || entity instanceof PrimedTnt
        );
        List<Target> targets = new ArrayList<>(found.size());
        for (Entity entity : found) {
            double dx = entity.getX() - center.x;
            double dy = entity.getY() - center.y;
            double dz = entity.getZ() - center.z;
            targets.add(new Target((int) (dx * dx + dy * dy + dz * dz), entity));
        }
        targets.sort((a, b) -> Integer.compare(a.distanceKey, b.distanceKey));
        return targets;
    }

    private void spawnBreakParticles(BlockPos pos) {
        double effectX = pos.getX() + level.getRandom().nextFloat();
        double effectY = pos.getY() + level.getRandom().nextFloat();
        double effectZ = pos.getZ() + level.getRandom().nextFloat();
        double dx = effectX - center.x;
        double dy = effectY - center.y;
        double dz = effectZ - center.z;
        double effectDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (effectDistance == 0.0) {
            effectDistance = 1.0;
        }
        dx /= effectDistance;
        dy /= effectDistance;
        dz /= effectDistance;
        double scale = 0.5 / (effectDistance / power + 0.1);
        scale *= level.getRandom().nextFloat() * level.getRandom().nextFloat() + 0.3F;
        dx *= scale;
        dy *= scale;
        dz *= scale;
        level.sendParticles(ParticleTypes.EXPLOSION, (effectX + center.x) / 2.0, (effectY + center.y) / 2.0, (effectZ + center.z) / 2.0, 0, dx, dy, dz, 1.0);
        level.sendParticles(ParticleTypes.SMOKE, effectX, effectY, effectZ, 0, dx, dy, dz, 1.0);
    }

    private static void keepDrop(Map<Column, Map<ItemStack, Drop>> drops, BlockPos pos, ItemStack stack) {
        Column column = new Column(pos.getX() / 2, pos.getZ() / 2);
        Map<ItemStack, Drop> columnDrops = drops.computeIfAbsent(column, key -> new HashMap<>());
        ItemStack key = stack.copyWithCount(1);
        Drop existing = columnDrops.get(key);
        if (existing == null) {
            columnDrops.put(key, new Drop(stack.getCount(), pos.getY()));
        } else {
            existing.count += stack.getCount();
            existing.maxY = Math.max(existing.maxY, pos.getY());
        }
    }

    private void spawnDrops(Map<Column, Map<ItemStack, Drop>> drops) {
        for (var columnEntry : drops.entrySet()) {
            Column column = columnEntry.getKey();
            for (var dropEntry : columnEntry.getValue().entrySet()) {
                int count = dropEntry.getValue().count;
                while (count > 0) {
                    int size = Math.min(count, 64);
                    ItemStack stack = dropEntry.getKey().copyWithCount(size);
                    double x = (column.x + level.getRandom().nextFloat()) * 2.0;
                    double z = (column.z + level.getRandom().nextFloat()) * 2.0;
                    ItemEntity entity = new ItemEntity(level, x, dropEntry.getValue().maxY + 0.5, z, stack);
                    entity.setPickUpDelay(10);
                    level.addFreshEntity(entity);
                    count -= size;
                }
            }
        }
    }

    private record Target(int distanceKey, Entity entity) {}

    private record Column(int x, int z) {}

    private static final class Drop {
        int count;
        int maxY;

        Drop(int count, int maxY) {
            this.count = count;
            this.maxY = maxY;
        }
    }
}
