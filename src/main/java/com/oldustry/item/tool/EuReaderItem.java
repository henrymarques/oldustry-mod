package com.oldustry.item.tool;

import java.text.DecimalFormat;

import com.oldustry.energy.EnergyNet;
import com.oldustry.energy.EnergyTile;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/**
 * IC2 1.117 {@code ItemToolMeter}: two-click average EU/t via EnergyNet totals.
 * Same tile again → report + roll baseline (continuous); other tile → new measurement.
 */
public class EuReaderItem extends Item {
    private static final DecimalFormat POWER = new DecimalFormat("0.##");

    public EuReaderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) {
            return InteractionResult.PASS;
        }
        if (!(context.getLevel() instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        // JAR: EnergyNet.getTileEntity — only tiles registered in the net.
        BlockPos pos = context.getClickedPos();
        EnergyTile tile = EnergyNet.of(server).get(pos);
        if (tile == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        long emitted = tile.totalEmitted();
        long sunken = tile.totalSunken();
        long now = server.getGameTime();
        BlockPos last = stack.get(OldustryDataComponents.MEASURE_POS.get());
        if (last == null || !last.equals(pos)) {
            stack.set(OldustryDataComponents.MEASURE_POS.get(), pos.immutable());
            context.getPlayer().sendSystemMessage(Component.translatable("info.oldustry.eu_reader.start"));
        } else {
            long ticks = Math.max(1, now - stack.getOrDefault(OldustryDataComponents.MEASURE_TICK.get(), 0L));
            double out = (emitted - stack.getOrDefault(OldustryDataComponents.MEASURE_EMITTED.get(), 0L)) / (double) ticks;
            double in = (sunken - stack.getOrDefault(OldustryDataComponents.MEASURE_SUNKEN.get(), 0L)) / (double) ticks;
            context.getPlayer().sendSystemMessage(
                Component.translatable("info.oldustry.eu_reader.result", POWER.format(in), POWER.format(out), POWER.format(in - out), ticks)
            );
        }
        // JAR always refreshes baselines after either branch (continuous windows).
        stack.set(OldustryDataComponents.MEASURE_TICK.get(), now);
        stack.set(OldustryDataComponents.MEASURE_EMITTED.get(), emitted);
        stack.set(OldustryDataComponents.MEASURE_SUNKEN.get(), sunken);
        return InteractionResult.SUCCESS;
    }
}
