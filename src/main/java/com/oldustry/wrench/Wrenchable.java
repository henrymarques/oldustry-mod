package com.oldustry.wrench;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface Wrenchable {
    default boolean canSetFacing(BlockState state, Direction face) {
        return false;
    }

    default BlockState setFacing(BlockState state, Direction face) {
        return state;
    }

    default boolean canRemove() {
        return true;
    }

    /** Same as {@link #canRemove()} unless the block cares who is holding the wrench. */
    default boolean canRemove(Player player) {
        return canRemove();
    }

    default float wrenchDropRate() {
        return 1f;
    }

    ItemStack wrenchKeepDrop(BlockState state);

    ItemStack wrenchFailDrop(BlockState state);
}
