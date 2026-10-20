package com.oldustry.item;

import com.oldustry.compat.CuriosWearables;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.registry.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ModeSwitch {
    public static void toggle(Player player) {
        ItemStack held = player.getMainHandItem();
        if (held.is(ModItems.ELECTRIC_WRENCH.get())) {
            boolean next = !held.getOrDefault(ModDataComponents.LOSSLESS.get(), false);
            held.set(ModDataComponents.LOSSLESS.get(), next);
            if (player instanceof ServerPlayer server) {
                server.sendSystemMessage(Component.translatable(next ? "info.oldustry.wrench.lossless.on" : "info.oldustry.wrench.lossless.off"), true);
            }
            return;
        }
        ItemStack chest = CuriosWearables.chestOrCurios(player, ModItems.FUEL_JETPACK.get());
        if (chest.is(ModItems.FUEL_JETPACK.get())) {
            boolean next = !chest.getOrDefault(ModDataComponents.HOVER.get(), false);
            chest.set(ModDataComponents.HOVER.get(), next);
            if (player instanceof ServerPlayer server) {
                server.sendSystemMessage(Component.translatable(next ? "info.oldustry.jetpack.hover.on" : "info.oldustry.jetpack.hover.off"), true);
            }
        }
    }

    private ModeSwitch() {}
}
