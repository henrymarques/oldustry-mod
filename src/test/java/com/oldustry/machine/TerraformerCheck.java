package com.oldustry.machine;

import net.minecraft.util.RandomSource;

public final class TerraformerCheck {
    public static void main(String[] args) {
        assert OldustryMachineKind.TERRAFORMER.capacity == OldustryTerraformer.CAPACITY;
        assert OldustryMachineKind.TERRAFORMER.packet == OldustryTerraformer.MAX_INPUT;
        assert OldustryMachineKind.TERRAFORMER.tier == 3;
        assert OldustryMachineKind.TERRAFORMER.slots == 1;
        assert !OldustryMachineLayout.hasGui(OldustryMachineKind.TERRAFORMER);
        assert OldustryTfbp.CULTIVATION.consume == 4000 && OldustryTfbp.CULTIVATION.range == 40;
        assert OldustryTfbp.IRRIGATION.consume == 3000 && OldustryTfbp.IRRIGATION.range == 60;
        assert OldustryTfbp.CHILLING.consume == 2000 && OldustryTfbp.CHILLING.range == 50;
        assert OldustryTfbp.DESERTIFICATION.consume == 2500 && OldustryTfbp.DESERTIFICATION.range == 40;
        assert OldustryTfbp.FLATIFICATION.consume == 4000 && OldustryTfbp.FLATIFICATION.range == 40;
        assert OldustryTfbp.MUSHROOM.consume == 8000 && OldustryTfbp.MUSHROOM.range == 25;

        var fresh = OldustryTerraformer.Cursor.fresh();
        assert OldustryTerraformer.scanRange(40, fresh) == 8;
        assert OldustryTerraformer.scanRange(40, new OldustryTerraformer.Cursor(4, 0, -1, 0)) == 40;
        assert OldustryTerraformer.scanRange(40, new OldustryTerraformer.Cursor(9, 0, -1, 0)) == 40;
        assert OldustryTerraformer.clampFailed(new OldustryTerraformer.Cursor(9, 3, -1, 4)).failed() == 4;
        assert OldustryTerraformer.scanRange(40, new OldustryTerraformer.Cursor(0, 12, 64, 8)) == 4;
        assert OldustryTerraformer.roll(10, 4, 3, 1) == 8;

        assert OldustryTerraformer.spent(10000, 4000, true) == 6000;
        assert OldustryTerraformer.spent(10000, 4000, false) == 9600;
        var hit = OldustryTerraformer.after(fresh, true, 4, 5, 70);
        assert hit.failed() == 0 && hit.lastX() == 4 && hit.lastY() == 70 && hit.lastZ() == 5;
        var miss = OldustryTerraformer.after(new OldustryTerraformer.Cursor(2, 4, 70, 5), false, 9, 9, 70);
        assert miss.failed() == 3 && miss.lastY() == -1 && miss.lastX() == 4 && miss.lastZ() == 5;

        boolean active = true;
        int inactive = 0;
        int lit = 0;
        for (int i = 0; i < 40; i++) {
            int[] grace = OldustryTerraformer.grace(false, active, inactive);
            active = grace[0] == 1;
            inactive = grace[1];
            if (active) {
                lit++;
            }
        }
        assert lit == 31;
        int[] working = OldustryTerraformer.grace(true, false, 12);
        assert working[0] == 1 && working[1] == 0;

        int[] column = {0, 1, 1, 2};
        assert OldustryTerraformer.switchColumn(column, 2, 1, 2, true);
        assert column[1] == 2;
        int[] down = {1, 2, 2};
        assert OldustryTerraformer.switchColumn(down, 2, 1, 2, false);
        assert down[0] == 2;

        RandomSource random = RandomSource.create(1);
        int pick = OldustryTfbp.pickPlant(random, 9);
        assert pick >= 0 && pick < 9;

        System.out.println("TerraformerCheck ok");
    }

    private TerraformerCheck() {}
}
