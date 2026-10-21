package com.oldustry.item;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.oldustry.registry.ModItems;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Two water cells and two lava cells become obsidian. The cells are spent, unlike liquid crafting. */
public final class ObsidianCellsRecipe extends CustomRecipe {
    public static final ObsidianCellsRecipe INSTANCE = new ObsidianCellsRecipe();
    public static final MapCodec<ObsidianCellsRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, ObsidianCellsRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ObsidianCellsRecipe>> SERIALIZER =
        CfSprayerChargeRecipe.SERIALIZERS.register("obsidian_cells", () -> new RecipeSerializer<>(CODEC, STREAM_CODEC));

    public static void bootstrap() {
        SERIALIZER.getClass();
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.BUILDING;
    }

    private static List<Ingredient> ingredients() {
        return List.of(
            Ingredient.of(ModItems.WATER_CELL.get()),
            Ingredient.of(ModItems.WATER_CELL.get()),
            Ingredient.of(ModItems.LAVA_CELL.get()),
            Ingredient.of(ModItems.LAVA_CELL.get())
        );
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredients());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(
            ingredients().stream().map(Ingredient::display).toList(),
            new SlotDisplay.ItemSlotDisplay(Items.OBSIDIAN),
            new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        ));
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int water = 0;
        int lava = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(ModItems.WATER_CELL.get())) {
                water++;
            } else if (stack.is(ModItems.LAVA_CELL.get())) {
                lava++;
            } else {
                return false;
            }
        }
        return water == 2 && lava == 2;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return new ItemStack(Items.OBSIDIAN);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<ObsidianCellsRecipe> getSerializer() {
        return SERIALIZER.get();
    }
}
