package com.oldustry.machine;

import com.oldustry.block.OldustryCropBlockEntity;
import com.oldustry.item.HydratingCellItem;
import com.oldustry.item.WeedExItem;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** JAR {@code TileEntityCropmatron}: LV sink, 9×3×9 scan, fertilizer / hydration / Weed-EX. */
final class OldustryCropMatronBlockEntity extends OldustryMachineBlockEntity {
    private int scanX = -4;
    private int scanY = -1;
    private int scanZ = -4;

    OldustryCropMatronBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.CROP_MATRON, pos, state);
    }

    boolean tickMatron(ServerLevel level, BlockPos pos) {
        if (energy >= 31) {
            scan(level, pos);
        }
        return energy > 0;
    }

    private void scan(ServerLevel level, BlockPos origin) {
        scanX++;
        if (scanX > 4) {
            scanX = -4;
            scanZ++;
            if (scanZ > 4) {
                scanZ = -4;
                scanY++;
                if (scanY > 1) {
                    scanY = -1;
                }
            }
        }
        energy--;
        BlockEntity te = level.getBlockEntity(origin.offset(scanX, scanY, scanZ));
        if (!(te instanceof OldustryCropBlockEntity crop)) {
            return;
        }
        if (!fertilizerStack().isEmpty() && crop.applyFertilizer(false)) {
            energy -= 10;
            consumeFertilizer();
        }
        if (!hydrationStack().isEmpty() && crop.applyHydration(false, this::drainHydration)) {
            energy -= 10;
        }
        if (!weedExStack().isEmpty() && crop.applyWeedEx(false)) {
            energy -= 10;
            damageWeedEx();
        }
    }

    private ItemStack fertilizerStack() {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack hydrationStack() {
        for (int i = 3; i < 6; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack weedExStack() {
        for (int i = 6; i < 9; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private void consumeFertilizer() {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = items.get(i);
            if (stack.is(OldustryItems.FERTILIZER.get())) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    items.set(i, ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    private int drainHydration(int want) {
        int taken = 0;
        for (int i = 3; i < 6 && taken < want; i++) {
            ItemStack stack = items.get(i);
            int got = HydratingCellItem.consume(stack, want - taken);
            taken += got;
            if (stack.getItem() instanceof HydratingCellItem && HydratingCellItem.remaining(stack) <= 0) {
                items.set(i, ItemStack.EMPTY);
            }
        }
        return taken;
    }

    private void damageWeedEx() {
        for (int i = 6; i < 9; i++) {
            ItemStack stack = items.get(i);
            if (stack.getItem() instanceof WeedExItem && WeedExItem.spend(stack)) {
                if (WeedExItem.uses(stack) <= 0) {
                    items.set(i, ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("scanX", scanX);
        output.putInt("scanY", scanY);
        output.putInt("scanZ", scanZ);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        scanX = input.getIntOr("scanX", -4);
        scanY = input.getIntOr("scanY", -1);
        scanZ = input.getIntOr("scanZ", -4);
    }
}
