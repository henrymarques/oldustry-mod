package com.oldustry.item;

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
        }
    }

    private ModeSwitch() {}
}
