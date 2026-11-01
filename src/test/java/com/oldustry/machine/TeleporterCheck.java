package com.oldustry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public final class TeleporterCheck {
    public static void main(String[] args) {
        assert OldustryMachineKind.TELEPORTER.capacity == 0;
        assert OldustryMachineKind.TELEPORTER.packet == 0;
        assert OldustryMachineKind.TELEPORTER.wrenchRate == 1.0f;
        assert OldustryMachineKind.TELEPORTER.slots == 0;
        assert !OldustryMachineLayout.hasGui(OldustryMachineKind.TELEPORTER);
        assert OldustryMachineSlots.slotsForFace(OldustryMachineKind.TELEPORTER).length == 0;

        assert OldustryTeleporter.cost(1000, 0) == 25059;
        assert OldustryTeleporter.cost(100, 1000) == 63386;
        assert !OldustryTeleporter.far(999.9);
        assert OldustryTeleporter.far(1000);
        assert OldustryTeleporter.arriveY(64) == 65.5;
        assert OldustryTeleporter.stackWeight(16, 64) == 25;
        assert OldustryTeleporter.bodyWeight(true, true, true, true, true) == 100;
        assert OldustryTeleporter.bodyWeight(false, true, true, true, true) == 1000;
        assert OldustryTeleporter.bodyWeight(false, false, true, true, true) == 2500;
        assert OldustryTeleporter.bodyWeight(false, false, false, true, true) == 10000;
        assert OldustryTeleporter.bodyWeight(false, false, false, false, true) == 500;
        assert OldustryTeleporter.bodyWeight(false, false, false, false, false) == 0;

        AABB box = OldustryTeleporter.pickupBox(new BlockPos(10, 64, -3));
        assert box.minX == 10 && box.minY == 65 && box.minZ == -3;
        assert box.maxX == 11 && box.maxY == 67 && box.maxZ == -2;

        int[] one = {1000};
        assert OldustryTeleporter.draw(100, one) && one[0] == 900;
        int[] split = {100, 100};
        assert OldustryTeleporter.draw(100, split) && split[0] == 50 && split[1] == 50;
        int[] uneven = {10, 100};
        assert OldustryTeleporter.draw(50, uneven) && uneven[0] == 0 && uneven[1] == 60;
        int[] shortfall = {10, 20};
        assert !OldustryTeleporter.draw(50, shortfall) && shortfall[0] == 10 && shortfall[1] == 20;

        OldustryFrequency.Link empty = OldustryFrequency.empty();
        OldustryFrequency.ClickResult stored = OldustryFrequency.click(empty, 3, 4, 5, false, false, 0, 0, 0, false);
        assert stored.click() == OldustryFrequency.Click.STORED;
        assert stored.transmitter().set() && stored.transmitter().latched();
        assert stored.transmitter().x() == 3 && stored.transmitter().y() == 4 && stored.transmitter().z() == 5;
        assert !stored.pointHere();

        OldustryFrequency.Link held = OldustryFrequency.air(stored.transmitter());
        assert held.set() && !held.latched();
        OldustryFrequency.Link cleared = OldustryFrequency.air(held);
        assert !cleared.set() && !cleared.latched();
        assert OldustryFrequency.air(cleared).equals(cleared);

        OldustryFrequency.Link link = stored.transmitter();
        OldustryFrequency.ClickResult self = OldustryFrequency.click(link, 3, 4, 5, true, false, 0, 0, 0, false);
        assert self.click() == OldustryFrequency.Click.SELF && !self.pointHere();
        OldustryFrequency.ClickResult same = OldustryFrequency.click(link, 8, 4, 5, true, true, 3, 4, 5, false);
        assert same.click() == OldustryFrequency.Click.UNCHANGED && !same.pointHere();
        OldustryFrequency.ClickResult linked = OldustryFrequency.click(link, 8, 4, 5, true, false, 0, 0, 0, true);
        assert linked.click() == OldustryFrequency.Click.LINKED && linked.pointHere() && linked.pointBack();
        OldustryFrequency.ClickResult oneWay = OldustryFrequency.click(link, 8, 4, 5, true, true, 1, 2, 3, false);
        assert oneWay.click() == OldustryFrequency.Click.LINKED && oneWay.pointHere() && !oneWay.pointBack();
        OldustryFrequency.ClickResult gone = OldustryFrequency.click(link, 9, 9, 9, false, false, 0, 0, 0, false);
        assert gone.click() == OldustryFrequency.Click.STORED && gone.transmitter().x() == 9;

        System.out.println("TeleporterCheck ok");
    }

    private TeleporterCheck() {}
}
