package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

final class OldustryStorageBlockEntity extends OldustryMachineBlockEntity {
    private static final Component[] MODE_MSG = {
        Component.translatable("info.oldustry.storage.mode.nothing"),
        Component.translatable("info.oldustry.storage.mode.full"),
        Component.translatable("info.oldustry.storage.mode.partial"),
        Component.translatable("info.oldustry.storage.mode.empty"),
        Component.translatable("info.oldustry.storage.mode.no_output"),
        Component.translatable("info.oldustry.storage.mode.output_full")
    };

    private int mode;

    OldustryStorageBlockEntity(OldustryMachineKind kind, BlockPos pos, BlockState state) {
        super(kind, pos, state);
    }

    public void cycleMode(Player player) {
        mode = (mode + 1) % 6;
        if (player != null) {
            player.sendSystemMessage(MODE_MSG[mode]);
        }
        setChanged();
    }

    public int redstonePower() {
        int cap = capacity();
        int packet = kind.packet;
        return switch (mode) {
            case 1 -> energy >= cap && cap > 0 ? 15 : 0;
            case 2 -> energy > packet && energy < cap ? 15 : 0;
            case 3 -> energy < packet ? 15 : 0;
            default -> 0;
        };
    }

    boolean allowOutput() {
        // JAR TileEntityElectricBlock: modes 4/5 gate on redstone input.
        boolean powered = level != null && level.hasNeighborSignal(worldPosition);
        return switch (mode) {
            case 4 -> !powered;
            case 5 -> !powered || energy >= capacity();
            default -> true;
        };
    }

    boolean tickStorage() {
        chargeSlot(0);
        dischargeSlot(1);
        return redstonePower() > 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("mode", mode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mode = input.getIntOr("mode", 0);
    }
}
