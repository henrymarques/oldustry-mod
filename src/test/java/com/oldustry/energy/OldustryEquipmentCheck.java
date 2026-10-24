package com.oldustry.energy;

import com.oldustry.item.BronzeStats;
import com.oldustry.item.tool.ChainsawStats;
import com.oldustry.item.tool.DrillStats;

public final class OldustryEquipmentCheck {
    public static void main(String[] args) {
        assert pin(BronzeStats.DURABILITY) == 350 && BronzeStats.DURABILITY > 250;
        assert pin(BronzeStats.SPEED) == 6.5f && BronzeStats.SPEED > 6f;
        assert pin(BronzeStats.ENCHANT) == 14;
        assert pin(DrillStats.MINING_SPEED) == 10f;
        assert pin(DrillStats.DIAMOND_SPEED) == 20f;
        assert pin(DrillStats.DIAMOND_SPEED) == 2f * DrillStats.MINING_SPEED;
        // JAR EU costs unchanged despite speed desvio
        assert pin(DrillStats.MINING_EU) == 50;
        assert pin(DrillStats.DIAMOND_EU) == 80;
        assert pin(com.oldustry.item.equipment.JetpackFuel.MAX) == 17998;
        assert com.oldustry.item.equipment.JetpackFuel.afterUse(17998, 9) == 17985;
        assert com.oldustry.item.equipment.JetpackFuel.afterUse(20, 6) == 10;
        assert com.oldustry.item.equipment.JetpackFuel.afterUse(10, 9) == 0;
        assert com.oldustry.item.equipment.JetpackFuel.afterCanner(0, 6 * 2548) == 15286;
        assert com.oldustry.item.equipment.JetpackFuel.afterCanner(15286, 6 * 2548) == 17998;
        // Electric jetpack: JAR power 0.7, ceiling worldHeight/1.28, consume 9-2 / 6-2, partial drain.
        assert pin(com.oldustry.item.equipment.JetpackFlight.ELECTRIC.power()) == 0.7f;
        assert com.oldustry.item.equipment.JetpackFlight.ceiling(256, false) == 256;
        assert com.oldustry.item.equipment.JetpackFlight.ceiling(256, true) == 200;
        assert com.oldustry.item.equipment.JetpackFlight.scaledPower(0.7f, 30000, 30000, 0.05f) == 0.7f;
        assert com.oldustry.item.equipment.JetpackFlight.scaledPower(1f, 17998, 17998, 0.2f) == 1f;
        assert com.oldustry.item.equipment.JetpackFlight.electricDrain(30000, false) == 7;
        assert com.oldustry.item.equipment.JetpackFlight.electricDrain(30000, true) == 4;
        assert com.oldustry.item.equipment.JetpackFlight.electricDrain(3, false) == 3;
        assert new ElectricCharge.Spec(30000, 60, 1, false).max() == 30000;
        assert pin(com.oldustry.item.equipment.Nightvision.DARK) == 7;
        assert pin(com.oldustry.item.equipment.Nightvision.DURATION) == 250;
        assert com.oldustry.item.equipment.Nightvision.darkEnough(7);
        assert !com.oldustry.item.equipment.Nightvision.darkEnough(8);
        assert com.oldustry.item.equipment.Nightvision.fromGoggles(250, 0);
        assert com.oldustry.item.equipment.Nightvision.fromGoggles(1, 0);
        assert !com.oldustry.item.equipment.Nightvision.fromGoggles(251, 0);
        assert !com.oldustry.item.equipment.Nightvision.fromGoggles(250, 1);
        assert com.oldustry.item.equipment.Nightvision.SPEC.max() == 20000;
        assert com.oldustry.item.equipment.Nightvision.SPEC.transfer() == 200;
        assert com.oldustry.item.equipment.Nightvision.SPEC.tier() == 1;
        var nanoSet = com.oldustry.item.equipment.NanoArmor.absorb(10f, 100000, 100000, 100000, 100000, false);
        assert Math.abs(nanoSet.remaining() - 1f) < 0.001f : nanoSet.remaining();
        assert nanoSet.boots() + nanoSet.legs() + nanoSet.chest() + nanoSet.helmet() == 7200;
        var chestOnly = com.oldustry.item.equipment.NanoArmor.absorb(10f, 0, 0, 100000, 0, false);
        assert Math.abs(chestOnly.remaining() - 6.4f) < 0.001f;
        assert chestOnly.chest() == 2880;
        assert com.oldustry.item.equipment.NanoArmor.absorb(10f, 0, 0, 0, 0, false).remaining() == 10f;
        assert com.oldustry.item.equipment.NanoArmor.fallCancelEu(10) == 5600;
        assert com.oldustry.item.equipment.NanoArmor.fallCancelEu(11) == -1;
        assert com.oldustry.item.equipment.NanoArmor.fallCancelEu(6) == 2400;
        assert com.oldustry.item.equipment.NanoArmor.display(3, 800) == 3;
        assert com.oldustry.item.equipment.NanoArmor.display(2, 800) == 7;
        assert com.oldustry.item.equipment.NanoArmor.display(1, 800) == 5;
        assert com.oldustry.item.equipment.NanoArmor.display(0, 800) == 3;
        assert com.oldustry.item.equipment.NanoArmor.display(2, 799) == 0;
        assert ElectricCharge.carriedCharge(10_000, 10_000) == 10_000;
        assert ElectricCharge.carriedCharge(10_000, 2_500) == 2_500;
        assert ElectricCharge.carriedCharge(100_000, 100_000) == 100_000;
        assert ElectricCharge.carriedCharge(1_000_000, 100_000) == 100_000;
        assert ElectricCharge.carriedCharge(10_000, 50_000) == 10_000;
        assert ElectricCharge.carriedCharge(100_000, 60_000L + 60_000L) == 100_000;
        assert ElectricCharge.carriedCharge(10_000, 0) == 0;
        assert com.oldustry.item.tool.NanoSaber.MAX == 100000;
        assert com.oldustry.item.tool.NanoSaber.HIT_EU == 160;
        assert com.oldustry.item.tool.NanoSaber.ACTIVE_BONUS == 16;
        assert com.oldustry.item.tool.NanoSaber.upkeep(true, 16) == 512;
        assert com.oldustry.item.tool.NanoSaber.upkeep(false, 16) == 0;
        assert com.oldustry.item.tool.NanoSaber.upkeep(false, 64) == 128;
        assert com.oldustry.item.tool.NanoSaber.upkeep(true, 64) == 512;
        assert pin(DrillStats.MINING_ENCHANT) == 14;
        assert pin(DrillStats.DIAMOND_ENCHANT) == 10;
        // Chainsaw: JAR efficiency 12 / EU 100; desvio speed 15
        assert pin(ChainsawStats.SPEED) == 15f && ChainsawStats.SPEED > 12f;
        assert pin(ChainsawStats.EU) == 100;
        System.out.println("OldustryEquipmentCheck ok");
    }

    private static int pin(int value) { return value; }
    private static float pin(float value) { return value; }
    private OldustryEquipmentCheck() {}
}
