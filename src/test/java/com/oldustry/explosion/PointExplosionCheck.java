package com.oldustry.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class PointExplosionCheck {
    public static void main(String[] args) {
        var positions = PointExplosion.candidatePositions(new Vec3(-0.1, 4.8, 7.0));
        assert positions.size() == 27;
        assert positions.contains(new BlockPos(-2, 3, 6));
        assert positions.contains(new BlockPos(0, 5, 8));
        assert !positions.contains(new BlockPos(1, 4, 7));

        // A point 0.001 inside the floor floors one block low (3x3x2). The face itself does not.
        var sunk = PointExplosion.candidatePositions(new Vec3(0.0, 63.999, 0.0));
        assert sunk.contains(new BlockPos(0, 62, 0));
        double stood = ExplosiveRest.stepOut(63.999, 63, 1.0);
        var cube = PointExplosion.candidatePositions(new Vec3(0.0, stood, 0.0));
        assert !cube.contains(new BlockPos(0, 62, 0));
        assert cube.contains(new BlockPos(0, 63, 0));
        double backed = ExplosiveRest.backOff(new Vec3(0.0, 64.0, 0.0), new Vec3(0.0, 1.0, 0.0)).y;
        assert backed > 64.0 && backed < 64.1;
        assert !PointExplosion.candidatePositions(new Vec3(0.0, backed, 0.0)).contains(new BlockPos(0, 62, 0));
        // ITNT center is feet + height/2. Feet sunk to 63.2 must stand at 64, center still in that cell.
        double center = ExplosiveRest.stepOut(63.2, 63, 1.0) + 0.98 / 2.0;
        assert center > 64.0 && center < 65.0;
        System.out.println("PointExplosionCheck ok");
    }
}
