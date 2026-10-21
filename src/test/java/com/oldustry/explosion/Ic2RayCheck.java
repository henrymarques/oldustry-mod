package com.oldustry.explosion;

public final class Ic2RayCheck {
    public static void main(String[] args) {
        assert Ic2Ray.hit(false, 0, false, 5.5).absorption() == 0.5;
        var stone = Ic2Ray.hit(true, 6.0, false, 5.5);
        assert stone.absorption() == 3.5 : stone.absorption();
        assert !stone.stop();
        assert !stone.passThrough();
        assert Boolean.TRUE.equals(Ic2Ray.drop(5.5, false));

        var stopped = Ic2Ray.hit(true, 6.0, false, 3.0);
        assert stopped.stop();

        var bedrock = Ic2Ray.hit(true, 3_600_000.0, false, 35.0);
        assert bedrock.passThrough() && bedrock.absorption() == 0.5;
        var whitelisted = Ic2Ray.hit(true, 3_600_000.0, true, 35.0);
        assert whitelisted.stop() && !whitelisted.passThrough();

        assert Boolean.FALSE.equals(Ic2Ray.drop(8.1, false));
        assert Boolean.TRUE.equals(Ic2Ray.drop(8.0, false));
        assert Ic2Ray.drop(5.0, true) == null;
        assert Boolean.FALSE.equals(Ic2Ray.drop(9.0, true));

        assert Ic2Ray.entityDamage(5.5) == 22;
        assert Ic2Ray.entityDamage(0.25) == 1;
        assert Ic2Ray.raySteps(5.5) == 44;
        assert Ic2Ray.floor(-1.2) == -2;
        assert Ic2Ray.floor(1.2) == 1;
        assert Ic2Ray.floor(-2.0) == -2;

        assert Ic2Ray.anchorIndex(4, new int[] {9, 1, 4}) == 0;
        int anchor = Ic2Ray.anchorIndex(6, new int[] {1, 12, 40, 80});
        assert anchor == 1 : anchor;
        assert Ic2Ray.anchorRepeats(4, 1, 12, 6) == 3;

        double[] east = Ic2Ray.direction(0.0, Math.PI / 2.0);
        assert Math.abs(east[0] - 1.0) < 1.0e-9;
        assert Math.abs(east[1]) < 1.0e-9;
        assert Math.abs(east[2]) < 1.0e-9;

        System.out.println("Ic2RayCheck ok");
    }
}
