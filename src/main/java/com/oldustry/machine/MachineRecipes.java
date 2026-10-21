package com.oldustry.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/** IC2 1.117 maps for macerator/extractor/compressor/recycler. Furnace uses vanilla smelting. */
public final class MachineRecipes {
    /** One JEI-facing recipe row (input count from {@link #consume}). */
    public record Listed(@NonNull ItemStack input, @NonNull ItemStack output) {}
    public static ItemStack macerator(Item item) {
        if (item == Items.COPPER_ORE || item == Items.DEEPSLATE_COPPER_ORE || item == Items.RAW_COPPER) {
            return new ItemStack(ModItems.COPPER_DUST.get(), item == Items.RAW_COPPER ? 1 : 2);
        }
        if (item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE || item == Items.RAW_IRON) {
            return new ItemStack(ModItems.IRON_DUST.get(), item == Items.RAW_IRON ? 1 : 2);
        }
        if (item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE || item == Items.NETHER_GOLD_ORE || item == Items.RAW_GOLD) {
            return new ItemStack(ModItems.GOLD_DUST.get(), item == Items.RAW_GOLD ? 1 : 2);
        }
        if (item == ModItems.TIN_ORE.get() || item == ModItems.RAW_TIN.get()) {
            return new ItemStack(ModItems.TIN_DUST.get(), item == ModItems.RAW_TIN.get() ? 1 : 2);
        }
        if (item == Items.COAL) {
            return new ItemStack(ModItems.COAL_DUST.get());
        }
        if (item == Items.IRON_INGOT) {
            return new ItemStack(ModItems.IRON_DUST.get());
        }
        if (item == Items.GOLD_INGOT) {
            return new ItemStack(ModItems.GOLD_DUST.get());
        }
        if (item == Items.COPPER_INGOT) {
            return new ItemStack(ModItems.COPPER_DUST.get());
        }
        if (item == ModItems.TIN_INGOT.get()) {
            return new ItemStack(ModItems.TIN_DUST.get());
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
            return new ItemStack(ModItems.CLAY_DUST.get(), 2);
        }
        if (item == ModItems.PLANTBALL.get()) {
            return new ItemStack(Items.DIRT, 8);
        }
        if (item == ModItems.COFFEE_BEANS.get()) {
            return new ItemStack(ModItems.COFFEE_POWDER.get(), 3);
        }
        if (item == Items.BONE) {
            return new ItemStack(Items.BONE_MEAL, 5);
        }
        if (item == Items.SPIDER_EYE) {
            return new ItemStack(ModItems.GRIN_POWDER.get(), 2);
        }
        if (item == Items.FERMENTED_SPIDER_EYE) {
            return new ItemStack(ModItems.GRIN_POWDER.get());
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
            return new ItemStack(ModItems.ANCIENT_SCRAP_DUST.get(), 2);
        }
        for (DyeColor color : DyeColor.values()) {
            if (item == Items.WOOL.pick(color)) {
                return new ItemStack(Items.STRING);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack extractor(Item item) {
        if (item == ModItems.STICKY_RESIN.get()) {
            return new ItemStack(ModItems.RUBBER.get(), 3);
        }
        if (item == ModItems.RUBBER_SAPLING.get() || item == ModItems.RUBBER_LOG.get()) {
            return new ItemStack(ModItems.RUBBER.get());
        }
        if (item == ModItems.BIO_CELL.get()) {
            return new ItemStack(ModItems.BIOFUEL_CELL.get());
        }
        if (item == ModItems.COAL_CELL.get()) {
            return new ItemStack(ModItems.COALFUEL_CELL.get());
        }
        if (item == ModItems.WATER_CELL.get()) {
            return new ItemStack(ModItems.HYDRATING_CELL.get());
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack compressor(Item item) {
        if (item == ModItems.PLANTBALL.get()) {
            return new ItemStack(ModItems.COMPRESSED_PLANTS.get());
        }
        if (item == ModItems.HYDRATED_COAL_DUST.get()) {
            return new ItemStack(ModItems.HYDRATED_COAL_CLUMP.get());
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
            return new ItemStack(ModItems.DENSE_COPPER_PLATE.get());
        }
        if (item == ModItems.MIXED_METAL_INGOT.get()) {
            return new ItemStack(ModItems.ADVANCED_ALLOY.get());
        }
        if (item == ModItems.CARBON_MESH.get()) {
            return new ItemStack(ModItems.CARBON_PLATE.get());
        }
        if (item == ModItems.COAL_BALL.get()) {
            return new ItemStack(ModItems.COMPRESSED_COAL_BALL.get());
        }
        if (item == ModItems.COAL_CHUNK.get()) {
            return new ItemStack(Items.DIAMOND);
        }
        if (item == ModItems.WATER_CELL.get()) {
            return new ItemStack(Items.SNOWBALL);
        }
        if (item == ModItems.EMPTY_CELL.get()) {
            return new ItemStack(ModItems.AIR_CELL.get());
        }
        if (item == ModItems.URANIUM_DROP.get() || item == ModItems.URANIUM_ORE.get()) {
            return new ItemStack(ModItems.URANIUM_INGOT.get());
        }
        if (item == ModItems.CONSTRUCTION_FOAM.get() || item == ModBlocks.CONSTRUCTION_FOAM.get().asItem()) {
            return new ItemStack(ModItems.CF_PELLET.get());
        }
        return ItemStack.EMPTY;
    }

    public static int consume(MachineKind.Type type, Item item) {
        if (type == MachineKind.Type.COMPRESSOR && item == Items.COPPER_INGOT) {
            return 8;
        }
        if (type == MachineKind.Type.COMPRESSOR && item == Items.NETHERRACK) {
            return 3;
        }
        return 1;
    }

    public static boolean recyclerBlacklist(Item item) {
        return item == Items.COBWEB
            || item == Items.STICK
            || item == Items.SNOWBALL
            || item == ModBlocks.WOODEN_SCAFFOLD.get().asItem();
    }

    /** All mapped recipes for JEI (macerator/extractor/compressor). Recycler is open-ended — not listed. */
    public static @NonNull List<@NonNull Listed> list(MachineKind.Type type) {
        Function<Item, ItemStack> map = switch (type) {
            case MACERATOR -> MachineRecipes::macerator;
            case EXTRACTOR -> MachineRecipes::extractor;
            case COMPRESSOR -> MachineRecipes::compressor;
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

    private MachineRecipes() {}
}
