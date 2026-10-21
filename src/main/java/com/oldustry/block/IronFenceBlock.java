package com.oldustry.block;

import com.oldustry.compat.CuriosWearables;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;

/** IC2 BlockPoleFence: magnetizer stores charge 0–15 in metadata; metal shoes → lift. */
public class IronFenceBlock extends IronBarsBlock {
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 15);

    public IronFenceBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CHARGE);
    }

    /** JAR {@code isPole}: no iron fence on the four horizontal sides. */
    private static boolean isPole(Level level, BlockPos pos) {
        return !(level.getBlockState(pos.north()).getBlock() instanceof IronFenceBlock)
            && !(level.getBlockState(pos.south()).getBlock() instanceof IronFenceBlock)
            && !(level.getBlockState(pos.west()).getBlock() instanceof IronFenceBlock)
            && !(level.getBlockState(pos.east()).getBlock() instanceof IronFenceBlock);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!(entity instanceof Player player) || !isPole(level, pos)) {
            return;
        }
        int charge = state.getValue(CHARGE);
        boolean powered = charge > 0;
        // Feet slot, or Curios — static boots live on the feet accessory. Motion is client-auth.
        boolean metalShoes = CuriosWearables.wearingFeet(player, OldustryTags.Items.METAL_SHOES);
        if (!powered || !metalShoes) {
            if (player.isShiftKeyDown()) {
                Vec3 m = player.getDeltaMovement();
                if (m.y < -0.25) {
                    player.setDeltaMovement(m.x, m.y * 0.9, m.z);
                } else {
                    player.resetFallDistance();
                }
            }
            return;
        }
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(CHARGE, charge - 1), Block.UPDATE_CLIENTS);
        }
        Vec3 m = player.getDeltaMovement();
        double y = m.y + 0.075;
        if (y > 0) {
            y *= 1.03;
            player.resetFallDistance();
        }
        if (player.isShiftKeyDown()) {
            y = Math.min(y, 0.3);
        } else {
            y = Math.min(y, 1.5);
        }
        player.setDeltaMovement(m.x, y, m.z);
    }
}
