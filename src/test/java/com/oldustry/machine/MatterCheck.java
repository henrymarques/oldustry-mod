package com.oldustry.machine;

public final class MatterCheck {
    public static void main(String[] args) {
        assert OldustryMachineKind.MASS_FABRICATOR.capacity == OldustryMatter.UU;
        assert OldustryMachineKind.MASS_FABRICATOR.tier == 3;
        assert OldustryMachineKind.MASS_FABRICATOR.packet == OldustryMatter.MAX_INPUT;
        assert OldustryMachineKind.MASS_FABRICATOR.wrenchRate == 0.7f;
        assert OldustryMachineLayout.hasGui(OldustryMachineKind.MASS_FABRICATOR);
        assert OldustryMatter.SCRAP == 5000 && OldustryMatter.SCRAP_BOX == 45000;

        int[] bare = OldustryMatter.inject(0, 0, 512, false);
        assert bare[0] == 3072 && bare[1] == -512 && bare[2] == 0;
        int[] box = OldustryMatter.inject(0, 45000, 512, false);
        assert box[0] == 512 + 5 * 45000 && box[1] == 0 && box[2] == 0;
        int[] full = OldustryMatter.inject(OldustryMatter.UU, 5000, 512, false);
        assert full[0] == OldustryMatter.UU && full[1] == 5000 && full[2] == 512;
        int[] redstone = OldustryMatter.inject(100, 5000, 512, true);
        assert redstone[0] == 100 && redstone[1] == 5000 && redstone[2] == 512;

        assert OldustryMatter.progress(0) == 0;
        assert OldustryMatter.progress(50_000) == 5;
        assert OldustryMatter.progress(OldustryMatter.UU) == 100;
        assert OldustryMatter.progress(2_000_000) == 100;
        assert OldustryMatter.produce(OldustryMatter.UU, true) == 0;
        assert OldustryMatter.produce(OldustryMatter.UU + 200, false) == OldustryMatter.UU + 200;
        assert OldustryMatter.running(1, false);
        assert !OldustryMatter.running(0, false);
        assert !OldustryMatter.running(1, true);

        System.out.println("MatterCheck ok");
    }

    private MatterCheck() {}
}
