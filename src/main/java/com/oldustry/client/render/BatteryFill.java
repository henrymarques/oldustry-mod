package com.oldustry.client.render;

import com.mojang.serialization.MapCodec;

import com.oldustry.item.ElectricItem;
import com.oldustry.item.OldustryBatteryIcon;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.jspecify.annotations.Nullable;

/** JAR {@code ItemBattery.getIcon}: five frames from the stored charge. */
@OnlyIn(Dist.CLIENT)
public record BatteryFill() implements RangeSelectItemModelProperty {
    public static final MapCodec<BatteryFill> CODEC = MapCodec.unit(new BatteryFill());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        if (!(stack.getItem() instanceof ElectricItem electric)) {
            return 0f;
        }
        return OldustryBatteryIcon.frame(ElectricItem.energy(stack), electric.spec().max());
    }

    @Override
    public MapCodec<BatteryFill> type() {
        return CODEC;
    }
}
