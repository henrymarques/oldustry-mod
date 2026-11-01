package com.oldustry.entity;

import org.jspecify.annotations.Nullable;

import com.oldustry.explosion.OldustryExplosion;
import com.oldustry.item.tool.OldustryLaser;
import com.oldustry.registry.OldustryEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.SynchedEntityData;

/**
 * IC2 {@code EntityMiningLaser}. One block per tick. Water kills it.
 * Unbreakable faces are brick, obsidian, lava, water, bedrock and glass.
 */
public final class MiningLaser extends Entity {
    private float range;
    private float power;
    private int breaks;
    private boolean explosive;
    private boolean smelt;
    private int ticksInAir;
    private @Nullable LivingEntity owner;

    public MiningLaser(EntityType<? extends MiningLaser> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public static void shoot(
        ServerLevel level, LivingEntity owner,
        float range, float power, int breaks, boolean explosive, boolean smelt,
        float yaw, float pitch, double y
    ) {
        MiningLaser laser = new MiningLaser(OldustryEntities.MINING_LASER.get(), level);
        laser.owner = owner;
        laser.range = range;
        laser.power = power;
        laser.breaks = breaks;
        laser.explosive = explosive;
        laser.smelt = smelt;
        double[] heading = OldustryLaser.heading(yaw, pitch);
        laser.setPos(OldustryLaser.muzzleX(owner.getX(), yaw), y, OldustryLaser.muzzleZ(owner.getZ(), yaw));
        laser.setDeltaMovement(heading[0], heading[1], heading[2]);
        laser.setYRot((float) Math.toDegrees(Math.atan2(heading[0], heading[2])));
        laser.setXRot((float) Math.toDegrees(Math.atan2(heading[1], Math.sqrt(heading[0] * heading[0] + heading[2] * heading[2]))));
        laser.yRotO = laser.getYRot();
        laser.xRotO = laser.getXRot();
        level.addFreshEntity(laser);
    }

    public static void shootLook(ServerLevel level, LivingEntity owner, float range, float power, int breaks, boolean explosive, boolean smelt) {
        shoot(
            level, owner, range, power, breaks, explosive, smelt,
            owner.getYRot(), owner.getXRot(), OldustryLaser.eyeLaunch(owner.getY(), owner.getEyeHeight())
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public void tick() {
        baseTick();
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        if (OldustryLaser.spent(range, power, breaks)) {
            if (explosive) {
                explode(level);
            }
            discard();
            return;
        }
        ticksInAir++;
        Vec3 from = position();
        Vec3 motion = getDeltaMovement();
        Vec3 to = from.add(motion);
        BlockHitResult blockHit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 entityEnd = blockHit.getType() == HitResult.Type.MISS ? to : blockHit.getLocation();
        Entity struck = closest(from, entityEnd);
        if (struck != null || blockHit.getType() != HitResult.Type.MISS) {
            if (explosive) {
                explode(level);
                discard();
                return;
            }
            if (struck != null) {
                hitEntity(level, struck);
                discard();
                return;
            }
            if (!hitBlock(level, blockHit.getBlockPos())) {
                return;
            }
        } else {
            power = OldustryLaser.airDrain(power);
        }
        setPos(to);
        range = OldustryLaser.stepRange(range);
        if (isInWater()) {
            discard();
        }
    }

    private @Nullable Entity closest(Vec3 from, Vec3 to) {
        AABB box = getBoundingBox().expandTowards(getDeltaMovement()).inflate(1);
        Entity closest = null;
        double best = 0;
        for (Entity candidate : level().getEntities(this, box, Entity::isPickable)) {
            if (candidate == owner && ticksInAir < 5) {
                continue;
            }
            AABB grown = candidate.getBoundingBox().inflate(0.3);
            Vec3 hit = grown.clip(from, to).orElse(null);
            if (hit == null) {
                continue;
            }
            double distance = from.distanceTo(hit);
            if (closest == null || distance < best) {
                closest = candidate;
                best = distance;
            }
        }
        return closest;
    }

    private void hitEntity(ServerLevel level, Entity target) {
        int damage = (int) power;
        if (damage <= 0) {
            return;
        }
        target.igniteForSeconds(OldustryLaser.burnSeconds(damage, smelt));
        DamageSource source = level.damageSources().thrown(this, owner);
        if (target.hurtServer(level, source, damage) && owner instanceof ServerPlayer player && dragonDied(target)) {
            var holder = player.level().getServer().getAdvancements().get(
                Identifier.fromNamespaceAndPath(com.oldustry.Oldustry.MOD_ID, "kill_dragon_mining_laser"));
            if (holder != null) {
                player.getAdvancements().award(holder, "killed");
            }
        }
    }

    private static boolean dragonDied(Entity target) {
        EnderDragon dragon = target instanceof EnderDragon direct
            ? direct
            : target instanceof EnderDragonPart part ? part.parentMob : null;
        return dragon != null && dragon.getHealth() <= 0;
    }

    /** @return false when the beam must stop */
    private boolean hitBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || blocked(state)) {
            discard();
            return false;
        }
        power = OldustryLaser.absorb(power, state.getExplosionResistance(level, pos, null));
        if (power < 0f) {
            return true;
        }
        boolean remove = true;
        boolean drop = true;
        if (state.getBlock() instanceof net.minecraft.world.level.block.TntBlock) {
            state.onCaughtFire(level, pos, Direction.UP, owner);
        } else if (smelt && melt(level, pos, state)) {
            drop = false;
            power = 0f;
        }
        if (remove) {
            if (drop && level.getRandom().nextFloat() < 0.9f) {
                for (ItemStack stack : Block.getDrops(state, level, pos, level.getBlockEntity(pos))) {
                    Block.popResource(level, pos, stack);
                }
            }
            boolean burns = state.isFlammable(level, pos, Direction.UP);
            level.removeBlock(pos, false);
            if (burns && level.getRandom().nextInt(10) == 0) {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        breaks--;
        return true;
    }

    /** Furnace result on the ground. The block itself is removed by the caller. */
    private static boolean melt(ServerLevel level, BlockPos pos, BlockState state) {
        boolean any = false;
        for (ItemStack drop : Block.getDrops(state, level, pos, level.getBlockEntity(pos))) {
            SingleRecipeInput input = new SingleRecipeInput(drop);
            ItemStack result = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level)
                .map(holder -> holder.value().assemble(input))
                .orElse(ItemStack.EMPTY);
            if (result.isEmpty()) {
                continue;
            }
            any = true;
            double ox = level.getRandom().nextFloat() * 0.7 + 0.15;
            double oy = level.getRandom().nextFloat() * 0.7 + 0.15;
            double oz = level.getRandom().nextFloat() * 0.7 + 0.15;
            ItemEntity entity = new ItemEntity(level, pos.getX() + ox, pos.getY() + oy, pos.getZ() + oz, result.copy());
            entity.setPickUpDelay(10);
            level.addFreshEntity(entity);
        }
        return any;
    }

    private void explode(ServerLevel level) {
        new OldustryExplosion(level, null, position(), OldustryLaser.EXPLOSION_POWER, OldustryLaser.EXPLOSION_DROP).detonate();
    }

    /**
     * JAR ids: brick, obsidian, both lavas, both waters, bedrock, glass.
     * Brick is {@code field_72081_al}. Bedrock is the explosion-whitelist block.
     */
    static boolean blocked(BlockState state) {
        return state.is(Blocks.BRICKS)
            || state.is(Blocks.OBSIDIAN)
            || state.getFluidState().is(net.minecraft.world.level.material.Fluids.LAVA)
            || state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)
            || state.is(Blocks.BEDROCK)
            || state.is(Blocks.GLASS);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256.0 * 256.0;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}

    @Override
    protected void readAdditionalSaveData(ValueInput input) {}
}
