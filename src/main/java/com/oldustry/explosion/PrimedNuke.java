package com.oldustry.explosion;

import com.oldustry.OldustryConfig;
import com.oldustry.item.ElectricWrenchItem;
import com.oldustry.item.WrenchItem;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Primed nuke. Fuse 300, power from config (35), drop rate 0.05. The JAR damage argument 1.5 is unused. */
public final class PrimedNuke extends PrimedTnt {
    public static final int FUSE = 300;
    public static final float DROP_RATE = 0.05F;
    /** Electric wrench {@code damage(stack, 1)} spends 50 EU. */
    private static final int WRENCH_EU = 50;

    private @Nullable EntityReference<LivingEntity> igniter;

    public PrimedNuke(EntityType<? extends PrimedNuke> type, Level level) {
        super(type, level);
    }

    public PrimedNuke(ServerLevel level, BlockPos pos, @Nullable LivingEntity igniter, int fuse) {
        this(ModEntities.PRIMED_NUKE.get(), level);
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5 - getBbHeight() / 2.0;
        double z = pos.getZ() + 0.5;
        setPos(x, y, z);
        float angle = level.getRandom().nextFloat() * (float) (Math.PI * 2);
        setDeltaMovement(
            -Mth.sin(angle * (float) Math.PI / 180.0F) * 0.02,
            0.2,
            -Mth.cos(angle * (float) Math.PI / 180.0F) * 0.02
        );
        setFuse(fuse);
        setBlockState(ModBlocks.NUKE.get().defaultBlockState());
        xo = x;
        yo = y;
        zo = z;
        this.igniter = EntityReference.of(igniter);
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(igniter, level());
    }

    /** JAR {@code Entity} defaults {@code canBePushed} to true. 26.2 {@code PrimedTnt} does not. */
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
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack stack = player.getItemInHand(hand);
        boolean wrench = stack.getItem() instanceof WrenchItem;
        boolean electric = stack.getItem() instanceof ElectricWrenchItem;
        if (!wrench && !electric) {
            return InteractionResult.PASS;
        }
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.getAbilities().instabuild) {
            discard();
            return InteractionResult.SUCCESS;
        }
        if (wrench) {
            stack.hurtAndBreak(1, player, hand);
            discard();
            return InteractionResult.SUCCESS;
        }
        ElectricWrenchItem tool = (ElectricWrenchItem) stack.getItem();
        if (!tool.canPay(stack, WRENCH_EU, player)) {
            return InteractionResult.FAIL;
        }
        tool.pay(stack, WRENCH_EU, player);
        discard();
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void explode() {
        if (!(level() instanceof ServerLevel server) || !server.getGameRules().get(GameRules.TNT_EXPLODES)) {
            return;
        }
        double feet = ExplosiveRest.restOn(server, position()).y;
        float power = OldustryConfig.NUKE_POWER.get().floatValue();
        new OldustryExplosion(server, this, new Vec3(getX(), feet + getBbHeight() / 2.0, getZ()), power, DROP_RATE, true).detonate();
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
        if (oldEntity instanceof PrimedNuke primed) {
            igniter = primed.igniter;
        }
    }
}
