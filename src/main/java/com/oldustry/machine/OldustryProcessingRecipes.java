package com.oldustry.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/** IC2 1.117 maps for macerator/extractor/compressor/recycler. Furnace uses vanilla smelting. */
public final class OldustryProcessingRecipes {
    /** One JEI-facing recipe row (input count from {@link #consume}). */
    public record Listed(@NonNull ItemStack input, @NonNull ItemStack output) {}
    public static ItemStack macerator(Item item) {
        if (item == Items.COPPER_ORE || item == Items.DEEPSLATE_COPPER_ORE || item == Items.RAW_COPPER) {
            return new ItemStack(OldustryItems.COPPER_DUST.get(), item == Items.RAW_COPPER ? 1 : 2);
        }
        if (item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE || item == Items.RAW_IRON) {
            return new ItemStack(OldustryItems.IRON_DUST.get(), item == Items.RAW_IRON ? 1 : 2);
        }
        if (item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE || item == Items.NETHER_GOLD_ORE || item == Items.RAW_GOLD) {
            return new ItemStack(OldustryItems.GOLD_DUST.get(), item == Items.RAW_GOLD ? 1 : 2);
        }
        if (item == OldustryItems.TIN_ORE.get() || item == OldustryItems.RAW_TIN.get()) {
            return new ItemStack(OldustryItems.TIN_DUST.get(), item == OldustryItems.RAW_TIN.get() ? 1 : 2);
        }
        if (item == Items.COAL) {
            return new ItemStack(OldustryItems.COAL_DUST.get());
        }
        if (item == Items.IRON_INGOT) {
            return new ItemStack(OldustryItems.IRON_DUST.get());
        }
        if (item == Items.GOLD_INGOT) {
            return new ItemStack(OldustryItems.GOLD_DUST.get());
        }
        if (item == Items.COPPER_INGOT) {
            return new ItemStack(OldustryItems.COPPER_DUST.get());
        }
        if (item == OldustryItems.TIN_INGOT.get()) {
            return new ItemStack(OldustryItems.TIN_DUST.get());
        }
        if (item == Items.STONE) {
            return new ItemStack(Items.COBBLESTONE);
        }
        if (item == Items.COBBLESTONE) {
            return new ItemStack(Items.SAND);
        }
        if (item == Items.GRAVEL) {
            return new ItemStack(Items.FLINT);
        }
        if (item == Items.SANDSTONE || item == Items.SMOOTH_SANDSTONE || item == Items.CHISELED_SANDSTONE) {
            return new ItemStack(Items.SAND);
        }
        if (item == Items.CLAY) {
            return new ItemStack(OldustryItems.CLAY_DUST.get(), 2);
        }
        if (item == OldustryItems.PLANTBALL.get()) {
            return new ItemStack(Items.DIRT, 8);
        }
        if (item == OldustryItems.COFFEE_BEANS.get()) {
            return new ItemStack(OldustryItems.COFFEE_POWDER.get(), 3);
        }
        if (item == Items.BONE) {
            return new ItemStack(Items.BONE_MEAL, 5);
        }
        if (item == Items.SPIDER_EYE) {
            return new ItemStack(OldustryItems.GRIN_POWDER.get(), 2);
        }
        if (item == Items.FERMENTED_SPIDER_EYE) {
            return new ItemStack(OldustryItems.GRIN_POWDER.get());
        }
        if (item == Items.GLOWSTONE) {
            return new ItemStack(Items.GLOWSTONE_DUST, 4);
        }
        if (item == Items.ICE) {
            return new ItemStack(Items.SNOWBALL);
        }
        if (item == Items.BLAZE_ROD) {
            return new ItemStack(Items.BLAZE_POWDER, 5);
        }
        if (item == Items.NETHER_QUARTZ_ORE) {
            return new ItemStack(Items.QUARTZ, 4);
        }
        if (item == Items.QUARTZ_BLOCK) {
            return new ItemStack(Items.QUARTZ, 6);
        }
        // Modern: ancient debris → 2 dust (furnace → netherite scrap). Not in IC2 1.117.
        if (item == Items.ANCIENT_DEBRIS) {
            return new ItemStack(OldustryItems.ANCIENT_SCRAP_DUST.get(), 2);
        }
        for (DyeColor color : DyeColor.values()) {
            if (item == Items.WOOL.pick(color)) {
                return new ItemStack(Items.STRING);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack extractor(Item item) {
        if (item == OldustryItems.STICKY_RESIN.get()) {
            return new ItemStack(OldustryItems.RUBBER.get(), 3);
        }
        if (item == OldustryItems.RUBBER_SAPLING.get() || item == OldustryItems.RUBBER_LOG.get()) {
            return new ItemStack(OldustryItems.RUBBER.get());
        }
        if (item == OldustryItems.BIO_CELL.get()) {
            return new ItemStack(OldustryItems.BIOFUEL_CELL.get());
        }
        if (item == OldustryItems.COAL_CELL.get()) {
            return new ItemStack(OldustryItems.COALFUEL_CELL.get());
        }
        if (item == OldustryItems.WATER_CELL.get()) {
            return new ItemStack(OldustryItems.HYDRATING_CELL.get());
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack compressor(Item item) {
        if (item == OldustryItems.PLANTBALL.get()) {
            return new ItemStack(OldustryItems.COMPRESSED_PLANTS.get());
        }
        if (item == OldustryItems.HYDRATED_COAL_DUST.get()) {
            return new ItemStack(OldustryItems.HYDRATED_COAL_CLUMP.get());
        }
        if (item == Items.SAND) {
            return new ItemStack(Items.SANDSTONE);
        }
        if (item == Items.SNOWBALL) {
            return new ItemStack(Blocks.ICE);
        }
        if (item == Items.NETHERRACK) {
            return new ItemStack(Blocks.NETHER_BRICKS);
        }
        if (item == Items.COPPER_INGOT) {
            return new ItemStack(OldustryItems.DENSE_COPPER_PLATE.get());
        }
        if (item == OldustryItems.MIXED_METAL_INGOT.get()) {
            return new ItemStack(OldustryItems.ADVANCED_ALLOY.get());
        }
        if (item == OldustryItems.CARBON_MESH.get()) {
            return new ItemStack(OldustryItems.CARBON_PLATE.get());
        }
        if (item == OldustryItems.COAL_BALL.get()) {
            return new ItemStack(OldustryItems.COMPRESSED_COAL_BALL.get());
        }
        if (item == OldustryItems.COAL_CHUNK.get()) {
            return new ItemStack(Items.DIAMOND);
        }
        if (item == OldustryItems.WATER_CELL.get()) {
            return new ItemStack(Items.SNOWBALL);
        }
        if (item == OldustryItems.EMPTY_CELL.get()) {
            return new ItemStack(OldustryItems.AIR_CELL.get());
        }
        if (item == OldustryItems.URANIUM_DROP.get() || item == OldustryItems.URANIUM_ORE.get()) {
            return new ItemStack(OldustryItems.URANIUM_INGOT.get());
        }
        if (item == OldustryItems.CONSTRUCTION_FOAM.get() || item == OldustryBlocks.CONSTRUCTION_FOAM.get().asItem()) {
            return new ItemStack(OldustryItems.CF_PELLET.get());
        }
        return ItemStack.EMPTY;
    }

    public static int consume(OldustryMachineKind.Type type, Item item) {
        if (type == OldustryMachineKind.Type.COMPRESSOR && item == Items.COPPER_INGOT) {
            return 8;
        }
        if (type == OldustryMachineKind.Type.COMPRESSOR && item == Items.NETHERRACK) {
            return 3;
        }
        return 1;
    }

    public static boolean recyclerBlacklist(Item item) {
        return item == Items.COBWEB
            || item == Items.STICK
            || item == Items.SNOWBALL
            || item == OldustryBlocks.WOODEN_SCAFFOLD.get().asItem();
    }

    /** All mapped recipes for JEI (macerator/extractor/compressor). Recycler is open-ended — not listed. */
    public static @NonNull List<@NonNull Listed> list(OldustryMachineKind.Type type) {
        Function<Item, ItemStack> map = switch (type) {
            case MACERATOR -> OldustryProcessingRecipes::macerator;
            case EXTRACTOR -> OldustryProcessingRecipes::extractor;
            case COMPRESSOR -> OldustryProcessingRecipes::compressor;
            default -> _ -> ItemStack.EMPTY;
        };
        List<@NonNull Listed> recipes = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack out = map.apply(item);
            if (out.isEmpty()) {
                continue;
            }
            @SuppressWarnings("null")
            ItemStack input = new ItemStack(item, consume(type, item));
            recipes.add(new Listed(input, out.copy()));
        }
        return recipes;
    }

    private OldustryProcessingRecipes() {}
}
