package com.oldustry.block;

import java.util.Random;

/** Pure tap state machine. No Minecraft types so the check can run without bootstrap. */
public final class RubberTapLogic {
    public record Result(int resin, boolean hole, boolean wet) {}

    public static Result extract(boolean hole, boolean wet, Random random) {
        if (!hole) {
            return new Result(0, false, false);
        }
        if (wet) {
            return new Result(random.nextInt(3) + 1, true, false);
        }
        // IC2 ItemTreetap dry: two independent rolls. Close is first (meta → 1), then 1 resin.
        boolean keepHole = random.nextInt(5) != 0;
        int resin = random.nextInt(5) == 0 ? 1 : 0;
        return new Result(resin, keepHole, false);
    }

    public static boolean refillDry(Random random) {
        return random.nextInt(200) == 0;
    }

    /** IC2: 1/6 on grown trunks (tree gen used meta 1; drop was `meta != 0`). */
    public static boolean extraResinOnBreak(Random random) {
        return random.nextInt(6) == 0;
    }

    private RubberTapLogic() {}
}
