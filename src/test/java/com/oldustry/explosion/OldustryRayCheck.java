package com.oldustry.explosion;

public final class OldustryRayCheck {
    public static void main(String[] args) {
        assert OldustryRay.hit(false, 0, false, 5.5).absorption() == 0.5;
        var stone = OldustryRay.hit(true, 6.0, false, 5.5);
        assert stone.absorption() == 3.5 : stone.absorption();
        assert !stone.stop();
        assert !stone.passThrough();
        assert Boolean.TRUE.equals(OldustryRay.drop(5.5, false));

        var stopped = OldustryRay.hit(true, 6.0, false, 3.0);
        assert stopped.stop();

        var bedrock = OldustryRay.hit(true, 3_600_000.0, false, 35.0);
        assert bedrock.passThrough() && bedrock.absorption() == 0.5;
        var whitelisted = OldustryRay.hit(true, 3_600_000.0, true, 35.0);
        assert whitelisted.stop() && !whitelisted.passThrough();

        assert Boolean.FALSE.equals(OldustryRay.drop(8.1, false));
        assert Boolean.TRUE.equals(OldustryRay.drop(8.0, false));
        assert OldustryRay.drop(5.0, true) == null;
        assert Boolean.FALSE.equals(OldustryRay.drop(9.0, true));

        assert OldustryRay.entityDamage(5.5) == 22;
        assert OldustryRay.entityDamage(0.25) == 1;
        assert OldustryRay.raySteps(5.5) == 44;
        assert OldustryRay.floor(-1.2) == -2;
        assert OldustryRay.floor(1.2) == 1;
        assert OldustryRay.floor(-2.0) == -2;

        assert OldustryRay.anchorIndex(4, new int[] {9, 1, 4}) == 0;
        int anchor = OldustryRay.anchorIndex(6, new int[] {1, 12, 40, 80});
        assert anchor == 1 : anchor;
        assert OldustryRay.anchorRepeats(4, 1, 12, 6) == 3;

        double[] east = OldustryRay.direction(0.0, Math.PI / 2.0);
        assert Math.abs(east[0] - 1.0) < 1.0e-9;
        assert Math.abs(east[1]) < 1.0e-9;
        assert Math.abs(east[2]) < 1.0e-9;

        assert NukeFallout.hungerTicks(0) == 12000;
        assert NukeFallout.hungerTicks(100) == 0;
        assert NukeFallout.hungerTicks(101) < 0;
        assert NukeFallout.radiationTicks(0) == 2400;
        assert NukeFallout.radiationTicks(30) == 0;
        assert NukeFallout.radiationTick(25, 0);
        assert !NukeFallout.radiationTick(24, 0);

        System.out.println("OldustryRayCheck ok");
    }
}
