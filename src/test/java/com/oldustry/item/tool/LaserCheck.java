package com.oldustry.item.tool;

public final class LaserCheck {
    public static void main(String[] args) {
        assert OldustryLaser.cost(0) == 1250;
        assert OldustryLaser.cost(1) == 100;
        assert OldustryLaser.cost(2) == 5000;
        assert OldustryLaser.cost(OldustryLaser.HORIZONTAL) == 0;
        assert OldustryLaser.cost(4) == 2500;
        assert OldustryLaser.cost(5) == 10000;
        assert OldustryLaser.cost(6) == 5000;
        assert OldustryLaser.next(6) == 0;
        assert OldustryLaser.HORIZONTAL_COST == 3000;

        double eye = 64 + 1.62;
        assert OldustryLaser.horizontalAim(eye, 64);
        assert OldustryLaser.horizontalAim(eye, 65);
        assert OldustryLaser.horizontalAim(eye, 66);
        assert !OldustryLaser.horizontalAim(eye, 63);
        assert !OldustryLaser.horizontalAim(eye, 67);
        assert !OldustryLaser.horizontalAim(70, 64);

        double[] flat = OldustryLaser.heading(0, 0);
        assert Math.abs(flat[0]) < 1.0e-6;
        assert Math.abs(flat[1]) < 1.0e-6;
        assert Math.abs(flat[2] - 1) < 1.0e-6;
        double[] down = OldustryLaser.heading(0, 90);
        assert Math.abs(down[1] + 1) < 1.0e-6;

        assert OldustryLaser.scatterYaw(10, -2) == -30f;
        assert OldustryLaser.scatterPitch(5, 2) == 45f;
        assert OldustryLaser.spent(0.5f, 5f, 3);
        assert OldustryLaser.spent(8f, 0f, 3);
        assert OldustryLaser.spent(8f, 5f, 0);
        assert !OldustryLaser.spent(Float.POSITIVE_INFINITY, 5f, Integer.MAX_VALUE);
        assert Math.abs(OldustryLaser.absorb(5f, 6f) - 4.37f) < 1.0e-4;
        assert OldustryLaser.airDrain(5f) == 4.5f;
        assert OldustryLaser.stepRange(4f) == 3f;
        assert OldustryLaser.burnSeconds(5, false) == 5f;
        assert OldustryLaser.burnSeconds(5, true) == 10f;
        assert Math.abs(OldustryLaser.muzzleX(0, 0) + 0.16) < 1.0e-9;
        assert Math.abs(OldustryLaser.eyeLaunch(10, 1.62) - 11.52) < 1.0e-9;

        System.out.println("laser check ok");
    }
}
