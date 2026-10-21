package com.oldustry.block;

import net.minecraft.util.StringRepresentable;

/**
 * Where a placed dynamite stick is held. Priority matches {@code BlockDynamite.onBlockAdded}:
 * floor, then west, east, north, south.
 */
public enum DynamiteAnchor implements StringRepresentable {
    FLOOR("floor"),
    WEST("west"),
    EAST("east"),
    NORTH("north"),
    SOUTH("south");

    private final String name;

    DynamiteAnchor(String name) {
        this.name = name;
    }

    /** {@code null} when no full-cube neighbor can hold the stick. */
    public static DynamiteAnchor choose(boolean down, boolean west, boolean east, boolean north, boolean south) {
        if (down) {
            return FLOOR;
        }
        if (west) {
            return WEST;
        }
        if (east) {
            return EAST;
        }
        if (north) {
            return NORTH;
        }
        if (south) {
            return SOUTH;
        }
        return null;
    }

    public boolean detached(boolean down, boolean west, boolean east, boolean north, boolean south) {
        return switch (this) {
            case FLOOR -> !down;
            case WEST -> !west;
            case EAST -> !east;
            case NORTH -> !north;
            case SOUTH -> !south;
        };
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
