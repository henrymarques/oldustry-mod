package com.oldustry.item;

import com.oldustry.item.explosive.RemoteLink;
import com.oldustry.item.explosive.RemoteLinks;

import java.util.List;

public final class RemoteLinksCheck {
    public static void main(String[] args) {
        RemoteLink a = new RemoteLink("minecraft:overworld", 1, 2, 3);
        RemoteLink sameSpotNether = new RemoteLink("minecraft:the_nether", 1, 2, 3);
        RemoteLink b = new RemoteLink("minecraft:overworld", 4, 5, 6);

        List<RemoteLink> links = RemoteLinks.add(List.of(), a);
        links = RemoteLinks.add(links, b);
        links = RemoteLinks.add(links, a);
        links = RemoteLinks.add(links, sameSpotNether);
        assert links.size() == 3;
        assert RemoteLinks.indexOf(links, a) == 0;
        assert RemoteLinks.indexOf(links, sameSpotNether) == 2;
        assert RemoteLinks.indexOf(links, new RemoteLink("minecraft:overworld", 0, 0, 0)) == -1;

        List<RemoteLink> kept = RemoteLinks.exceptDimension(links, "minecraft:overworld");
        assert kept.size() == 1;
        assert kept.get(0).equals(sameSpotNether);

        links = RemoteLinks.removeAt(links, 0);
        assert links.size() == 2;
        assert links.get(0).equals(b);
        System.out.println("RemoteLinksCheck ok");
    }
}
