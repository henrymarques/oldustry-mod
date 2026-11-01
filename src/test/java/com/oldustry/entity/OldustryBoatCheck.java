package com.oldustry.entity;

import com.oldustry.entity.OldustryBoatStats.Kind;

public final class OldustryBoatCheck {
    public static void main(String[] args) {
        assert !OldustryBoatStats.crashes(0.23, Kind.RUBBER.breakMotion);
        assert OldustryBoatStats.crashes(0.24, Kind.RUBBER.breakMotion);
        assert !OldustryBoatStats.crashes(0.4, Kind.CARBON.breakMotion);
        assert OldustryBoatStats.crashes(0.41, Kind.CARBON.breakMotion);
        assert !OldustryBoatStats.crashes(0.5, Kind.ELECTRIC.breakMotion);
        assert OldustryBoatStats.crashes(0.51, Kind.ELECTRIC.breakMotion);

        assert !OldustryBoatStats.rubberSplits(0.26);
        assert OldustryBoatStats.rubberSplits(0.27);
        assert !OldustryBoatStats.paysEu(0.1);
        assert OldustryBoatStats.paysEu(0.11);
        assert Kind.ELECTRIC.lava && !Kind.RUBBER.lava && !Kind.CARBON.lava;
        assert OldustryBoatStats.ELECTRIC_COAST < Kind.ELECTRIC.breakMotion;
        assert OldustryBoatStats.ELECTRIC_TOP > Kind.ELECTRIC.breakMotion;
        assert OldustryBoatStats.PLAIN_TOP < Kind.CARBON.breakMotion;
        assert OldustryBoatStats.PLAIN_TOP > Kind.RUBBER.breakMotion;

        System.out.println("OldustryBoatCheck ok");
    }
}
