package com.oldustry.machine;

/** JAR {@code ItemFrequencyTransmitter}. Damage 1 is {@code latched}; air clears the latch, then the link. */
public final class OldustryFrequency {
    public record Link(boolean set, int x, int y, int z, boolean latched) {
        public Link latch() {
            return new Link(set, x, y, z, true);
        }
    }

    public enum Click {
        STORED,
        SELF,
        UNCHANGED,
        LINKED
    }

    public record ClickResult(Link transmitter, Click click, boolean pointHere, boolean pointBack) {}

    public static Link empty() {
        return new Link(false, 0, 0, 0, false);
    }

    public static Link air(Link link) {
        if (link.latched()) {
            return new Link(link.set(), link.x(), link.y(), link.z(), false);
        }
        if (link.set()) {
            return new Link(false, link.x(), link.y(), link.z(), false);
        }
        return link;
    }

    /**
     * @param storedExists the remembered block is still a teleporter
     * @param destSet the clicked teleporter already has a target
     * @param storedOpen the remembered teleporter exists and has no target yet
     */
    public static ClickResult click(
        Link link,
        int x,
        int y,
        int z,
        boolean storedExists,
        boolean destSet,
        int destX,
        int destY,
        int destZ,
        boolean storedOpen
    ) {
        boolean set = link.set() && storedExists;
        if (!set) {
            return new ClickResult(new Link(true, x, y, z, true), Click.STORED, false, false);
        }
        if (x == link.x() && y == link.y() && z == link.z()) {
            return new ClickResult(link.latch(), Click.SELF, false, false);
        }
        if (destSet && destX == link.x() && destY == link.y() && destZ == link.z()) {
            return new ClickResult(link.latch(), Click.UNCHANGED, false, false);
        }
        return new ClickResult(link.latch(), Click.LINKED, true, storedOpen);
    }

    private OldustryFrequency() {}
}
