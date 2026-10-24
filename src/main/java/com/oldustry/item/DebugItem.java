package com.oldustry.item;

import com.oldustry.energy.EnergyTile;
import com.oldustry.machine.OldustryMachineBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DebugItem extends Item {
    public DebugItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!player.getAbilities().instabuild) {
            return InteractionResult.PASS;
        }
        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
        if (be == null) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        String msg = be.getClass().getSimpleName() + " " + be.getBlockPos();
        if (be instanceof EnergyTile tile) {
            msg += " offer=" + tile.offerEnergy() + " demand=" + tile.demand()
                + " maxIn=" + tile.maxSafeInput()
                + " emitted=" + tile.totalEmitted() + " sunken=" + tile.totalSunken();
        }
        if (be instanceof OldustryMachineBlockEntity machine) {
            msg += " energy=" + machine.energy() + "/" + machine.energyCapacity();
        }
        player.sendSystemMessage(Component.literal(msg));
        return InteractionResult.SUCCESS;
    }
}
