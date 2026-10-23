package com.oldustry.block;

import java.util.UUID;

/** Access from {@code TileEntityPersonalChest.permitsAccess} / {@code wrenchCanRemove}. The name in the JAR is a UUID here. */
public final class PersonalSafeAccess {
    public enum Verdict {
        DENY,
        CLAIM,
        ALLOW
    }

    private PersonalSafeAccess() {}

    public static Verdict open(UUID owner, UUID player, boolean privileged) {
        if (player == null) {
            return Verdict.DENY;
        }
        if (owner == null) {
            return Verdict.CLAIM;
        }
        if (owner.equals(player) || privileged) {
            return Verdict.ALLOW;
        }
        return Verdict.DENY;
    }

    /** An empty safe the player may open. A denied player does not take it off with a wrench. */
    public static boolean wrenchRemoves(Verdict verdict, boolean empty) {
        return verdict != Verdict.DENY && empty;
    }

    /** JAR {@code (++ticks % 20) * 4 == 0}: the viewer count is resent every 20 ticks. */
    public static boolean syncViewers(int ticksSinceSync) {
        return ticksSinceSync % 20 * 4 == 0;
    }
}
