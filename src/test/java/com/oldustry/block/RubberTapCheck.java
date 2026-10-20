package com.oldustry.block;

import java.util.Random;

public final class RubberTapCheck {
    public static void main(String[] args) {
        var wet = RubberTapLogic.extract(true, true, new Random(1));
        assert wet.resin() >= 1 && wet.resin() <= 3 : wet;
        assert wet.hole() && !wet.wet() : wet;

        int drops = 0;
        int resets = 0;
        var r = new Random(42);
        for (int i = 0; i < 10_000; i++) {
            var dry = RubberTapLogic.extract(true, false, r);
            if (dry.resin() > 0) {
                drops++;
            }
            if (!dry.hole()) {
                resets++;
            }
        }
        assert drops > 1500 && drops < 2500 : drops;
        assert resets > 1500 && resets < 2500 : resets;

        int both = 0;
        var bothRng = new Random(42);
        for (int i = 0; i < 10_000; i++) {
            var dry = RubberTapLogic.extract(true, false, bothRng);
            if (dry.resin() > 0 && !dry.hole()) {
                both++;
            }
        }
        assert both > 200 && both < 600 : both;
        assert !RubberTapLogic.extract(false, true, new Random(0)).hole();

        int extra = 0;
        var extraRng = new Random(7);
        for (int i = 0; i < 10_000; i++) {
            if (RubberTapLogic.extraResinOnBreak(extraRng)) {
                extra++;
            }
        }
        assert extra > 1400 && extra < 1900 : extra;
        System.out.println("RubberTapCheck ok");
    }

    private RubberTapCheck() {}
}
