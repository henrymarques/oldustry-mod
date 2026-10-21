package com.oldustry.machine;

import java.util.function.IntUnaryOperator;

/** JAR {@code TileEntityMiner.getOperationHeight}: first block under the miner that is not a mining pipe. */
public final class MinerColumn {
    public static final int PIPE = 0;
    public static final int TIP = 1;
    public static final int AIR = 2;
    public static final int OTHER = 3;

    public static int head(int originY, int minY, IntUnaryOperator cell) {
        for (int y = originY - 1; y >= minY; y--) {
            if (cell.applyAsInt(y) != PIPE) {
                return y;
            }
        }
        return minY - 1;
    }

    private MinerColumn() {}
}
