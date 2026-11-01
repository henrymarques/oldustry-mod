package com.oldustry.item.equipment;

public final class QuantumCheck {
    public static void main(String[] args) {
        // Lava is 4. ApplyArmor truncates each share to 1 point, so every piece pays 900.
        QuantumArmor.Hit lava = QuantumArmor.absorb(4f, 1_000_000, 1_000_000, 1_000_000, 1_000_000, false);
        assert lava.remaining() == 0f : lava.remaining();
        assert lava.boots() == 900 && lava.legs() == 900 && lava.chest() == 900 && lava.helmet() == 900;

        QuantumArmor.Hit full = QuantumArmor.absorb(10f, 1_000_000, 1_000_000, 1_000_000, 1_000_000, false);
        assert full.remaining() == 0f : full.remaining();
        assert full.boots() == 900 && full.legs() == 1800 && full.chest() == 3600 && full.helmet() == 900;

        QuantumArmor.Hit chest = QuantumArmor.absorb(10f, 0, 0, 1_000_000, 0, false);
        assert Math.abs(chest.remaining() - 5.6f) < 0.02f : chest.remaining();
        assert chest.chest() == 3600 : chest.chest();

        QuantumArmor.Hit fall = QuantumArmor.absorb(10f, 1_000_000, 1_000_000, 1_000_000, 1_000_000, true);
        assert fall.remaining() == 0f;
        assert fall.boots() == 9000 && fall.chest() == 0;

        assert QuantumArmor.fallCancelEu(9) == 0;
        assert QuantumArmor.fallCancelEu(11) == 900;
        assert QuantumArmor.fallCancelEu(20) == 9000;
        assert QuantumArmor.display(2, 900) == 9;
        assert QuantumArmor.display(0, 899) == 0;
        assert QuantumArmor.cleanseBase(false, false) == 0;
        assert QuantumArmor.cleanseBase(true, false) == QuantumArmor.POISON_EU;
        assert QuantumArmor.cleanseBase(true, true) == QuantumArmor.WITHER_EU;
        assert QuantumArmor.cleanseCost(QuantumArmor.POISON_EU, 0) == 10_000;
        assert QuantumArmor.cleanseCost(QuantumArmor.POISON_EU, 1) == 20_000;
        assert QuantumArmor.cleanseCost(QuantumArmor.WITHER_EU, 0) == 25_000;
        assert QuantumArmor.refilledAir(50) == 250;
        assert !QuantumArmor.legPays(8) && QuantumArmor.legPays(9);
        assert QuantumArmor.afterLegTick(9) == 0 && QuantumArmor.afterLegTick(0) == 1;
        assert QuantumArmor.legSpeed(false) == 0.22f && QuantumArmor.legSpeed(true) == 0.1f;
        assert QuantumArmor.firstHop(1f) && !QuantumArmor.firstHop(QuantumArmor.nextJump(1f));
        assert Math.abs(QuantumArmor.lift(1f) - 0.3) < 1.0e-6;
        System.out.println("quantum check ok");
    }
}
