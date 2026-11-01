package com.oldustry.reactor;

import com.mojang.serialization.MapCodec;
import com.oldustry.item.construction.CfSprayerChargeRecipe;
import com.oldustry.registry.OldustryItems;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * IC2 {@code RecipeGradual} for the two condensators.
 * RSH takes redstone for 10000. LZH takes redstone for 5000 or lapis for 40000, never both.
 */
public final class OldustryCondensatorChargeRecipe extends CustomRecipe {
    public static final OldustryCondensatorChargeRecipe INSTANCE = new OldustryCondensatorChargeRecipe();
    public static final MapCodec<OldustryCondensatorChargeRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, OldustryCondensatorChargeRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OldustryCondensatorChargeRecipe>> SERIALIZER =
        CfSprayerChargeRecipe.SERIALIZERS.register("condensator_charge", () -> new RecipeSerializer<>(CODEC, STREAM_CODEC));

    public static void bootstrap() {
        SERIALIZER.getClass();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !assemble(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack condensator = ItemStack.EMPTY;
        int redstone = 0;
        int lapis = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (condensator.isEmpty() && stack.getItem() instanceof OldustryCondensatorItem) {
                condensator = stack;
            } else if (stack.is(Items.REDSTONE)) {
                redstone++;
            } else if (stack.is(Items.LAPIS_LAZULI)) {
                lapis++;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (condensator.isEmpty() || redstone + lapis == 0 || (redstone > 0 && lapis > 0)) {
            return ItemStack.EMPTY;
        }
        int restored;
        if (condensator.is(OldustryItems.RSH_CONDENSATOR.get())) {
            if (lapis > 0) {
                return ItemStack.EMPTY;
            }
            restored = 10_000 * redstone;
        } else if (redstone > 0) {
            restored = 5_000 * redstone;
        } else {
            restored = 40_000 * lapis;
        }
        ItemStack result = condensator.copy();
        result.setCount(1);
        int max = ((OldustryCondensatorItem) result.getItem()).maxDamage();
        OldustryReactorStore.setWear(result, OldustryReactor.repairWear(OldustryReactorStore.wear(result), restored, max));
        return result;
    }

    @Override
    public RecipeSerializer<OldustryCondensatorChargeRecipe> getSerializer() {
        return SERIALIZER.get();
    }
}
