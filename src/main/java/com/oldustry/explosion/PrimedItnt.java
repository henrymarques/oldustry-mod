package com.oldustry.explosion;

import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Primed Industrial TNT. Fuse 60, power 5.5, drop rate 0.9. The JAR damage argument 0.3 is unused. */
public final class PrimedItnt extends PrimedTnt {
    public static final int FUSE = 60;
    public static final float POWER = 5.5F;
    public static final float DROP_RATE = 0.9F;

    private @Nullable EntityReference<LivingEntity> igniter;

    public PrimedItnt(EntityType<? extends PrimedItnt> type, Level level) {
        super(type, level);
    }

    public PrimedItnt(ServerLevel level, BlockPos pos, @Nullable LivingEntity igniter, int fuse) {
        this(ModEntities.PRIMED_ITNT.get(), level);
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5 - getBbHeight() / 2.0;
        double z = pos.getZ() + 0.5;
        setPos(x, y, z);
        // JAR multiplies an already-radian angle by pi/180, so the sideways hop is ~0.
        float angle = level.getRandom().nextFloat() * (float) (Math.PI * 2);
        setDeltaMovement(
            -Mth.sin(angle * (float) Math.PI / 180.0F) * 0.02,
            0.2,
            -Mth.cos(angle * (float) Math.PI / 180.0F) * 0.02
        );
        setFuse(fuse);
        setBlockState(ModBlocks.ITNT.get().defaultBlockState());
        xo = x;
        yo = y;
        zo = z;
        this.igniter = EntityReference.of(igniter);
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(igniter, level());
    }

    /** JAR {@code EntityIC2Explosive} keeps the default {@code canBePushed}. */
    @Override
    public boolean isPushable() {
        return !this.isRemoved();
    }

    @Override
    public void move(MoverType type, Vec3 delta) {
        super.move(type, delta);
        Vec3 stood = ExplosiveRest.restOn(level(), position());
        if (stood.y != getY()) {
            setPos(getX(), stood.y, getZ());
            Vec3 motion = getDeltaMovement();
            if (motion.y < 0.0) {
                setDeltaMovement(motion.x, 0.0, motion.z);
            }
        }
    }

    @Override
    protected void explode() {
        if (!(level() instanceof ServerLevel server) || !server.getGameRules().get(GameRules.TNT_EXPLODES)) {
            return;
        }
        // JAR explodes at posY, which is the box center (yOffset = height / 2), with the feet on the face.
        double feet = ExplosiveRest.restOn(server, position()).y;
        new OldustryExplosion(server, this, new Vec3(getX(), feet + getBbHeight() / 2.0, getZ()), POWER, DROP_RATE).detonate();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference.store(igniter, output, "igniter");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        igniter = EntityReference.read(input, "igniter");
    }

    @Override
    public void restoreFrom(net.minecraft.world.entity.Entity oldEntity) {
        super.restoreFrom(oldEntity);
        if (oldEntity instanceof PrimedItnt primed) {
            igniter = primed.igniter;
        }
    }
}
