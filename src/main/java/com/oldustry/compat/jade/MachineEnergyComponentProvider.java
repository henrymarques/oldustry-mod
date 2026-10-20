package com.oldustry.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** Client-only Jade tooltip: reads {@link MachineEnergyProvider} sync data. */
public enum MachineEnergyComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        MachineEnergyProvider.INSTANCE.decodeFromData(accessor).ifPresent(data -> {
            if (data.capacity() > 0) {
                tooltip.add(Component.translatable(
                    "jade.oldustry.eu",
                    data.energy(),
                    data.capacity()
                ));
            }
            if (data.duration() > 0) {
                int pct = Math.min(100, data.progress() * 100 / data.duration());
                tooltip.add(Component.translatable("jade.oldustry.progress", pct));
            }
        });
    }

    @Override
    public Identifier getUid() {
        return MachineEnergyProvider.UID;
    }
}
