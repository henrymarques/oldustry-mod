package com.oldustry.item.tool;

import java.util.List;

import com.oldustry.registry.OldustryTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

/**
 * JAR {@code IC2.registerOre} / static {@code addValuableOre}: value by ore class.
 * Specific {@code c:} tags come before {@code #c:ores} (the {@code ore*} catch-all, value 1).
 */
public final class ValuableOre {
    private record Rated(TagKey<Block> tag, int value) {}

    private static final List<Rated> RATED = List.of(
        new Rated(Tags.Blocks.ORES_COPPER, 2),
        new Rated(OldustryTags.Blocks.ORES_TIN, 2),
        new Rated(Tags.Blocks.ORES_GOLD, 3),
        new Rated(Tags.Blocks.ORES_LAPIS, 3),
        new Rated(Tags.Blocks.ORES_REDSTONE, 3),
        new Rated(OldustryTags.Blocks.ORES_SILVER, 3),
        new Rated(Tags.Blocks.ORES_DIAMOND, 4),
        new Rated(OldustryTags.Blocks.ORES_URANIUM, 4),
        new Rated(OldustryTags.Blocks.ORES_RUBY, 4),
        new Rated(OldustryTags.Blocks.ORES_SAPPHIRE, 4),
        new Rated(OldustryTags.Blocks.ORES_GREEN_SAPPHIRE, 4),
        new Rated(Tags.Blocks.ORES_EMERALD, 5),
        new Rated(Tags.Blocks.ORES_QUARTZ, 5),
        new Rated(OldustryTags.Blocks.ORES_TUNGSTEN, 5),
        new Rated(Tags.Blocks.ORES, 1)
    );

    public static int value(BlockState state) {
        for (Rated rated : RATED) {
            if (state.is(rated.tag())) {
                return rated.value();
            }
        }
        return 0;
    }

    /** Table lookup used by the self-check. 0 when the tag is not rated. */
    public static int valueOf(TagKey<Block> tag) {
        for (Rated rated : RATED) {
            if (rated.tag().equals(tag)) {
                return rated.value();
            }
        }
        return 0;
    }

    /** {@code #c:ores} has to be last, or copper (also in that tag) would score 1. */
    public static boolean specificBeforeCatchAll() {
        Rated last = RATED.getLast();
        if (!last.tag().equals(Tags.Blocks.ORES) || last.value() != 1) {
            return false;
        }
        for (int i = 0; i < RATED.size() - 1; i++) {
            if (RATED.get(i).value() <= 1 || RATED.get(i).tag().equals(Tags.Blocks.ORES)) {
                return false;
            }
        }
        return true;
    }

    private ValuableOre() {}
}
