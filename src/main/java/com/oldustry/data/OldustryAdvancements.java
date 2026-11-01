package com.oldustry.data;

import java.util.function.Consumer;

import com.oldustry.registry.OldustryItems;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public final class OldustryAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
        item(output, "mine_ore", OldustryItems.TIN_ORE.get(), "minecraft:story/mine_stone", true);
        item(output, "acquire_resin", OldustryItems.STICKY_RESIN.get(), "minecraft:story/mine_stone", false);
        item(output, "acquire_refined_iron", OldustryItems.REFINED_IRON_INGOT.get(), "minecraft:story/smelt_iron", false);
        item(output, "build_improved_furnace", OldustryItems.IMPROVED_FURNACE.get(), "oldustry:acquire_refined_iron", false);
        item(output, "build_cable", OldustryItems.INSULATED_COPPER_CABLE.get(), "oldustry:acquire_refined_iron", false);
        item(output, "build_generator", OldustryItems.GENERATOR.get(), "oldustry:build_cable", false);
        item(output, "build_macerator", OldustryItems.MACERATOR.get(), "oldustry:build_generator", false);
        item(output, "build_coal_diamond", OldustryItems.COAL_CHUNK.get(), "oldustry:build_macerator", false);
        item(output, "build_elec_furnace", OldustryItems.ELECTRIC_FURNACE.get(), "oldustry:build_improved_furnace", false);
        item(output, "build_ind_furnace", OldustryItems.INDUCTION.get(), "oldustry:build_elec_furnace", false);
        item(output, "build_compressor", OldustryItems.COMPRESSOR.get(), "oldustry:build_generator", false);
        item(output, "compress_uranium", OldustryItems.URANIUM_INGOT.get(), "oldustry:build_compressor", false);
        item(output, "build_extractor", OldustryItems.EXTRACTOR.get(), "oldustry:build_generator", false);
        item(output, "build_batbox", OldustryItems.BATBOX.get(), "oldustry:build_generator", false);
        item(output, "build_drill", OldustryItems.MINING_DRILL.get(), "oldustry:build_generator", false);
        item(output, "build_ddrill", OldustryItems.DIAMOND_DRILL.get(), "oldustry:build_drill", false);
        item(output, "build_chainsaw", OldustryItems.CHAINSAW.get(), "oldustry:build_generator", false);
        challenge(output, "kill_creeper_chainsaw", OldustryItems.CHAINSAW.get(), "oldustry:build_chainsaw", "killed");
        item(output, "build_mfe", OldustryItems.MFE.get(), "oldustry:build_batbox", false);
        item(output, "build_mining_laser", OldustryItems.MINING_LASER.get(), "oldustry:build_mfe", false);
        challenge(output, "kill_dragon_mining_laser", OldustryItems.MINING_LASER.get(), "oldustry:build_mining_laser", "killed");
        item(output, "build_mfsu", OldustryItems.MFSU.get(), "oldustry:build_mfe", false);
        item(output, "build_teleporter", OldustryItems.TELEPORTER.get(), "oldustry:build_mfsu", false);
        challenge(output, "teleport_far_away", OldustryItems.TELEPORTER.get(), "oldustry:build_teleporter", "far");
        item(output, "build_terraformer", OldustryItems.TERRAFORMER.get(), "oldustry:build_mfsu", false);
        challenge(output, "terraform_end_cultivation", OldustryItems.TFBP_CULTIVATION.get(), "oldustry:build_terraformer", "end");
        item(output, "build_mass_fab", OldustryItems.MASS_FABRICATOR.get(), "oldustry:build_batbox", false);
        item(output, "acquire_matter", OldustryItems.UU_MATTER.get(), "oldustry:build_mass_fab", false);
        quantum(output);
        challenge(output, "starve_with_q_helmet", OldustryItems.FILLED_TIN_CAN.get(), "oldustry:build_q_armor", "starved");
        challenge(output, "die_from_own_nuke", OldustryItems.NUKE.get(), "oldustry:compress_uranium", "nuked");
    }

    /** Any quantum piece. JAR {@code buildQArmor} checks the four ids. */
    private static void quantum(Consumer<AdvancementHolder> output) {
        Advancement.Builder.advancement()
            .parent(AdvancementSubProvider.createPlaceholder("oldustry:acquire_matter"))
            .display(
                OldustryItems.QUANTUM_CHESTPLATE.get(),
                Component.translatable("advancements.oldustry.build_q_armor.title"),
                Component.translatable("advancements.oldustry.build_q_armor.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("helmet", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.QUANTUM_HELMET.get()))
            .addCriterion("chest", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.QUANTUM_CHESTPLATE.get()))
            .addCriterion("legs", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.QUANTUM_LEGGINGS.get()))
            .addCriterion("boots", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.QUANTUM_BOOTS.get()))
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(output, "oldustry:build_q_armor");
    }

    private static void challenge(Consumer<AdvancementHolder> output, String id, ItemLike icon, String parent, String criterion) {
        Advancement.Builder.advancement()
            .parent(AdvancementSubProvider.createPlaceholder(parent))
            .display(
                icon,
                Component.translatable("advancements.oldustry." + id + ".title"),
                Component.translatable("advancements.oldustry." + id + ".description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .addCriterion(criterion, CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(output, "oldustry:" + id);
    }

    private static void item(Consumer<AdvancementHolder> output, String id, ItemLike icon, String parent, boolean orOres) {
        Advancement.Builder builder = Advancement.Builder.advancement()
            .parent(AdvancementSubProvider.createPlaceholder(parent))
            .display(
                icon,
                Component.translatable("advancements.oldustry." + id + ".title"),
                Component.translatable("advancements.oldustry." + id + ".description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            );
        if (id.equals("build_ind_furnace")) {
            builder.addCriterion("crafted", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                ResourceKey.<Recipe<?>>create(Registries.RECIPE, Identifier.fromNamespaceAndPath("oldustry", "induction"))));
        } else {
            builder.addCriterion("has", InventoryChangeTrigger.TriggerInstance.hasItems(icon));
        }
        if (orOres) {
            builder.addCriterion("raw_tin", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.RAW_TIN.get()))
                .addCriterion("uranium_drop", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.URANIUM_DROP.get()))
                .addCriterion("uranium_ore", InventoryChangeTrigger.TriggerInstance.hasItems(OldustryItems.URANIUM_ORE.get()))
                .requirements(AdvancementRequirements.Strategy.OR);
        }
        builder.save(output, "oldustry:" + id);
    }
}
