package com.oldustry.reactor;

public final class ReactorCheck {
    public static void main(String[] args) {
        assert OldustryReactor.soloOutput(1) == 1;
        assert OldustryReactor.soloHeat(1) == 4;
        assert OldustryReactor.sent(1, 5) == 5;
        assert OldustryReactor.soloOutput(2) == 4;
        assert OldustryReactor.soloHeat(2) == 24;
        assert OldustryReactor.sent(4, 5) == 20;
        assert OldustryReactor.soloOutput(4) == 12;
        assert OldustryReactor.soloHeat(4) == 96;
        assert OldustryReactor.sent(12, 5) == 60;

        assert OldustryReactor.columns(0) == 3;
        assert OldustryReactor.columns(3) == 6;
        assert OldustryReactor.columns(6) == 9;
        assert OldustryReactor.columns(9) == 9;
        assert OldustryReactor.slotLeft(3) == 62;
        assert OldustryReactor.slotLeft(9) == 8;

        assert !OldustryReactor.fuelSpent(9998);
        assert OldustryReactor.fuelSpent(9999);
        assert OldustryReactor.keepNearDepleted(0);
        assert !OldustryReactor.keepNearDepleted(1);
        assert OldustryReactor.isotopeDamage(9999, 0) == 9998;
        assert OldustryReactor.isotopeDamage(1, 0) == 0;
        assert OldustryReactor.isotopeDamage(2, 3000) == 0;

        assert OldustryReactor.blast(1f, new float[] { 2f }, 45f) == 12f;
        assert Math.abs(OldustryReactor.blast(1f, new float[] { 2f, 0.95f }, 45f) - 11.4f) < 0.001f;
        assert OldustryReactor.blast(1f, new float[0], 45f) == 10f;
        assert OldustryReactor.blast(10f, new float[] { 100f }, 45f) == 45f;

        assert OldustryReactor.geiger(0f) == 0;
        assert OldustryReactor.geiger(39f) == 1;
        assert OldustryReactor.geiger(40f) == 2;
        assert OldustryReactor.geiger(79f) == 2;
        assert OldustryReactor.geiger(80f) == 3;

        assert !OldustryReactor.heatEffects(3999, 45f);
        assert OldustryReactor.heatEffects(4000, 45f);
        assert !OldustryReactor.heatEffects(10000, 0f);
        assert OldustryReactor.melts(1f);
        assert !OldustryReactor.melts(0.99f);
        assert OldustryReactor.lavaRoll(0.85f, 1f, 0.2f);
        assert !OldustryReactor.lavaRoll(0.85f, 1f, 0.21f);
        assert OldustryReactor.hurts(0.7f);
        assert OldustryReactor.hurt(3, 1f) == 3;
        assert OldustryReactor.evaporates(0.5f, 1f, 1f);
        assert !OldustryReactor.evaporates(0.49f, 1f, 0f);
        assert OldustryReactor.ignites(0.4f, 1f, 0.4f);

        assert OldustryReactor.triangular(1) == 1;
        assert OldustryReactor.triangular(3) == 6;
        assert OldustryReactor.producing(true, 5);
        assert !OldustryReactor.producing(true, 0);
        assert !OldustryReactor.producing(false, 5);
        assert OldustryReactor.lit(1000, 0f);
        assert OldustryReactor.lit(0, 1f);
        assert !OldustryReactor.lit(999, 0f);

        assert OldustryReactor.storedHeat(0, 4, 10_000) == 4;
        assert OldustryReactor.heatLeftover(0, 4, 10_000) == 0;
        assert !OldustryReactor.heatMelts(9_998, 2, 10_000);
        assert OldustryReactor.heatMelts(10_000, 4, 10_000);
        assert OldustryReactor.heatLeftover(10_000, 4, 10_000) == -3;
        assert OldustryReactor.storedHeat(2, -6, 10_000) == 0;
        assert OldustryReactor.heatLeftover(2, -6, 10_000) == -4;

        assert OldustryReactor.condensatorOpen(0, 20_001);
        assert !OldustryReactor.condensatorOpen(20_000, 20_001);
        assert OldustryReactor.condensatorWear(0, 20_001, 5) == 5;
        assert OldustryReactor.condensatorLeftover(0, 20_001, 5) == 0;
        assert OldustryReactor.condensatorWear(0, 20_001, -4) == -4;
        assert OldustryReactor.condensatorLeftover(19_999, 20_001, 5) == 2;
        assert OldustryReactor.repairWear(15_000, 10_000, 20_001) == 5_000;
        assert OldustryReactor.repairWear(1_000, 10_000, 20_001) == 0;
        assert OldustryReactor.repairWear(50_000, 1_000, 20_001) == 20_001;

        assert OldustryReactor.reflectorBreaks(9_999, 10_000);
        assert !OldustryReactor.reflectorBreaks(9_998, 10_000);
        assert OldustryReactor.ventPull(40, 36) == 36;
        assert OldustryReactor.ventPull(4, 36) == 4;
        assert OldustryReactor.clampTransfer(80, 12) == 12;
        assert OldustryReactor.clampTransfer(-80, 12) == -12;
        assert OldustryReactor.heatMedian(0.5, true, 0.5, 0, 0) == 0.5;
        assert OldustryReactor.heatpackAdd(1, 1, 1000, 0, 0) == 1;
        assert OldustryReactor.heatpackAdd(1, 1, 1000, 1000, 0) == 0;
        assert OldustryReactor.heatpackAdd(2, 1, 1000, 0, 1999) == 1;
        assert OldustryReactor.heatpackBlast(9) == 0f;
        assert OldustryReactor.heatpackBlast(10) == 1f;

        System.out.println("reactor check ok");
    }

    private ReactorCheck() {}
}
