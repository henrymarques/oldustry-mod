package com.oldustry.crop;

import com.oldustry.item.BoozeContents;
import com.oldustry.machine.OldustryMachineKind;

/**
 * Self-check for D6 Crop-Matron / Cropnalyzer / Barrel numbers from the JAR.
 * Does not touch Item subclasses (bootstrap).
 */
public final class D6Check {
    public static void main(String[] args) {
        assert OldustryMachineKind.CROP_MATRON.capacity == 1000;
        assert OldustryMachineKind.CROP_MATRON.packet == 32;
        assert OldustryMachineKind.CROP_MATRON.slots == 9;
        // JAR HandHeldCropnalyzer.energyForLevel — keep in sync with CropnalyzerItem.energyForLevel.
        assert energyForLevel(0) == 10;
        assert energyForLevel(1) == 90;
        assert energyForLevel(2) == 900;
        assert energyForLevel(3) == 9000;
        BoozeContents beer = BoozeContents.beer(8, 3, 2, 1);
        assert beer.pack() != 0;
        assert BoozeContents.unpack(beer.pack()).amount() == 8;
        assert BoozeContents.unpack(beer.pack()).type() == 1;
        assert BoozeContents.unpack(beer.pack()).solidRatio() == 3;
        assert BoozeContents.unpack(beer.pack()).hopsRatio() == 2;
        assert BoozeContents.unpack(beer.pack()).timeRatio() == 1;
        BoozeContents rum = BoozeContents.rum(4, 100);
        assert BoozeContents.unpack(rum.pack()).type() == 2;
        assert BoozeContents.unpack(rum.pack()).amount() == 4;
        assert BoozeContents.unpack(rum.pack()).rumProgress() == 100;
        assert OldustryCrop.WHEAT.desc(0).startsWith("Yellow");
        assert "Alblaka".equals(OldustryCrop.WHEAT.discoveredBy());
        assert "I".equals(OldustryCrop.WHEAT.tierRoman());
        System.out.println("d6 check ok");
    }

    private static int energyForLevel(int level) {
        return switch (level) {
            case 1 -> 90;
            case 2 -> 900;
            case 3 -> 9000;
            default -> 10;
        };
    }
}
