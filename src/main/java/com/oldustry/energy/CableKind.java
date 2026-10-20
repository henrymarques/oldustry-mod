package com.oldustry.energy;

import net.minecraft.util.StringRepresentable;

/** IC2 1.117.385-lf TileEntityCable: loss, melt threshold (exclusive), insulation absorption. */
public enum CableKind implements StringRepresentable {
    COPPER(0.2, 33, 32, 1, 6, "insulated_copper_cable"),
    COPPER_BARE(0.3, 33, 8, 0, 4, "copper_cable"),
    GOLD_BARE(0.5, 129, 8, 0, 3, "gold_cable"),
    GOLD(0.45, 129, 32, 1, 5, "insulated_gold_cable"),
    GOLD_2X(0.4, 129, 128, 2, 6, "2x_insulated_gold_cable"),
    HV_BARE(1.0, 2049, 0, 0, 6, "hv_cable"),
    HV(0.95, 2049, 128, 1, 8, "insulated_hv_cable"),
    HV_2X(0.9, 2049, 512, 2, 10, "2x_insulated_hv_cable"),
    HV_3X(0.8, 2049, 9001, 3, 12, "3x_insulated_hv_cable"),
    GLASS(0.025, 513, 9001, 0, 4, "glass_fibre_cable"),
    TIN(0.025, 6, 3, 0, 5, "tin_cable"),
    DETECTOR(0.5, 2049, 9001, 0, 8, "detector_cable"),
    SPLITTER(0.5, 2049, 9001, 0, 8, "splitter_cable");

    public final double loss;
    public final int meltAt;
    public final int insulation;
    public final int insulationLayers;
    public final int thickness;
    public final String id;

    CableKind(double loss, int meltAt, int insulation, int insulationLayers, int thickness, String id) {
        this.loss = loss;
        this.meltAt = meltAt;
        this.insulation = insulation;
        this.insulationLayers = insulationLayers;
        this.thickness = thickness;
        this.id = id;
    }

    public CableKind strip() {
        return switch (this) {
            case COPPER -> COPPER_BARE;
            case GOLD -> GOLD_BARE;
            case GOLD_2X -> GOLD;
            case HV -> HV_BARE;
            case HV_2X -> HV;
            case HV_3X -> HV_2X;
            default -> this;
        };
    }

    public CableKind insulate() {
        return switch (this) {
            case COPPER_BARE -> COPPER;
            case GOLD_BARE -> GOLD;
            case GOLD -> GOLD_2X;
            case HV_BARE -> HV;
            case HV -> HV_2X;
            case HV_2X -> HV_3X;
            default -> this;
        };
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
