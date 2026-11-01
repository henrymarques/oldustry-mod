package com.oldustry.reactor;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** IC2 {@code ItemReactorHeatStorage}. 10k, 30k and 60k coolant, and the base of exchangers and vents. */
public class OldustryHeatStorageItem extends Item implements OldustryReactorComponent {
    protected final int capacity;

    public OldustryHeatStorageItem(int capacity, Properties properties) {
        super(properties);
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public void processChamber(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {}

    @Override
    public boolean acceptUraniumPulse(OldustryReactorGrid reactor, ItemStack stack, ItemStack pulser, int x, int y, int pulseX, int pulseY) {
        return false;
    }

    @Override
    public boolean canStoreHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return true;
    }

    @Override
    public int maxHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return capacity;
    }

    @Override
    public int currentHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y) {
        return OldustryReactorStore.heat(stack);
    }

    @Override
    public int alterHeat(OldustryReactorGrid reactor, ItemStack stack, int x, int y, int heat) {
        int current = OldustryReactorStore.heat(stack);
        if (OldustryReactor.heatMelts(current, heat, capacity)) {
            reactor.setItemAt(x, y, ItemStack.EMPTY);
        } else {
            OldustryReactorStore.setHeat(stack, OldustryReactor.storedHeat(current, heat, capacity));
        }
        return OldustryReactor.heatLeftover(current, heat, capacity);
    }

    @Override
    public float influenceExplosion(OldustryReactorGrid reactor, ItemStack stack) {
        return 0f;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return OldustryReactorStore.heat(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return OldustryReactorStore.bar(OldustryReactorStore.heat(stack), capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = OldustryReactorStore.heat(stack) / (float) capacity;
        return fill > 0.85f ? 0xFF5555 : (fill > 0.5f ? 0xFFAA00 : 0x55FFFF);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("info.oldustry.reactor.heat", OldustryReactorStore.heat(stack), capacity).withStyle(ChatFormatting.GRAY));
    }
}
