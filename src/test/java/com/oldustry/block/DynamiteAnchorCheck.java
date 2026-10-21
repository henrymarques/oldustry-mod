package com.oldustry.block;

public final class DynamiteAnchorCheck {
    public static void main(String[] args) {
        assert DynamiteAnchor.choose(true, true, true, true, true) == DynamiteAnchor.FLOOR;
        assert DynamiteAnchor.choose(false, true, true, true, true) == DynamiteAnchor.WEST;
        assert DynamiteAnchor.choose(false, false, true, true, true) == DynamiteAnchor.EAST;
        assert DynamiteAnchor.choose(false, false, false, true, true) == DynamiteAnchor.NORTH;
        assert DynamiteAnchor.choose(false, false, false, false, true) == DynamiteAnchor.SOUTH;
        assert DynamiteAnchor.choose(false, false, false, false, false) == null;

        assert DynamiteAnchor.FLOOR.detached(false, true, false, false, false);
        assert !DynamiteAnchor.WEST.detached(false, true, false, false, false);
        assert DynamiteAnchor.WEST.detached(true, false, true, true, true);

        System.out.println("DynamiteAnchorCheck ok");
    }
}
