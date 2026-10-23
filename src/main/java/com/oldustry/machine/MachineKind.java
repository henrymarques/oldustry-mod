package com.oldustry.machine;

import net.minecraft.util.StringRepresentable;

public enum MachineKind implements StringRepresentable {
    GENERATOR("generator", Type.GENERATOR, 4000, 10, 1, 0.9f, 2),
    GEO_GENERATOR("geo_generator", Type.GEO, 20, 20, 1, 0.9f, 1),
    SOLAR_PANEL("solar_panel", Type.SOLAR, 1, 1, 1, 0.9f, 0),
    WATER_MILL("water_mill", Type.WATER, 2, 2, 1, 0.9f, 1),
    WIND_MILL("wind_mill", Type.WIND, 4, 4, 1, 0.9f, 0),
    BATBOX("batbox", Type.STORAGE, 40000, 32, 1, 1.0f, 2),
    MFE("mfe", Type.STORAGE, 600000, 128, 2, 1.0f, 2),
    MFSU("mfsu", Type.STORAGE, 10000000, 512, 3, 1.0f, 2),
    LV_TRANSFORMER("lv_transformer", Type.TRANSFORMER, 256, 32, 1, 0.9f, 0),
    MV_TRANSFORMER("mv_transformer", Type.TRANSFORMER, 1024, 128, 2, 0.9f, 0),
    HV_TRANSFORMER("hv_transformer", Type.TRANSFORMER, 4096, 512, 3, 0.9f, 0),
    MACERATOR("macerator", Type.MACERATOR, 800, 2, 1, 0.85f, 7),
    EXTRACTOR("extractor", Type.EXTRACTOR, 800, 2, 1, 0.85f, 7),
    COMPRESSOR("compressor", Type.COMPRESSOR, 800, 2, 1, 0.85f, 7),
    RECYCLER("recycler", Type.RECYCLER, 45, 1, 1, 0.85f, 7),
    ELECTRIC_FURNACE("electric_furnace", Type.FURNACE, 390, 3, 1, 1.0f, 7),
    LUMINATOR("luminator", Type.LUMINATOR, 10000, 32, 1, 1.0f, 0),
    MINER("miner", Type.MINING, 1000, 32, 1, 0.85f, 7),
    PUMP("pump", Type.PUMPING, 200, 128, 2, 0.85f, 3),
    CANNER("canner", Type.CANNING, 600, 32, 1, 0.85f, 4),
    ELECTROLYZER("electrolyzer", Type.ELECTROLYZER, 20000, 0, 1, 0.8f, 2),
    MAGNETIZER("magnetizer", Type.MAGNETIZER, 100, 32, 1, 0.8f, 0),
    INDUCTION("induction", Type.INDUCTION, 10000, 128, 2, 0.8f, 5);

    public enum Type {
        GENERATOR,
        GEO,
        SOLAR,
        WATER,
        WIND,
        STORAGE,
        TRANSFORMER,
        MACERATOR,
        EXTRACTOR,
        COMPRESSOR,
        RECYCLER,
        FURNACE,
        LUMINATOR,
        MINING,
        PUMPING,
        CANNING,
        ELECTROLYZER,
        MAGNETIZER,
        INDUCTION
    }

    private final String id;
    public final Type type;
    public final int capacity;
    public final int packet;
    public final int tier;
    public final float wrenchRate;
    public final int slots;

    MachineKind(String id, Type type, int capacity, int packet, int tier, float wrenchRate, int slots) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.packet = packet;
        this.tier = tier;
        this.wrenchRate = wrenchRate;
        this.slots = slots;
    }

    public int duration() {
        return switch (type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR -> 400;
            case RECYCLER -> 45;
            case FURNACE -> 130;
            case CANNING -> 600;
            case INDUCTION -> 4000;
            default -> 0;
        };
    }

    public int euPerTick() {
        return switch (type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR -> 2;
            case RECYCLER -> 1;
            case FURNACE -> 3;
            case CANNING -> 1;
            case INDUCTION -> 15;
            default -> packet;
        };
    }

    public int buffer() {
        return switch (type) {
            case GEO -> 32;
            default -> capacity;
        };
    }

    /** Macerator-family GUIs have upgrade slots. A single-use battery waits for a storage upgrade there. */
    public boolean suNeedsStorageUpgrade() {
        return switch (type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE -> true;
            default -> false;
        };
    }

    public int highPacket() {
        return switch (this) {
            case LV_TRANSFORMER -> 128;
            case MV_TRANSFORMER -> 512;
            case HV_TRANSFORMER -> 2048;
            default -> packet;
        };
    }

    public boolean verticalFacing() {
        return type == Type.STORAGE || type == Type.TRANSFORMER;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
