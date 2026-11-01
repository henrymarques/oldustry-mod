package com.oldustry.item;

import com.oldustry.item.tool.MiningLaserItem;
import com.oldustry.item.tool.OldustryLaser;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryItems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ModeSwitch {
    public static void toggle(Player player) {
        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof MiningLaserItem) {
            int next = OldustryLaser.next(MiningLaserItem.mode(held));
            if (next == 0) {
                held.remove(OldustryDataComponents.LASER_MODE.get());
            } else {
                held.set(OldustryDataComponents.LASER_MODE.get(), next);
            }
            if (player instanceof ServerPlayer server) {
                server.sendSystemMessage(Component.translatable(
                    "info.oldustry.laser.mode",
                    Component.translatable("info.oldustry.laser.mode." + next)
                ));
            }
        } else if (held.is(OldustryItems.ELECTRIC_WRENCH.get())) {
            boolean next = !held.getOrDefault(OldustryDataComponents.LOSSLESS.get(), false);
            held.set(OldustryDataComponents.LOSSLESS.get(), next);
            if (player instanceof ServerPlayer server) {
                server.sendSystemMessage(Component.translatable(next ? "info.oldustry.wrench.lossless.on" : "info.oldustry.wrench.lossless.off"), true);
            }
        }
    }

    private ModeSwitch() {}
}
