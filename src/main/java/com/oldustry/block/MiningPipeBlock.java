package com.oldustry.block;

import com.oldustry.registry.OldustryBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Thin pipe column placed by the Miner as it descends. */
public class MiningPipeBlock extends Block {
    private static final VoxelShape SHAPE =
        box(6, 0, 6, 10, 16, 10); // 4×16×4 pixel column

    private final boolean isTip;

    public MiningPipeBlock(boolean isTip, Properties props) {
        super(props);
        this.isTip = isTip;
    }

    public boolean isTip() {
        return isTip;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return isTip ? Shapes.block() : SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return isTip ? Shapes.block() : Shapes.empty();
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        // JAR BlockMiningTip.idDropped + empty getSubBlocks: the tip is not an item.
        return new ItemStack(isTip ? OldustryBlocks.MINING_PIPE.get() : this);
    }
}
