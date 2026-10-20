package com.oldustry.energy;

public final class ProcessLogic {
    public record Tick(int energy, int progress, boolean crafted) {}

    public static Tick tick(int energy, int progress, int euPerTick, int duration, boolean hasWork) {
        if (!hasWork) {
            return new Tick(energy, 0, false);
        }
        if (energy < euPerTick) {
            return new Tick(energy, progress, false);
        }
        int next = progress + 1;
        if (next >= duration) {
            return new Tick(energy - euPerTick, 0, true);
        }
        return new Tick(energy - euPerTick, next, false);
    }

    public static boolean recyclerScrap(int roll, int chance) {
        return roll % chance == 0;
    }

    private ProcessLogic() {}
}
