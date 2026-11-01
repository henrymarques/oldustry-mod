package com.oldustry.item;

/**
 * JAR {@code ItemBattery.getIcon} fed by {@code ElectricItemManager}'s damage.
 * {@code BaseElectricItem} sets max damage to 27. Charge stays on the {@code ENERGY}
 * component; frames are {@code .0} empty through {@code .4} full.
 * The empty RE-battery item draws this same frame 0.
 */
public final class OldustryBatteryIcon {
    /** JAR {@code BaseElectricItem.setMaxDamage(27)}. */
    public static final int MAX_DAMAGE = 27;

    private OldustryBatteryIcon() {}

    public static int frame(int charge, int maxCharge) {
        if (charge <= 0 || maxCharge <= 0) {
            return 0;
        }
        if (charge > maxCharge) {
            charge = maxCharge;
        }
        int damage = 1 + (maxCharge - charge) * (MAX_DAMAGE - 2) / maxCharge;
        return icon(damage);
    }

    /** JAR {@code ItemBattery.func_77617_a}. */
    public static int icon(int meta) {
        if (meta <= 1) {
            return 4;
        }
        if (meta >= MAX_DAMAGE - 1) {
            return 0;
        }
        return 3 - 3 * (meta - 2) / (MAX_DAMAGE - 4 + 1);
    }
}
