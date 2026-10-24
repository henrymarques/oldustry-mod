package com.oldustry.item.tool;

import com.oldustry.wrench.WrenchHandler;

/** IC2 electric wrench: EU = amount × 50 (rotate 1, remove 10, lossless remove 200). */
public final class WrenchEu {
    public static final int ROTATE_AMOUNT = 1;
    public static final int REMOVE_AMOUNT = 10;
    public static final int LOSSLESS_REMOVE_AMOUNT = 200;

    private WrenchEu() {}

    public static int amount(WrenchHandler.Result result, boolean lossless) {
        return switch (result) {
            case ROTATE -> ROTATE_AMOUNT;
            case REMOVE -> lossless ? LOSSLESS_REMOVE_AMOUNT : REMOVE_AMOUNT;
            case PASS -> 0;
        };
    }

    public static int eu(WrenchHandler.Result result, boolean lossless) {
        return amount(result, lossless) * 50;
    }
}
