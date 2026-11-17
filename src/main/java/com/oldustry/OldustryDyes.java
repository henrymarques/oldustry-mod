package com.oldustry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** 26.1 has per-color items/blocks; 26.2 ColorCollection / Items.DYE.pick is gone here. */
public final class OldustryDyes {
    public static Item dye(DyeColor color) {
        return BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.getName() + "_dye"));
    }

    public static Block wool(DyeColor color) {
        return BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(color.getName() + "_wool"));
    }

    public static Block concrete(DyeColor color) {
        return BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(color.getName() + "_concrete"));
    }

    private OldustryDyes() {}
}
