package com.oldustry.item;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModDataComponents;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ToolboxItem extends Item {
    public ToolboxItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack toolbox = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        List<ItemStack> stored = new ArrayList<>(toolbox.getOrDefault(ModDataComponents.BOX.get(), List.of()));
        Inventory inv = player.getInventory();
        if (stored.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                ItemStack slot = inv.getItem(i);
                if (slot.isEmpty() || slot == toolbox || !boxable(slot)) {
                    stored.add(ItemStack.EMPTY);
                    continue;
                }
                stored.add(slot.copy());
                inv.setItem(i, ItemStack.EMPTY);
            }
        } else {
            for (int i = 0; i < stored.size() && i < 8; i++) {
                ItemStack packed = stored.get(i);
                if (packed.isEmpty()) {
                    continue;
                }
                if (inv.getItem(i).isEmpty()) {
                    inv.setItem(i, packed.copy());
                    stored.set(i, ItemStack.EMPTY);
                }
            }
            stored.removeIf(s -> s == null || s.isEmpty());
        }
        toolbox.set(ModDataComponents.BOX.get(), List.copyOf(stored));
        return InteractionResult.SUCCESS;
    }

    private static boolean boxable(ItemStack stack) {
        if (stack.getCount() > 1 && !stack.is(ModBlocks.WOODEN_SCAFFOLD.get().asItem())) {
            return false;
        }
        return true;
    }
}
