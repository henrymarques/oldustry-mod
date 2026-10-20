package com.oldustry.item;

import java.util.EnumMap;

import com.oldustry.Oldustry;
import com.oldustry.registry.OldustryTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.ToolMaterial;

public final class OldustryMaterials {
    public static final ResourceKey<EquipmentAsset> BRONZE_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "bronze")
    );

    // Desvio: um pouco melhor que IRON(250/6/14); JAR tools usavam IRON+eff 5 (piores).
    public static final ToolMaterial BRONZE = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        BronzeStats.DURABILITY,
        BronzeStats.SPEED,
        BronzeStats.DAMAGE_BONUS,
        BronzeStats.ENCHANT,
        OldustryTags.Items.INGOTS_BRONZE
    );

    // Desvio speed 10/20 (JAR 8/16); enchant como picareta vanilla.
    // Durability unused (EU instead) — must not be 1 or the tool breaks and loses mining speed.
    public static final ToolMaterial MINING_DRILL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        Integer.MAX_VALUE,
        DrillStats.MINING_SPEED,
        2f,
        DrillStats.MINING_ENCHANT,
        OldustryTags.Items.INGOTS_REFINED_IRON
    );
    public static final ToolMaterial DIAMOND_DRILL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
        Integer.MAX_VALUE,
        DrillStats.DIAMOND_SPEED,
        3f,
        DrillStats.DIAMOND_ENCHANT,
        Tags.Items.GEMS_DIAMOND
    );
    // JAR EnumToolMaterial.IRON + efficiency 12; desvio speed 15. Durability unused (EU).
    public static final ToolMaterial CHAINSAW = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        Integer.MAX_VALUE,
        ChainsawStats.SPEED,
        2f,
        ChainsawStats.ENCHANT,
        OldustryTags.Items.INGOTS_REFINED_IRON
    );

    // IC2 bronze armor: durability 15, defense 3/8/6/3, enchant 9
    public static final ArmorMaterial BRONZE_ARMOR = new ArmorMaterial(
        15,
        Util.make(new EnumMap<>(ArmorType.class), map -> {
            map.put(ArmorType.BOOTS, 3);
            map.put(ArmorType.LEGGINGS, 6);
            map.put(ArmorType.CHESTPLATE, 8);
            map.put(ArmorType.HELMET, 3);
            map.put(ArmorType.BODY, 8);
        }),
        9,
        SoundEvents.ARMOR_EQUIP_IRON,
        0,
        0,
        OldustryTags.Items.INGOTS_BRONZE,
        BRONZE_EQUIPMENT
    );

    public static final ResourceKey<EquipmentAsset> ALLOY_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "alloy")
    );

    // IC2 alloy: durability 50, defense 4/9/7/4, enchant 12; toughness 2 = desvio moderno (JAR não tinha).
    public static final ArmorMaterial COMPOSITE_ARMOR = new ArmorMaterial(
        50,
        Util.make(new EnumMap<>(ArmorType.class), map -> {
            map.put(ArmorType.BOOTS, 4);
            map.put(ArmorType.LEGGINGS, 7);
            map.put(ArmorType.CHESTPLATE, 9);
            map.put(ArmorType.HELMET, 4);
            map.put(ArmorType.BODY, 9);
        }),
        12,
        SoundEvents.ARMOR_EQUIP_IRON,
        2f,
        0,
        OldustryTags.Items.ADVANCED_ALLOY,
        ALLOY_EQUIPMENT
    );

    public static final ResourceKey<EquipmentAsset> BATPACK_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "batpack")
    );
    public static final ResourceKey<EquipmentAsset> LAPPACK_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "lappack")
    );
    public static final ResourceKey<EquipmentAsset> JETPACK_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "jetpack")
    );
    public static final ResourceKey<EquipmentAsset> SOLAR_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "solar")
    );
    public static final ResourceKey<EquipmentAsset> RUBBER_EQUIPMENT = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "rubber")
    );

    /** Static boots (and similar): equippable; defense via bronze map until audited vs JAR utility. */
    public static ArmorMaterial pack(ResourceKey<EquipmentAsset> asset) {
        return new ArmorMaterial(
            BRONZE_ARMOR.durability(),
            BRONZE_ARMOR.defense(),
            BRONZE_ARMOR.enchantmentValue(),
            BRONZE_ARMOR.equipSound(),
            BRONZE_ARMOR.toughness(),
            BRONZE_ARMOR.knockbackResistance(),
            BRONZE_ARMOR.repairIngredient(),
            asset
        );
    }

    /**
     * JAR ItemArmorUtility / batpack: 0 defense.
     * Enchant value is unused — {@link #utilityArmor} skips {@code enchantable()} (26.2 requires positive).
     */
    public static ArmorMaterial utilityPack(ResourceKey<EquipmentAsset> asset) {
        return new ArmorMaterial(
            1,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                for (ArmorType type : ArmorType.values()) {
                    map.put(type, 0);
                }
            }),
            1,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            0,
            0,
            OldustryTags.Items.INGOTS_REFINED_IRON,
            asset
        );
    }

    /** Like {@code humanoidArmor} but no ENCHANTABLE and no attribute modifiers. */
    public static Item.Properties utilityArmor(Item.Properties props, ResourceKey<EquipmentAsset> asset, ArmorType type) {
        ArmorMaterial material = utilityPack(asset);
        return props
            .durability(type.getDurability(material.durability()))
            .component(
                DataComponents.EQUIPPABLE,
                Equippable.builder(type.getSlot())
                    .setEquipSound(material.equipSound())
                    .setAsset(material.assetId())
                    .build()
            )
            .repairable(material.repairIngredient());
    }

    private OldustryMaterials() {}
}
