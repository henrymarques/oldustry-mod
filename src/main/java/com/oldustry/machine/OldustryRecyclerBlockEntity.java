package com.oldustry.machine;

import com.oldustry.energy.ProcessLogic;
import com.oldustry.registry.OldustryItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class OldustryRecyclerBlockEntity extends OldustryProcessMachineBlockEntity {
    public OldustryRecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.RECYCLER, pos, state);
    }

    @Override
    protected ItemStack processResult(ServerLevel level, ItemStack in) {
        return in.isEmpty() ? ItemStack.EMPTY : new ItemStack(OldustryItems.SCRAP.get());
    }

    @Override
    protected boolean acceptsInput(ItemStack in) {
        return !OldustryProcessingRecipes.recyclerBlacklist(in.getItem());
    }

    @Override
    protected void craftResult(ServerLevel level, ItemStack result) {
        if (ProcessLogic.recyclerScrap(level.getRandom().nextInt(8), 8)
            && canOutput(items.get(1), result)) {
            mergeOut(1, result);
        }
    }
}
