package com.oldustry.reactor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/** What a fuel rod sees. IC2 {@code IReactor}, trimmed to the calls the components make. */
public interface OldustryReactorGrid {
    boolean produceEnergy();

    ItemStack itemAt(int x, int y);

    void setItemAt(int x, int y, ItemStack stack);

    float addOutput(float energy);

    int addHeat(int amount);

    int heat();

    void setHeat(int heat);

    int maxHeat();

    void setMaxHeat(int maxHeat);

    float heatEffect();

    void setHeatEffect(float modifier);

    RandomSource random();
}
