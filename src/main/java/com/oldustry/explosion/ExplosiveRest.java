package com.oldustry.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * JAR dynamite backs the hit point off the face by 0.05. ITNT rests with its feet on the collision top.
 * A point left inside the block floors one block too low, so the blast drops a layer.
 */
final class ExplosiveRest {
    static final double BACKOFF = 0.05;

    private ExplosiveRest() {}

    static Vec3 backOff(Vec3 hit, Vec3 faceNormal) {
        return hit.add(faceNormal.scale(BACKOFF));
    }

    /** {@code y} inside a collision shape moves to that shape's top. Already above it stays. */
    static double stepOut(double y, int blockY, double shapeMaxY) {
        double top = blockY + shapeMaxY;
        return y < top ? top : y;
    }

    static Vec3 restOn(BlockGetter level, Vec3 pos) {
        double y = pos.y;
        for (int i = 0; i < 4; i++) {
            BlockPos block = BlockPos.containing(pos.x, y, pos.z);
            VoxelShape shape = level.getBlockState(block).getCollisionShape(level, block);
            if (shape.isEmpty()) {
                break;
            }
            double next = stepOut(y, block.getY(), shape.max(Direction.Axis.Y));
            if (next == y) {
                break;
            }
            y = next;
        }
        return y == pos.y ? pos : new Vec3(pos.x, y, pos.z);
    }
}
