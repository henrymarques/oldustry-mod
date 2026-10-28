package com.oldustry.item.equipment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * JAR {@code ItemArmorHazmat} plus Forge {@code ISpecialArmor.ApplyArmor}.
 * Slot order is boots, legs, chest, helmet. {@code armorType} is the 1.5.2 field:
 * 0 helmet, 1 chest, 2 legs, 3 boots.
 */
public final class OldustryHazmatArmor {
    public static final int MAX_DAMAGE = 64;
    public static final int DISPLAY = 1;
    public static final int AIR_AT = 100;
    public static final int AIR_GAIN = 150;
    public static final int FALL_CANCEL_BELOW = 8;
    /** {@code Potion.fireResistance} for 60 ticks, amplifier 1, on inFire or lava. */
    public static final int RESIST_TICKS = 60;
    /** While burning in lava or fire: 20 ticks, amplifier 0. */
    public static final int LAVA_RESIST_TICKS = 20;

    public enum Source {
        IN_FIRE, ON_FIRE, LAVA, IN_WALL, FALL, ELECTRICITY, RADIATION, OTHER;

        /** {@code field_76372_a} inFire, {@code field_76370_b} onFire, {@code field_76371_c} lava, {@code field_76368_d} inWall, plus electricity and radiation. */
        public boolean absorbed() {
            return this == IN_FIRE || this == ON_FIRE || this == LAVA || this == IN_WALL
                || this == ELECTRICITY || this == RADIATION;
        }
    }

    public enum Kind { HAZMAT, NANO, VANILLA }

    /** {@code value} is hazmat damage, nano charge, or vanilla damage. */
    public record Piece(Kind kind, int armorType, int value, int points, int maxDamage) {}

    public record Attack(Source source, boolean bypassesArmor) {}

    public record Hit(float remaining, int[] hazmatWear, int[] nanoEu, int[] vanillaWear, boolean fireResist) {
        public int hazmat(int slot) {
            return hazmatWear[slot];
        }

        public int nano(int slot) {
            return nanoEu[slot];
        }
    }

    public record AirTick(boolean consume, int air, boolean dropEmpty) {}

    private OldustryHazmatArmor() {}

    public static boolean complete(Piece[] slots) {
        if (slots.length != 4) {
            return false;
        }
        for (Piece piece : slots) {
            if (piece == null || piece.kind != Kind.HAZMAT) {
                return false;
            }
        }
        return true;
    }

    /** A cell in the inventory or a curios slot does not fill a missing armor slot. */
    public static boolean complete(boolean boots, boolean legs, boolean chest, boolean helmet) {
        return boots && legs && chest && helmet;
    }

    public static Hit absorb(float damage, Piece[] slots, Attack attack) {
        int[] hazmatWear = new int[4];
        int[] nanoEu = new int[4];
        int[] vanillaWear = new int[4];
        boolean full = complete(slots);
        boolean immune = full && attack.source.absorbed();
        double scaled = damage * 25.0;
        ArrayList<Prop> props = new ArrayList<>();
        boolean fireResist = false;
        for (int slot = 0; slot < 4; slot++) {
            Piece piece = slots[slot];
            if (piece == null) {
                continue;
            }
            Prop prop = properties(piece, slot, damage, attack, immune);
            if (prop == null) {
                continue;
            }
            if (piece.kind == Kind.HAZMAT && piece.armorType == 0 && immune
                && (attack.source == Source.IN_FIRE || attack.source == Source.LAVA)) {
                fireResist = true;
            }
            props.add(prop);
        }
        if (props.isEmpty()) {
            return new Hit(damage, hazmatWear, nanoEu, vanillaWear, false);
        }
        Prop[] list = props.toArray(Prop[]::new);
        standardize(list, scaled);
        int level = list[0].priority;
        double ratio = 0;
        for (Prop prop : list) {
            if (level != prop.priority) {
                scaled -= scaled * ratio;
                ratio = 0;
                level = prop.priority;
            }
            ratio += prop.absorbRatio;
            double absorbed = scaled * prop.absorbRatio;
            if (absorbed <= 0) {
                continue;
            }
            int itemDamage = (int) (absorbed / 25.0 < 1 ? 1 : absorbed / 25.0);
            wear(slots[prop.slot], prop.slot, itemDamage, attack, immune, hazmatWear, nanoEu, vanillaWear);
        }
        scaled -= scaled * ratio;
        return new Hit((float) (scaled / 25.0), hazmatWear, nanoEu, vanillaWear, fireResist);
    }

    /** {@code (int) distance - 3}. */
    public static int fallDamage(double distance) {
        return (int) distance - 3;
    }

    /** Durability the boots pay to cancel. Negative when the fall is too short to bill. */
    public static int fallWear(int fallDamage) {
        return (fallDamage + 1) / 2;
    }

    /** Cancel only below 8 and only when the remaining durability covers {@link #fallWear}. */
    public static boolean cancelsFall(int fallDamage, int damageTaken) {
        if (fallDamage >= FALL_CANCEL_BELOW) {
            return false;
        }
        int cost = fallWear(fallDamage);
        int left = MAX_DAMAGE - damageTaken;
        return cost >= 0 && cost <= left;
    }

    public static AirTick air(int air, boolean hasCell, boolean roomForEmpty) {
        if (air > AIR_AT || !hasCell) {
            return new AirTick(false, air, false);
        }
        return new AirTick(true, air + AIR_GAIN, !roomForEmpty);
    }

    /**
     * JAR {@code isInLava}: sample {@code y + 0.02} against {@code BlockFluid.getLiquidHeightPercent}.
     * Metadata 8 and above is treated as a full source. Callers pass lava meta or fire age.
     */
    public static boolean insideMaterial(double y, int blockY, int meta) {
        double sample = y + 0.02;
        int fluid = meta >= 8 ? 0 : meta;
        float percent = (fluid + 1) / 9.0f - 0.11111111f;
        float surface = (blockY + 1) - percent;
        return sample < surface;
    }

    /** Source lava is meta 0. Falling lava (amount 8) collapses to a full block inside {@link #insideMaterial}. */
    public static int lavaMeta(boolean source, int amount) {
        return source ? 0 : amount;
    }

    /** Normal cap {@code ((max - damage + 1) / 2) * 25}. Fall boots use {@code (max - damage + 1) * 2 * 25}. */
    public static int absorbMax(int damageTaken, boolean fallBoots) {
        int span = MAX_DAMAGE - damageTaken + 1;
        return fallBoots ? span * 2 * 25 : (span / 2) * 25;
    }

    private static Prop properties(Piece piece, int slot, float damage, Attack attack, boolean immune) {
        if (piece.kind == Kind.HAZMAT) {
            if (piece.armorType == 0 && immune) {
                return new Prop(10, Integer.MAX_VALUE, 1.0, slot);
            }
            if (piece.armorType == 3 && attack.source == Source.FALL) {
                double ratio = damage < FALL_CANCEL_BELOW ? 1.0 : 0.875;
                return new Prop(10, absorbMax(piece.value, true), ratio, slot);
            }
            return new Prop(0, absorbMax(piece.value, false), 1.0, slot);
        }
        if (piece.kind == Kind.NANO) {
            if (attack.source == Source.FALL && piece.armorType == 3) {
                int cap = 25 * piece.value / NanoArmor.EU_PER_DAMAGE;
                return new Prop(10, cap, NanoArmor.bootsFallRatio(damage), slot);
            }
            if (attack.bypassesArmor) {
                return null;
            }
            double ratio = NanoArmor.typedRatio(piece.armorType);
            int cap = 25 * piece.value / NanoArmor.EU_PER_DAMAGE;
            return new Prop(0, cap, ratio, slot);
        }
        if (attack.bypassesArmor || piece.points <= 0 || piece.maxDamage <= 0) {
            return null;
        }
        return new Prop(0, piece.maxDamage + 1 - piece.value, piece.points / 25.0, slot);
    }

    private static void wear(Piece piece, int slot, int itemDamage, Attack attack, boolean immune, int[] hazmatWear, int[] nanoEu, int[] vanillaWear) {
        if (piece.kind == Kind.HAZMAT) {
            if (immune) {
                return;
            }
            hazmatWear[slot] = piece.armorType == 3 && attack.source == Source.FALL
                ? (itemDamage + 1) / 2
                : itemDamage * 2;
            return;
        }
        if (piece.kind == Kind.NANO) {
            nanoEu[slot] = itemDamage * NanoArmor.EU_PER_DAMAGE;
            return;
        }
        vanillaWear[slot] = itemDamage;
    }

    /** Forge {@code ArmorProperties.StandardizeList}. {@code damage} is already scaled by 25 and stays local. */
    private static void standardize(Prop[] armor, double damage) {
        Arrays.sort(armor);
        int start = 0;
        double total = 0;
        int priority = armor[0].priority;
        int priorityStart = 0;
        boolean priorityChange = false;
        boolean finished = false;
        for (int x = 0; x < armor.length; x++) {
            total += armor[x].absorbRatio;
            if (x == armor.length - 1 || armor[x].priority != priority) {
                if (armor[x].priority != priority) {
                    total -= armor[x].absorbRatio;
                    x--;
                    priorityChange = true;
                }
                if (total > 1) {
                    for (int y = start; y <= x; y++) {
                        double newRatio = armor[y].absorbRatio / total;
                        if (newRatio * damage > armor[y].absorbMax) {
                            armor[y].absorbRatio = armor[y].absorbMax / damage;
                            total = 0;
                            for (int z = priorityStart; z <= y; z++) {
                                total += armor[z].absorbRatio;
                            }
                            start = y + 1;
                            x = y;
                            break;
                        }
                        armor[y].absorbRatio = newRatio;
                        finished = true;
                    }
                    if (priorityChange && finished) {
                        damage -= damage * total;
                        total = 0;
                        start = x + 1;
                        priority = armor[start].priority;
                        priorityStart = start;
                        priorityChange = false;
                        finished = false;
                        if (damage <= 0) {
                            for (int y = x + 1; y < armor.length; y++) {
                                armor[y].absorbRatio = 0;
                            }
                            break;
                        }
                    }
                } else {
                    for (int y = start; y <= x; y++) {
                        total -= armor[y].absorbRatio;
                        if (damage * armor[y].absorbRatio > armor[y].absorbMax) {
                            armor[y].absorbRatio = armor[y].absorbMax / damage;
                        }
                        total += armor[y].absorbRatio;
                    }
                    damage -= damage * total;
                    total = 0;
                    if (x != armor.length - 1) {
                        start = x + 1;
                        priority = armor[start].priority;
                        priorityStart = start;
                        priorityChange = false;
                        if (damage <= 0) {
                            for (int y = x + 1; y < armor.length; y++) {
                                armor[y].absorbRatio = 0;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private static final class Prop implements Comparable<Prop> {
        final int priority;
        final int absorbMax;
        double absorbRatio;
        final int slot;

        Prop(int priority, int absorbMax, double absorbRatio, int slot) {
            this.priority = priority;
            this.absorbMax = absorbMax;
            this.absorbRatio = absorbRatio;
            this.slot = slot;
        }

        @Override
        public int compareTo(Prop other) {
            if (other.priority != priority) {
                return other.priority - priority;
            }
            double left = absorbRatio == 0 ? 0 : absorbMax * 100.0 / absorbRatio;
            double right = other.absorbRatio == 0 ? 0 : other.absorbMax * 100.0 / other.absorbRatio;
            return (int) (left - right);
        }
    }
}
