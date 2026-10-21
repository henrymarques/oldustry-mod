package com.oldustry.machine;

import com.oldustry.Oldustry;

import net.minecraft.resources.Identifier;

/** IC2 1.117.385-lf container slot coords and GUI overlay math. */
public final class MachineGui {
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final int STORAGE_HEIGHT = 196;
    /** 166 plus a band so the miner status sits above the inventory label. */
    public static final int MINER_HEIGHT = 186;
    public static final int MINER_STATUS_GAP = 12;

    public enum Role {
        HIDDEN,
        INPUT,
        OUTPUT,
        FUEL,
        CHARGE,
        DISCHARGE,
        UPGRADE
    }

    public enum Overlay {
        PROCESS,
        GENERATOR,
        GEO,
        SOLAR,
        WATER,
        WIND,
        STORAGE,
        MINER,
        PUMP,
        CANNER,
        ELECTROLYZER,
        INDUCTION
    }

    public static boolean hasGui(MachineKind kind) {
        return switch (kind.type) {
            case TRANSFORMER, LUMINATOR, MAGNETIZER -> false;
            default -> true;
        };
    }

    public static int height(MachineKind kind) {
        return switch (kind.type) {
            case STORAGE -> STORAGE_HEIGHT;
            case MINING -> MINER_HEIGHT;
            default -> HEIGHT;
        };
    }

    public static Identifier texture(MachineKind kind) {
        String name = switch (kind.type) {
            case STORAGE -> "electric_block";
            default -> kind.getSerializedName();
        };
        return Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/gui/container/" + name + ".png");
    }

    public static Overlay overlay(MachineKind kind) {
        return switch (kind.type) {
            case GENERATOR -> Overlay.GENERATOR;
            case GEO -> Overlay.GEO;
            case SOLAR -> Overlay.SOLAR;
            case WATER -> Overlay.WATER;
            case WIND -> Overlay.WIND;
            case STORAGE -> Overlay.STORAGE;
            case MINING -> Overlay.MINER;
            case PUMPING -> Overlay.PUMP;
            case CANNING -> Overlay.CANNER;
            case ELECTROLYZER -> Overlay.ELECTROLYZER;
            case INDUCTION -> Overlay.INDUCTION;
            default -> Overlay.PROCESS;
        };
    }

    public static Role role(MachineKind kind, int slot) {
        return switch (kind.type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE -> switch (slot) {
                case 0 -> Role.INPUT;
                case 1 -> Role.OUTPUT;
                case 2, 3, 4, 5 -> Role.UPGRADE;
                case 6 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            case GENERATOR, GEO -> slot == 0 ? Role.FUEL : slot == 1 ? Role.CHARGE : Role.HIDDEN;
            case SOLAR, WIND -> slot == 1 ? Role.CHARGE : Role.HIDDEN;
            case WATER -> slot == 0 ? Role.FUEL : slot == 1 ? Role.CHARGE : Role.HIDDEN;
            case STORAGE -> slot == 0 ? Role.CHARGE : slot == 1 ? Role.DISCHARGE : Role.HIDDEN;
            // Miner: drill=0, scanner=1, pipe=2, discharge=3, out=4-6
            case MINING -> switch (slot) {
                case 0 -> Role.INPUT;   // drill
                case 1 -> Role.INPUT;   // scanner
                case 2 -> Role.INPUT;   // pipe supply
                case 3 -> Role.DISCHARGE;
                case 4, 5, 6 -> Role.OUTPUT;
                default -> Role.HIDDEN;
            };
            // Pump: empty_cell=0, output=1, discharge=2
            case PUMPING -> switch (slot) {
                case 0 -> Role.INPUT;
                case 1 -> Role.OUTPUT;
                case 2 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            // Canner: resource=0, can=1, output=2, discharge=3
            case CANNING -> switch (slot) {
                case 0 -> Role.INPUT;
                case 1 -> Role.INPUT;
                case 2 -> Role.OUTPUT;
                case 3 -> Role.DISCHARGE;
                default -> Role.HIDDEN;
            };
            // Electrolyzer: water=0, electrolyzed=1
            case ELECTROLYZER -> switch (slot) {
                case 0, 1 -> Role.INPUT;
                default -> Role.HIDDEN;
            };
            // Induction: inputA=0, inputB=1, discharge=2, outA=3, outB=4
            case INDUCTION -> switch (slot) {
                case 0, 1 -> Role.INPUT;
                case 2 -> Role.DISCHARGE;
                case 3, 4 -> Role.OUTPUT;
                default -> Role.HIDDEN;
            };
            default -> Role.HIDDEN;
        };
    }

    /** Slot x,y; negative x means off-screen. */
    public static int[] slot(MachineKind kind, int index) {
        return switch (kind.type) {
            case MACERATOR, EXTRACTOR, COMPRESSOR, RECYCLER, FURNACE -> switch (role(kind, index)) {
                case INPUT -> xy(56, 17);
                case OUTPUT -> xy(116, 35);
                case DISCHARGE -> xy(56, 53);
                case UPGRADE -> xy(152, 8 + (index - 2) * 18);
                default -> hidden();
            };
            case GENERATOR, GEO -> switch (index) {
                case 0 -> xy(65, 53);
                case 1 -> xy(65, 17);
                default -> hidden();
            };
            case SOLAR, WIND -> index == 1 ? xy(80, 26) : hidden();
            case WATER -> switch (index) {
                case 0 -> xy(80, 53);
                case 1 -> xy(80, 17);
                default -> hidden();
            };
            case STORAGE -> switch (index) {
                case 0 -> xy(56, 17);
                case 1 -> xy(56, 53);
                default -> hidden();
            };
            // JAR ContainerMiner: drill 45,22 / pipe 81,22 / scanner 117,22; discharge 81,59.
            // Outputs 4–6 are the drop buffer (JAR ejected). Left of the battery, off the painted holes.
            case MINING -> switch (index) {
                case 0 -> xy(45, 22);
                case 1 -> xy(117, 22);
                case 2 -> xy(81, 22);
                case 3 -> xy(81, 59);
                case 4 -> xy(8, 53);
                case 5 -> xy(26, 53);
                case 6 -> xy(44, 53);
                default -> hidden();
            };
            // JAR ContainerPump: cell 62,17; discharge 62,53. Filled container is ejected, not a slot.
            case PUMPING -> switch (index) {
                case 0 -> xy(62, 17);
                case 2 -> xy(62, 53);
                default -> hidden();
            };
            // Canner JAR ContainerCanner: res(69,17) out(119,35) can(69,53) discharge(30,45)
            case CANNING -> switch (index) {
                case 0 -> xy(69, 17);   // resource (food/fuel/pellets)
                case 1 -> xy(69, 53);   // can/fuel_can/cf_pack
                case 2 -> xy(119, 35);  // output
                case 3 -> xy(30, 45);   // discharge
                default -> hidden();
            };
            case ELECTROLYZER -> switch (index) {
                case 0 -> xy(53, 35);   // water cell
                case 1 -> xy(112, 35);  // electrolyzed water cell
                default -> hidden();
            };
            case INDUCTION -> switch (index) {
                case 0 -> xy(47, 17);   // input A
                case 1 -> xy(63, 17);   // input B
                case 2 -> xy(56, 53);   // discharge
                case 3 -> xy(113, 35);  // output A
                case 4 -> xy(131, 35);  // output B
                default -> hidden();
            };
            default -> hidden();
        };
    }

    public static int scaled(int value, int max, int pixels) {
        if (value <= 0 || max <= 0) {
            return 0;
        }
        return (int) (pixels * (float) Math.min(value, max) / max);
    }

    /** Empty-track of the stored-EU gauge, GUI-local x,y,w,h. Null if this overlay has none. */
    public static int[] energyGauge(Overlay overlay) {
        return switch (overlay) {
            case PROCESS -> xywh(56, 36, 14, 14);
            case GENERATOR -> xywh(94, 35, 24, 17);
            case GEO -> xywh(89, 39, 22, 6);
            case STORAGE -> xywh(79, 34, 25, 16);
            case MINER -> xywh(81, 41, 14, 14); // JAR GuiMiner, under the pipe
            case PUMP -> xywh(62, 36, 14, 14); // JAR GuiPump energy bolt
            case CANNER -> xywh(31, 27, 14, 14); // JAR GuiCanner energy at 31,27
            case ELECTROLYZER -> xywh(79, 34, 25, 16);
            case INDUCTION -> xywh(56, 36, 14, 14);
            default -> null;
        };
    }

    public static String minerStatus(boolean active, boolean hasDrill, boolean hasPipe, int energy, int mineMode) {
        if (active) {
            return hasDrill ? "working" : "withdrawing";
        }
        if (!hasDrill) {
            return "no_drill";
        }
        if (!hasPipe) {
            return "no_pipe";
        }
        int needed = mineMode == 4 ? 200 : mineMode == 3 ? 6 : 3;
        return energy < needed ? "no_eu" : "idle";
    }

    /** Geo lava tank empty-track (JAR GuiGeoGenerator fuel gauge at 66,36). */
    public static int[] lavaGauge() {
        return xywh(66, 36, 14, 14);
    }

    public static boolean inside(int[] box, int x, int y) {
        return box != null && x >= box[0] && y >= box[1] && x < box[0] + box[2] && y < box[1] + box[3];
    }

    public static int inventoryTop(MachineKind kind) {
        return height(kind) - 82;
    }

    private static int[] xy(int x, int y) {
        return new int[] { x, y };
    }

    private static int[] xywh(int x, int y, int w, int h) {
        return new int[] { x, y, w, h };
    }

    private static int[] hidden() {
        return new int[] { -10000, -10000 };
    }

    private MachineGui() {}
}
