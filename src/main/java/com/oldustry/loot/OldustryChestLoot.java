package com.oldustry.loot;

import java.util.function.BiConsumer;

import com.oldustry.registry.OldustryItems;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class OldustryChestLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(OldustryLoot.INGOTS, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.TIN_INGOT.get()).setWeight(8).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
            .add(LootItem.lootTableItem(Items.COPPER_INGOT).setWeight(9).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))));

        output.accept(OldustryLoot.BRONZE_GEAR, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_PICKAXE.get()).setWeight(3))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_SWORD.get()).setWeight(3))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_HELMET.get()).setWeight(3))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_CHESTPLATE.get()).setWeight(3))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_LEGGINGS.get()).setWeight(3))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_BOOTS.get()).setWeight(3))));

        output.accept(OldustryLoot.MINE_SPECIAL, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.URANIUM_DROP.get()).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_PICKAXE.get()).setWeight(1))
            .add(LootItem.lootTableItem(OldustryItems.FILLED_TIN_CAN.get()).setWeight(8).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 16))))));

        output.accept(OldustryLoot.UU_RARE, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.UU_MATTER.get()).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
            .add(EmptyLootItem.emptyItem().setWeight(8))));

        output.accept(OldustryLoot.IRIDIUM_UNIQUE, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.IRIDIUM_ORE.get()).setWeight(1))
            .add(EmptyLootItem.emptyItem().setWeight(20))));

        output.accept(OldustryLoot.SAPLING, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.RUBBER_SAPLING.get()).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
            .add(EmptyLootItem.emptyItem().setWeight(6))));

        output.accept(OldustryLoot.SAPLING_ONE, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.RUBBER_SAPLING.get()))));

        output.accept(OldustryLoot.TREETAP, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.TREETAP.get()))));

        output.accept(OldustryLoot.CANS, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.FILLED_TIN_CAN.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 16))))));

        output.accept(OldustryLoot.BRONZE_INGOT, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_INGOT.get()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
            .add(EmptyLootItem.emptyItem().setWeight(3))));

        output.accept(OldustryLoot.STRONGHOLD_PICK, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.BRONZE_PICKAXE.get()).setWeight(1))
            .add(EmptyLootItem.emptyItem().setWeight(4))));

        output.accept(OldustryLoot.TIN_ONE, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.TIN_INGOT.get()).setWeight(1))
            .add(EmptyLootItem.emptyItem().setWeight(20))));

        output.accept(OldustryLoot.TIN_POT, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(OldustryItems.TIN_INGOT.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))));
    }
}
