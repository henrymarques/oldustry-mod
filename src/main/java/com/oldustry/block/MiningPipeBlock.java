package com.oldustry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

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
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }
}
