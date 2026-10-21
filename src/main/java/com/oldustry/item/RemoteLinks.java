package com.oldustry.item;

import java.util.ArrayList;
import java.util.List;

/** Links on a Dynamite-O-Mote. One entry per dimension and block. */
public final class RemoteLinks {
    public static int indexOf(List<RemoteLink> links, RemoteLink pos) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).equals(pos)) {
                return i;
            }
        }
        return -1;
    }

    public static List<RemoteLink> add(List<RemoteLink> links, RemoteLink pos) {
        if (indexOf(links, pos) >= 0) {
            return links;
        }
        List<RemoteLink> next = new ArrayList<>(links.size() + 1);
        next.addAll(links);
        next.add(pos);
        return List.copyOf(next);
    }

    public static List<RemoteLink> removeAt(List<RemoteLink> links, int index) {
        List<RemoteLink> next = new ArrayList<>(links.size() - 1);
        for (int i = 0; i < links.size(); i++) {
            if (i != index) {
                next.add(links.get(i));
            }
        }
        return List.copyOf(next);
    }

    /** Links outside this dimension stay. The caller handles the ones inside it. */
    public static List<RemoteLink> exceptDimension(List<RemoteLink> links, String dimension) {
        List<RemoteLink> kept = new ArrayList<>();
        for (RemoteLink link : links) {
            if (!link.dimension().equals(dimension)) {
                kept.add(link);
            }
        }
        return List.copyOf(kept);
    }

    private RemoteLinks() {}
}
