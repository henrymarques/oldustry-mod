package com.oldustry.block;

import com.mojang.serialization.MapCodec;
import com.oldustry.wrench.Wrenchable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MachineCasingBlock extends Block implements Wrenchable {
    public static final MapCodec<MachineCasingBlock> CODEC = simpleCodec(MachineCasingBlock::new);

    public MachineCasingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<MachineCasingBlock> codec() {
        return CODEC;
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
