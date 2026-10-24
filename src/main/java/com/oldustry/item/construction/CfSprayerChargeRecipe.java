package com.oldustry.item.construction;

import com.mojang.serialization.MapCodec;
import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** JAR {@code RecipeGradual} for the CF sprayer: one sprayer plus pellet slots, 100 foam each. */
public class CfSprayerChargeRecipe extends CustomRecipe {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Oldustry.MOD_ID);

    public static final CfSprayerChargeRecipe INSTANCE = new CfSprayerChargeRecipe();
    public static final MapCodec<CfSprayerChargeRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, CfSprayerChargeRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CfSprayerChargeRecipe>> SERIALIZER =
        SERIALIZERS.register("cf_sprayer_charge", () -> new RecipeSerializer<>(CODEC, STREAM_CODEC));

    public static void register(IEventBus bus) {
        ObsidianCellsRecipe.bootstrap();
        SERIALIZERS.register(bus);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !assemble(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack sprayer = ItemStack.EMPTY;
        int pellets = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (sprayer.isEmpty() && stack.is(OldustryItems.CF_SPRAYER.get())) {
                sprayer = stack;
            } else if (stack.is(OldustryItems.CF_PELLET.get())) {
                pellets++;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (sprayer.isEmpty() || pellets == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = sprayer.copy();
        result.setCount(1);
        SprayerItem.setCharge(result, SprayerItem.addPellets(SprayerItem.charge(sprayer), pellets));
        return result;
    }

    @Override
    public RecipeSerializer<CfSprayerChargeRecipe> getSerializer() {
        return SERIALIZER.get();
    }
}
