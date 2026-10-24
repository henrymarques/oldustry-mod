package com.oldustry.explosion;

import com.oldustry.registry.OldustryEntities;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class DynamiteProjectile extends ThrowableItemProjectile {
    /** Break or redstone, from {@code BlockDynamite.removeBlockByPlayer}. */
    public static final int LIT_FUSE = 40;
    /** Explosion, or the last supporting cube disappeared. */
    public static final int UNSEATED_FUSE = 5;

    private int fuse = 100;
    private @Nullable BlockPos stuckTo;

    public DynamiteProjectile(EntityType<? extends DynamiteProjectile> type, Level level) {
        super(type, level);
    }

    public DynamiteProjectile(ServerLevel level, LivingEntity owner, ItemStack stack) {
        super(OldustryEntities.DYNAMITE.get(), owner, level, stack);
    }

    public DynamiteProjectile(Level level, Position position, ItemStack stack) {
        super(OldustryEntities.DYNAMITE.get(), position.x(), position.y(), position.z(), level, stack);
    }

    /** Falling stick after the placed block is lit or loses every support. */
    public static boolean spawnPrimed(ServerLevel level, BlockPos pos, @Nullable LivingEntity owner, int fuse) {
        if (!level.getGameRules().get(GameRules.TNT_EXPLODES)) {
            return false;
        }
        DynamiteProjectile stick = new DynamiteProjectile(level, Vec3.atCenterOf(pos), new ItemStack(OldustryItems.DYNAMITE.get()));
        stick.fuse = fuse;
        stick.setDeltaMovement(Vec3.ZERO);
        if (owner != null) {
            stick.setOwner(owner);
        }
        if (!level.addFreshEntity(stick)) {
            return false;
        }
        level.playSound(null, stick.getX(), stick.getY(), stick.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(owner, GameEvent.PRIME_FUSE, pos);
        return true;
    }

    @Override
    protected Item getDefaultItem() {
        return OldustryItems.DYNAMITE.get();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected float getAirDrag() {
        return 0.98F;
    }

    @Override
    public void tick() {
        if (fuse-- <= 0) {
            if (level() instanceof ServerLevel serverLevel) {
                new PointExplosion(serverLevel, getOwner(), ExplosiveRest.restOn(serverLevel, position())).detonate();
            }
            discard();
            return;
        }
        if (isInWater()) {
            fuse += 2000;
        }
        if (fuse < 100 && fuse % 2 == 0) {
            level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5, getZ(), 0, 0, 0);
        }
        if (stuckTo != null) {
            if (!level().getBlockState(stuckTo).isAir()) {
                fuse -= 3;
                baseTick();
                return;
            }
            stuckTo = null;
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        // Parent parks the point on the face. The next ray then starts inside the block and the cube drops a layer.
        setPos(ExplosiveRest.backOff(hit.getLocation(), hit.getDirection().getUnitVec3()));
        if (getItem().is(OldustryItems.STICKY_DYNAMITE.get())) {
            stuckTo = hit.getBlockPos().immutable();
            setDeltaMovement(Vec3.ZERO);
        } else {
            Vec3 motion = getDeltaMovement();
            setDeltaMovement(
                motion.x * (0.75F - random.nextFloat()),
                motion.y * -0.3,
                motion.z * (0.75F - random.nextFloat())
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Fuse", fuse);
        if (stuckTo != null) {
            output.putInt("StickX", stuckTo.getX());
            output.putInt("StickY", stuckTo.getY());
            output.putInt("StickZ", stuckTo.getZ());
            output.putBoolean("Stuck", true);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        fuse = input.getIntOr("Fuse", 100);
        if (input.getBooleanOr("Stuck", false)) {
            stuckTo = new BlockPos(input.getIntOr("StickX", 0), input.getIntOr("StickY", 0), input.getIntOr("StickZ", 0));
        }
    }
}
