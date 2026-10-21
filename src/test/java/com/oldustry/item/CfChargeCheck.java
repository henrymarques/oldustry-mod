package com.oldustry.item;

/** Remaining foam units for CF sprayer / CF pack. Run with assertions enabled. */
public final class CfChargeCheck {
    public static void main(String[] args) {
        assert !CfCharge.canSprayFromSelf(CfCharge.SPRAYER_CRAFTED) : "crafted sprayer is empty";
        assert CfCharge.addPellets(CfCharge.SPRAYER_CRAFTED, 1) == 100;
        assert CfCharge.canSprayFromSelf(100);
        assert !CfCharge.canSprayFromSelf(99);
        assert CfCharge.afterSpray(100) == 0;
        assert CfCharge.addPellets(CfCharge.SPRAYER_CRAFTED, 16) == CfCharge.SPRAYER_MAX : "16 pellets fill the tank";
        assert CfCharge.addPellets(1600, 1) == 1600;

        assert !CfCharge.canFeed(CfCharge.PACK_CRAFTED) : "crafted pack is empty";
        assert CfCharge.canFeed(1);
        assert CfCharge.afterFeed(1) == 0;
        assert !CfCharge.canFeed(0);
        assert CfCharge.afterPellet(0) == 2;
        assert CfCharge.afterPellet(258) == 260;
        assert CfCharge.acceptsPellet(258);
        assert !CfCharge.acceptsPellet(259);
        assert CfCharge.leaveCanner(260, true);
        assert !CfCharge.leaveCanner(258, true);
        assert CfCharge.leaveCanner(5, false);
        // 130 pellets take a crafted pack (0) up to 260: 130 * 2.
        int charge = CfCharge.PACK_CRAFTED;
        int pellets = 0;
        while (CfCharge.acceptsPellet(charge)) {
            charge = CfCharge.afterPellet(charge);
            pellets++;
        }
        assert pellets == 130 : pellets;
        assert charge == 260 : charge;
        assert CfCharge.leaveCanner(charge, true);
        System.out.println("CfChargeCheck ok");
    }

    private CfChargeCheck() {}
}
