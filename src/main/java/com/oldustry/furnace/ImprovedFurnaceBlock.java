package com.oldustry.furnace;

import com.mojang.serialization.MapCodec;
import com.oldustry.registry.ModBlockEntities;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ImprovedFurnaceBlock extends AbstractFurnaceBlock implements Wrenchable {
    public static final MapCodec<ImprovedFurnaceBlock> CODEC = simpleCodec(ImprovedFurnaceBlock::new);

    public ImprovedFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<ImprovedFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ImprovedFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level instanceof net.minecraft.server.level.ServerLevel server
            ? createTickerHelper(type, ModBlockEntities.IMPROVED_FURNACE.get(), (inner, pos, blockState, furnace) -> ImprovedFurnaceBlockEntity.serverTick(server, pos, blockState, furnace))
            : null;
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ImprovedFurnaceBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1, 1, false);
            }
            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double ss = random.nextDouble() * 0.6 - 0.3;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : ss;
            double dy = random.nextDouble() * 6 / 16;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : ss;
            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0, 0, 0);
            level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0, 0, 0);
        }
    }

    @Override
    public ItemStack wrenchKeepDrop(BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public ItemStack wrenchFailDrop(BlockState state) {
        return new ItemStack(this);
    }
}
