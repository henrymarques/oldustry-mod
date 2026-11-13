package com.oldustry.client.render;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

/** JAR {@code ItemNanoSaber.getIcon}: while active, each draw picks active or activeAlternate. */
public record SaberFlicker() implements RangeSelectItemModelProperty {
    public static final MapCodec<SaberFlicker> CODEC = MapCodec.unit(new SaberFlicker());
    private static final RandomSource RANDOM = RandomSource.create();

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return RANDOM.nextBoolean() ? 1f : 0f;
    }

    @Override
    public MapCodec<SaberFlicker> type() {
        return CODEC;
    }
}
