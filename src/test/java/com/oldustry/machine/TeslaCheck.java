package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public final class TeslaCheck {
    public static void main(String[] args) {
        assert OldustryTesla.CAPACITY == 10000;
        assert OldustryTesla.MAX_INPUT == 128;
        assert OldustryTesla.COST == 400;
        assert OldustryTesla.SHOCK_PERIOD == 32;
        assert OldustryTesla.PARTICLE_RGB == 0x0000FF;
        assert OldustryMachineKind.TESLA.capacity == 10000;
        assert OldustryMachineKind.TESLA.tier == 2;
        assert OldustryMachineKind.TESLA.packet == 128;
        assert OldustryMachineKind.TESLA.wrenchRate == 1.0f;
        assert OldustryMachineKind.TESLA.slots == 0;
        assert !OldustryMachineLayout.hasGui(OldustryMachineKind.TESLA);

        OldustryTesla.Tick idle = OldustryTesla.step(10000, 7, false);
        assert idle.energy() == 10000 && idle.ticker() == 7 && idle.shockDamage() == 0;
        OldustryTesla.Tick low = OldustryTesla.step(399, 0, true);
        assert low.energy() == 399 && low.ticker() == 0 && low.shockDamage() == 0;

        OldustryTesla.Tick first = OldustryTesla.step(10000, 0, true);
        assert first.energy() == 9999 && first.ticker() == 1 && first.shockDamage() == 25;
        OldustryTesla.Tick between = OldustryTesla.step(800, 1, true);
        assert between.energy() == 799 && between.ticker() == 2 && between.shockDamage() == 0;
        OldustryTesla.Tick edge = OldustryTesla.step(400, 31, true);
        assert edge.energy() == 399 && edge.ticker() == 32 && edge.shockDamage() == 0;
        OldustryTesla.Tick again = OldustryTesla.step(400, 32, true);
        assert again.energy() == 399 && again.ticker() == 33 && again.shockDamage() == 1;

        AABB box = OldustryTesla.box(new BlockPos(10, 64, -3));
        assert box.minX == 6 && box.minY == 60 && box.minZ == -7;
        assert box.maxX == 15 && box.maxY == 69 && box.maxZ == 2;

        System.out.println("TeslaCheck ok");
    }

    private TeslaCheck() {}
}
