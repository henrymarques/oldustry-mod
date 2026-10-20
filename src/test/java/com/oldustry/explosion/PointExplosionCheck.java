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
        System.out.println("PointExplosionCheck ok");
    }
}
