package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class OldustryMachineTransfer {
    /** Insert via NeoForge item ResourceHandler; Container fallback. */
    public static int insert(ServerLevel level, BlockPos pos, Direction side, ItemStack stack, int amount) {
        if (amount <= 0) {
            return 0;
        }
        var handler = level.getCapability(Capabilities.Item.BLOCK, pos, side);
        if (handler != null) {
            ItemStack toMove = stack.copyWithCount(amount);
            try (Transaction tx = Transaction.openRoot()) {
                ItemStack left = ItemUtil.insertItemReturnRemaining(handler, toMove, false, tx);
                tx.commit();
                return amount - left.getCount();
            }
        }
        if (!(level.getBlockEntity(pos) instanceof Container container)) {
            return 0;
        }
        int left = amount;
        for (int i = 0; i < container.getContainerSize() && left > 0; i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                int n = Math.min(left, stack.getMaxStackSize());
                container.setItem(i, stack.copyWithCount(n));
                left -= n;
            } else if (ItemStack.isSameItemSameComponents(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int n = Math.min(left, slot.getMaxStackSize() - slot.getCount());
                slot.grow(n);
                left -= n;
            }
        }
        if (left < amount) {
            container.setChanged();
        }
        return amount - left;
    }

    private OldustryMachineTransfer() {}
}
