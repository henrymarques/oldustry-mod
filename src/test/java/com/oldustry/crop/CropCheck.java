package com.oldustry.crop;

public final class CropCheck {
    public static void main(String[] args) {
        assert OldustryCrop.byId(0) == OldustryCrop.WEED;
        assert OldustryCrop.byId(1) == OldustryCrop.WHEAT;
        assert OldustryCrop.byId(21) == OldustryCrop.POTATO;
        assert OldustryCrop.byId(-1) == OldustryCrop.NONE;
        assert OldustryCrop.WHEAT.tier() == 1 && OldustryCrop.WHEAT.maxSize() == 7;
        assert OldustryCrop.WHEAT.duration(1) == 200;
        assert OldustryCrop.WEED.duration(1) == 300;
        assert OldustryCrop.COFFEE.duration(3) == 700 && OldustryCrop.COFFEE.duration(4) == 2100;
        assert OldustryCrop.FERRU.duration(3) == 2000 && OldustryCrop.FERRU.duration(2) == 800;
        assert !OldustryCrop.WHEAT.canGrow(3, 8, 0, false, false);
        assert OldustryCrop.WHEAT.canGrow(3, 9, 0, false, false);
        assert OldustryCrop.FERRU.canGrow(3, 0, 0, true, false);
        assert !OldustryCrop.FERRU.canGrow(3, 0, 0, false, false);
        assert OldustryCrop.COCOA.weight(10, 10, 10) == (int) (10 * 0.8 + 10 * 1.3 + 10 * 0.9);
        assert OldustryCropMath.statDiffScore(0) == 3;
        assert OldustryCropMath.statDiffScore(1) == 1;
        assert OldustryCropMath.statDiffScore(2) == 0;
        assert OldustryCropMath.statDiffScore(4) == 2;
        assert OldustryCropMath.ratio(OldustryCrop.WHEAT, OldustryCrop.WHEAT) == 500;
        assert OldustryCropMath.humidity(2, 7, 5) == 2 + 2 + 2 + (5 + 24) / 25;
        assert OldustryCropMath.nutrients(1, 2, 19) == 1 + 2 + (19 + 19) / 20;
        assert OldustryCropMath.air(64, 9, true) == 4 + 2;
        assert OldustryCropMath.air(63, 9, false) == 4;
        assert OldustryCropMath.envHave(12) == 60;
        // plains-ish: humidity 2 + nutrients 1 + air 6 = 9 → have 45; wheat 1/1/1 need 3
        int have = OldustryCropMath.envHave(OldustryCrop.WHEAT.weight(2, 1, 6));
        assert have == 45;
        int grew = OldustryCropMath.growthRate(OldustryCrop.WHEAT, 1, 1, 1, have, 0, 0);
        assert grew == (3 + 0 + 1) * (100 + have - 3) / 100;
        // without ×5 the same weight would starve a 10/10/10 plant; with ×5 it still grows
        assert OldustryCropMath.growthRate(OldustryCrop.WHEAT, 10, 10, 10, OldustryCropMath.envHave(9), 0, 0)
            == (3 + 0 + 10) * (100 + 45 - 30) / 100;
        assert OldustryCropMath.growthRate(OldustryCrop.WHEAT, 10, 10, 10, 9, 0, 0) < 13 / 2;
        assert OldustryCropMath.growthRate(OldustryCrop.WHEAT, 31, 31, 0, 0, 0, 31) == -1;
        assert OldustryCropMath.crossedStat(4, 2, 0) == 0;
        assert OldustryCropMath.crossedStat(4, 2, 5) == 5;
        assert OldustryCropMath.gainDrops(1.0f, 0, () -> 0.5) == 1;
        int[] stay = OldustryCropMath.weedStep(0);
        int[] west = OldustryCropMath.weedStep(1);
        int[] north = OldustryCropMath.weedStep(3);
        assert stay[0] == 0 && stay[1] == 0;
        assert west[0] == -1 && west[1] == 0;
        assert north[0] == 0 && north[1] == -1;
        assert OldustryCrop.WHEAT.plainSeed(1, 1, 1);
        assert !OldustryCrop.WHEAT.plainSeed(2, 1, 1);
        assert "Ingrident".equals(OldustryCrop.COFFEE.attributes()[1]);
        System.out.println("crop check ok");
    }
}
