package com.oldustry.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public interface EnergyTile {
    BlockPos energyPos();

    EnergyNetLogic.Role energyRole();

    default boolean emits(Direction face) {
        return energyRole() == EnergyNetLogic.Role.SOURCE || energyRole() == EnergyNetLogic.Role.BOTH;
    }

    default boolean accepts(Direction face) {
        return energyRole() == EnergyNetLogic.Role.SINK || energyRole() == EnergyNetLogic.Role.BOTH;
    }

    default boolean conducts() {
        return energyRole() == EnergyNetLogic.Role.WIRE;
    }

    default double cableLoss() {
        return 0;
    }

    default int meltAt() {
        return Integer.MAX_VALUE;
    }

    default int color() {
        return 0;
    }

    default boolean foamed() {
        return false;
    }

    default boolean open() {
        return true;
    }

    default int insulation() {
        return 9001;
    }

    int demand();

    int acceptEnergy(int amount);

    int offerEnergy();

    void takeEnergy(int amount);

    /** JAR transformer emits up to 4× low packet per tick when stepping down. */
    default int emitBursts() {
        return 1;
    }

    default int maxSafeInput() {
        return Integer.MAX_VALUE;
    }

    default long totalEmitted() {
        return 0;
    }

    default long totalSunken() {
        return 0;
    }

    default void addEmitted(long amount) {}

    default void addSunken(long amount) {}

    default void onFlow(int amount) {}

    default void melt(ServerLevel level) {}

    default void explode(ServerLevel level) {}
}
