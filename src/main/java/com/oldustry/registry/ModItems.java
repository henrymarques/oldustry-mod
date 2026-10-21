package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.energy.ElectricCharge;
import com.oldustry.item.CfPackItem;
import com.oldustry.item.CoffeeMugItem;
import com.oldustry.item.CutterItem;
import com.oldustry.item.DebugItem;
import com.oldustry.item.DynamiteItem;
import com.oldustry.item.RemoteItem;
import com.oldustry.item.ChainsawItem;
import com.oldustry.item.ChainsawStats;
import com.oldustry.item.DrillStats;
import com.oldustry.item.MachineBlockItem;
import com.oldustry.item.ElectricArmorItem;
import com.oldustry.item.ElectricItem;
import com.oldustry.item.ElectricToolItem;
import com.oldustry.item.ElectricTreetapItem;
import com.oldustry.item.ElectricWrenchItem;
import com.oldustry.item.EuReaderItem;
import com.oldustry.item.FilledTinCanItem;
import com.oldustry.item.FuelJetpackItem;
import com.oldustry.item.OldustryMaterials;
import com.oldustry.item.PainterItem;
import com.oldustry.item.ReBatteryItem;
import com.oldustry.item.ScannerItem;
import com.oldustry.item.ScrapBoxItem;
import com.oldustry.item.SolarHelmetItem;
import com.oldustry.item.SprayerItem;
import com.oldustry.item.StaticBootsItem;
import com.oldustry.item.StickyResinItem;
import com.oldustry.item.SuBatteryItem;
import com.oldustry.item.ToolboxItem;
import com.oldustry.item.TreetapItem;
import com.oldustry.item.UpgradeItem;
import com.oldustry.item.WrenchItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.ColorCollection;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Oldustry.MOD_ID);

    public static final DeferredItem<BlockItem> TIN_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.TIN_ORE);
    public static final DeferredItem<BlockItem> URANIUM_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.URANIUM_ORE);
    public static final DeferredItem<BlockItem> TIN_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.TIN_BLOCK);
    public static final DeferredItem<BlockItem> BRONZE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.BRONZE_BLOCK);
    public static final DeferredItem<BlockItem> URANIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.URANIUM_BLOCK);
    public static final DeferredItem<BlockItem> REINFORCED_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.REINFORCED_STONE);
    public static final DeferredItem<BlockItem> REINFORCED_GLASS = ITEMS.registerSimpleBlockItem(ModBlocks.REINFORCED_GLASS);
    public static final DeferredItem<Item> REINFORCED_DOOR = ITEMS.registerItem("reinforced_door", props -> new DoubleHighBlockItem(ModBlocks.REINFORCED_DOOR.get(), props.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> IRON_FENCE = ITEMS.registerSimpleBlockItem(ModBlocks.IRON_FENCE);
    public static final DeferredItem<BlockItem> WOODEN_SCAFFOLD = ITEMS.registerSimpleBlockItem(ModBlocks.WOODEN_SCAFFOLD);
    public static final DeferredItem<BlockItem> IRON_SCAFFOLD = ITEMS.registerSimpleBlockItem(ModBlocks.IRON_SCAFFOLD);
    public static final DeferredItem<BlockItem> RUBBER_SHEET = ITEMS.registerSimpleBlockItem(ModBlocks.RUBBER_SHEET);
    public static final DeferredItem<BlockItem> RESIN_SHEET = ITEMS.registerSimpleBlockItem(ModBlocks.RESIN_SHEET);
    public static final DeferredItem<BlockItem> RUBBER_LOG = ITEMS.registerSimpleBlockItem(ModBlocks.RUBBER_LOG);
    public static final DeferredItem<BlockItem> RUBBER_LEAVES = ITEMS.registerSimpleBlockItem(ModBlocks.RUBBER_LEAVES);
    public static final DeferredItem<BlockItem> RUBBER_SAPLING = ITEMS.registerSimpleBlockItem(ModBlocks.RUBBER_SAPLING);
    public static final DeferredItem<BlockItem> IMPROVED_FURNACE = ITEMS.registerSimpleBlockItem(ModBlocks.IMPROVED_FURNACE);
    public static final DeferredItem<BlockItem> CONSTRUCTION_FOAM = ITEMS.registerSimpleBlockItem(ModBlocks.CONSTRUCTION_FOAM);
    public static final DeferredItem<BlockItem> MACHINE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.MACHINE_CASING);
    public static final DeferredItem<BlockItem> ADVANCED_MACHINE = ITEMS.registerSimpleBlockItem(ModBlocks.ADVANCED_MACHINE);
    public static final DeferredItem<Item> GENERATOR = ITEMS.registerItem("generator",
        props -> new MachineBlockItem(ModBlocks.GENERATOR.get(), props.useBlockDescriptionPrefix()));
    public static final DeferredItem<Item> GEO_GENERATOR = ITEMS.registerItem("geo_generator",
        props -> new MachineBlockItem(ModBlocks.GEO_GENERATOR.get(), props.useBlockDescriptionPrefix()));
    public static final DeferredItem<Item> SOLAR_PANEL = ITEMS.registerItem("solar_panel",
        props -> new MachineBlockItem(ModBlocks.SOLAR_PANEL.get(), props.useBlockDescriptionPrefix()));
    public static final DeferredItem<Item> WATER_MILL = ITEMS.registerItem("water_mill",
        props -> new MachineBlockItem(ModBlocks.WATER_MILL.get(), props.useBlockDescriptionPrefix()));
    public static final DeferredItem<Item> WIND_MILL = ITEMS.registerItem("wind_mill",
        props -> new MachineBlockItem(ModBlocks.WIND_MILL.get(), props.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> BATBOX = ITEMS.registerSimpleBlockItem(ModBlocks.BATBOX);
    public static final DeferredItem<BlockItem> MFE = ITEMS.registerSimpleBlockItem(ModBlocks.MFE);
    public static final DeferredItem<BlockItem> MFSU = ITEMS.registerSimpleBlockItem(ModBlocks.MFSU);
    public static final DeferredItem<BlockItem> LV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ModBlocks.LV_TRANSFORMER);
    public static final DeferredItem<BlockItem> MV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ModBlocks.MV_TRANSFORMER);
    public static final DeferredItem<BlockItem> HV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ModBlocks.HV_TRANSFORMER);
    public static final DeferredItem<BlockItem> MACERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.MACERATOR);
    public static final DeferredItem<BlockItem> EXTRACTOR = ITEMS.registerSimpleBlockItem(ModBlocks.EXTRACTOR);
    public static final DeferredItem<BlockItem> COMPRESSOR = ITEMS.registerSimpleBlockItem(ModBlocks.COMPRESSOR);
    public static final DeferredItem<BlockItem> RECYCLER = ITEMS.registerSimpleBlockItem(ModBlocks.RECYCLER);
    public static final DeferredItem<BlockItem> ELECTRIC_FURNACE = ITEMS.registerSimpleBlockItem(ModBlocks.ELECTRIC_FURNACE);
    public static final DeferredItem<BlockItem> LUMINATOR = ITEMS.registerSimpleBlockItem(ModBlocks.LUMINATOR);
    public static final DeferredItem<BlockItem> MINER = ITEMS.registerSimpleBlockItem(ModBlocks.MINER);
    public static final DeferredItem<BlockItem> PUMP = ITEMS.registerSimpleBlockItem(ModBlocks.PUMP);
    public static final DeferredItem<BlockItem> CANNER = ITEMS.registerSimpleBlockItem(ModBlocks.CANNER);
    public static final DeferredItem<BlockItem> ELECTROLYZER = ITEMS.registerSimpleBlockItem(ModBlocks.ELECTROLYZER);
    public static final DeferredItem<BlockItem> MAGNETIZER = ITEMS.registerSimpleBlockItem(ModBlocks.MAGNETIZER);
    public static final DeferredItem<BlockItem> INDUCTION = ITEMS.registerSimpleBlockItem(ModBlocks.INDUCTION);
    public static final DeferredItem<BlockItem> MINING_PIPE = ITEMS.registerSimpleBlockItem(ModBlocks.MINING_PIPE);
    public static final DeferredItem<BlockItem> NUKE = ITEMS.registerSimpleBlockItem(ModBlocks.NUKE);
    public static final DeferredItem<BlockItem> ITNT = ITEMS.registerSimpleBlockItem(ModBlocks.ITNT);
    public static final DeferredItem<BlockItem> INSULATED_COPPER_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.INSULATED_COPPER_CABLE);
    public static final DeferredItem<BlockItem> COPPER_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.COPPER_CABLE);
    public static final DeferredItem<BlockItem> GOLD_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.GOLD_CABLE);
    public static final DeferredItem<BlockItem> INSULATED_GOLD_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.INSULATED_GOLD_CABLE);
    public static final DeferredItem<BlockItem> GOLD_CABLE_2X = ITEMS.registerSimpleBlockItem(ModBlocks.GOLD_CABLE_2X);
    public static final DeferredItem<BlockItem> HV_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.HV_CABLE);
    public static final DeferredItem<BlockItem> INSULATED_HV_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.INSULATED_HV_CABLE);
    public static final DeferredItem<BlockItem> HV_CABLE_2X = ITEMS.registerSimpleBlockItem(ModBlocks.HV_CABLE_2X);
    public static final DeferredItem<BlockItem> HV_CABLE_3X = ITEMS.registerSimpleBlockItem(ModBlocks.HV_CABLE_3X);
    public static final DeferredItem<BlockItem> GLASS_FIBRE_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.GLASS_FIBRE_CABLE);
    public static final DeferredItem<BlockItem> TIN_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.TIN_CABLE);
    public static final DeferredItem<BlockItem> DETECTOR_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.DETECTOR_CABLE);
    public static final DeferredItem<BlockItem> SPLITTER_CABLE = ITEMS.registerSimpleBlockItem(ModBlocks.SPLITTER_CABLE);

    public static final DeferredItem<Item> REFINED_IRON_INGOT = item("refined_iron_ingot");
    public static final DeferredItem<Item> RAW_TIN = item("raw_tin");
    public static final DeferredItem<Item> TIN_INGOT = item("tin_ingot");
    public static final DeferredItem<Item> BRONZE_INGOT = item("bronze_ingot");
    public static final DeferredItem<Item> URANIUM_INGOT = item("uranium_ingot");
    public static final DeferredItem<Item> URANIUM_DROP = item("uranium_drop");
    public static final DeferredItem<Item> MIXED_METAL_INGOT = item("mixed_metal_ingot");

    public static final DeferredItem<Item> COAL_DUST = item("coal_dust");
    public static final DeferredItem<Item> IRON_DUST = item("iron_dust");
    public static final DeferredItem<Item> GOLD_DUST = item("gold_dust");
    public static final DeferredItem<Item> COPPER_DUST = item("copper_dust");
    public static final DeferredItem<Item> TIN_DUST = item("tin_dust");
    public static final DeferredItem<Item> BRONZE_DUST = item("bronze_dust");
    public static final DeferredItem<Item> CLAY_DUST = item("clay_dust");
    public static final DeferredItem<Item> SILVER_DUST = item("silver_dust");
    public static final DeferredItem<Item> SMALL_IRON_DUST = item("small_iron_dust");
    public static final DeferredItem<Item> ANCIENT_SCRAP_DUST = item("ancient_scrap_dust");

    public static final DeferredItem<Item> ELECTRONIC_CIRCUIT = item("electronic_circuit");
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = item("advanced_circuit");
    public static final DeferredItem<Item> ADVANCED_ALLOY = item("advanced_alloy");
    public static final DeferredItem<Item> CARBON_FIBER = item("carbon_fiber");
    public static final DeferredItem<Item> CARBON_MESH = item("carbon_mesh");
    public static final DeferredItem<Item> CARBON_PLATE = item("carbon_plate");
    public static final DeferredItem<Item> DENSE_COPPER_PLATE = item("dense_copper_plate");
    public static final DeferredItem<Item> INDUSTRIAL_DIAMOND = item("industrial_diamond");
    public static final DeferredItem<Item> SCRAP = item("scrap");
    public static final DeferredItem<Item> SCRAP_BOX = ITEMS.registerItem("scrap_box", ScrapBoxItem::new);
    public static final DeferredItem<Item> UU_MATTER = item("uu_matter");
    public static final DeferredItem<Item> IRIDIUM_ORE = item("iridium_ore");
    public static final DeferredItem<Item> IRIDIUM_PLATE = item("iridium_plate");
    public static final DeferredItem<Item> INDUSTRIAL_CREDIT = item("industrial_credit");
    public static final DeferredItem<Item> CF_PELLET = item("cf_pellet");
    public static final DeferredItem<Item> CF_PACK = ITEMS.registerItem("cf_pack", props -> new CfPackItem(
        OldustryMaterials.utilityEquip(
            props.stacksTo(1).component(ModDataComponents.FOAM.get(), CfPackItem.CRAFTED),
            OldustryMaterials.BATPACK_EQUIPMENT,
            ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> COAL_BALL = item("coal_ball");
    public static final DeferredItem<Item> COMPRESSED_COAL_BALL = item("compressed_coal_ball");
    public static final DeferredItem<Item> COAL_CHUNK = item("coal_chunk");

    public static final DeferredItem<Item> STICKY_RESIN = ITEMS.registerItem("sticky_resin", StickyResinItem::new);
    public static final DeferredItem<Item> RUBBER = item("rubber");
    public static final DeferredItem<Item> TREETAP = ITEMS.registerItem("treetap", props -> new TreetapItem(props.stacksTo(1).durability(16)));
    public static final DeferredItem<Item> CF_SPRAYER = ITEMS.registerItem("cf_sprayer", props -> new SprayerItem(
        props.stacksTo(1).component(ModDataComponents.FOAM.get(), SprayerItem.CRAFTED)));

    public static final DeferredItem<Item> BRONZE_PICKAXE = ITEMS.registerItem("bronze_pickaxe", props -> new Item(props.pickaxe(OldustryMaterials.BRONZE, 1, -2.8f)));
    public static final DeferredItem<Item> BRONZE_AXE = ITEMS.registerItem("bronze_axe", props -> new Item(props.axe(OldustryMaterials.BRONZE, 6, -3.1f)));
    public static final DeferredItem<Item> BRONZE_SWORD = ITEMS.registerItem("bronze_sword", props -> new Item(props.sword(OldustryMaterials.BRONZE, 3, -2.4f)));
    public static final DeferredItem<Item> BRONZE_SHOVEL = ITEMS.registerItem("bronze_shovel", props -> new Item(props.shovel(OldustryMaterials.BRONZE, 1.5f, -3f)));
    public static final DeferredItem<Item> BRONZE_HOE = ITEMS.registerItem("bronze_hoe", props -> new Item(props.hoe(OldustryMaterials.BRONZE, -2, -1f)));

    public static final DeferredItem<Item> BRONZE_HELMET = ITEMS.registerItem("bronze_helmet", props -> new Item(props.humanoidArmor(OldustryMaterials.BRONZE_ARMOR, ArmorType.HELMET)));
    public static final DeferredItem<Item> BRONZE_CHESTPLATE = ITEMS.registerItem("bronze_chestplate", props -> new Item(props.humanoidArmor(OldustryMaterials.BRONZE_ARMOR, ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> BRONZE_LEGGINGS = ITEMS.registerItem("bronze_leggings", props -> new Item(props.humanoidArmor(OldustryMaterials.BRONZE_ARMOR, ArmorType.LEGGINGS)));
    public static final DeferredItem<Item> BRONZE_BOOTS = ITEMS.registerItem("bronze_boots", props -> new Item(props.humanoidArmor(OldustryMaterials.BRONZE_ARMOR, ArmorType.BOOTS)));
    public static final DeferredItem<Item> COMPOSITE_VEST = ITEMS.registerItem("composite_vest", props -> new Item(props.humanoidArmor(OldustryMaterials.COMPOSITE_ARMOR, ArmorType.CHESTPLATE)));

    public static final DeferredItem<Item> PAINTER = item("painter");
    public static final ColorCollection<DeferredItem<Item>> PAINTERS = ColorCollection.prefixWithColor(ColorCollection.create("painter")).map(ModItems::coloredPainter);

    public static final DeferredItem<Item> TOOLBOX = ITEMS.registerItem("toolbox", ToolboxItem::new);
    public static final DeferredItem<Item> TIN_CAN = item("tin_can");
    public static final DeferredItem<Item> FILLED_TIN_CAN = ITEMS.registerItem("filled_tin_can", FilledTinCanItem::new);

    public static final DeferredItem<Item> COFFEE_BEANS = item("coffee_beans");
    public static final DeferredItem<Item> COFFEE_POWDER = item("coffee_powder");
    public static final DeferredItem<Item> STONE_MUG = item("stone_mug");
    public static final DeferredItem<Item> COFFEE_MUG_COLD = ITEMS.registerItem("coffee_mug_cold", props -> new CoffeeMugItem(CoffeeMugItem.Strength.COLD, props));
    public static final DeferredItem<Item> COFFEE_MUG_DARK = ITEMS.registerItem("coffee_mug_dark", props -> new CoffeeMugItem(CoffeeMugItem.Strength.DARK, props));
    public static final DeferredItem<Item> COFFEE_MUG = ITEMS.registerItem("coffee_mug", props -> new CoffeeMugItem(CoffeeMugItem.Strength.SWEET, props));

    public static final DeferredItem<Item> PLANTBALL = item("plantball");
    public static final DeferredItem<Item> COMPRESSED_PLANTS = item("compressed_plants");
    public static final DeferredItem<Item> HYDRATED_COAL_DUST = item("hydrated_coal_dust");
    public static final DeferredItem<Item> HYDRATED_COAL_CLUMP = item("hydrated_coal_clump");

    public static final DeferredItem<Item> EMPTY_CELL = item("empty_cell");
    /** Empty cell comes back from liquid crafting. Obsidian uses {@code ObsidianCellsRecipe} and keeps the cells. */
    public static final DeferredItem<Item> WATER_CELL = ITEMS.registerItem("water_cell",
        props -> new Item(props.craftRemainder(EMPTY_CELL.get())));
    public static final DeferredItem<Item> LAVA_CELL = item("lava_cell");
    public static final DeferredItem<Item> AIR_CELL = item("air_cell");
    public static final DeferredItem<Item> COAL_CELL = item("coal_cell");
    public static final DeferredItem<Item> BIO_CELL = item("bio_cell");
    public static final DeferredItem<Item> COALFUEL_CELL = item("coalfuel_cell");
    public static final DeferredItem<Item> BIOFUEL_CELL = item("biofuel_cell");
    public static final DeferredItem<Item> ELECTROLYZED_WATER_CELL = item("electrolyzed_water_cell");
    public static final DeferredItem<Item> HYDRATING_CELL = item("hydrating_cell");

    public static final DeferredItem<Item> FUEL_CAN = item("fuel_can");
    public static final DeferredItem<Item> FILLED_FUEL_CAN = item("filled_fuel_can");
    /** JAR reactorCoolantSimple (10k Coolant Cell). Fluid itemCoolant is D5. */
    public static final DeferredItem<Item> COOLANT_CELL = item("coolant_cell");
    public static final DeferredItem<Item> FERTILIZER = item("fertilizer");
    public static final DeferredItem<Item> WEED_EX = item("weed_ex");
    public static final DeferredItem<Item> GRIN_POWDER = item("grin_powder");
    public static final DeferredItem<Item> TERRA_WART = item("terra_wart");
    public static final DeferredItem<Item> HOPS = item("hops");
    public static final DeferredItem<Item> BARREL = item("barrel");
    public static final DeferredItem<Item> DEBUG_ITEM = ITEMS.registerItem("debug_item", DebugItem::new);
    public static final DeferredItem<Item> DYNAMITE = ITEMS.registerItem("dynamite", props -> new DynamiteItem(props, false));
    public static final DeferredItem<Item> STICKY_DYNAMITE = ITEMS.registerItem("sticky_dynamite", props -> new DynamiteItem(props, true));
    public static final DeferredItem<Item> REMOTE = ITEMS.registerItem("remote", RemoteItem::new);

    public static final DeferredItem<Item> WRENCH = ITEMS.registerItem("wrench", props -> new WrenchItem(props.stacksTo(1).durability(160)));
    public static final DeferredItem<Item> CUTTER = ITEMS.registerItem("cutter", props -> new CutterItem(props.stacksTo(1).durability(60)));
    public static final DeferredItem<Item> EU_READER = ITEMS.registerItem("eu_reader", props -> new EuReaderItem(props.stacksTo(1)));
    public static final DeferredItem<Item> SU_BATTERY = ITEMS.registerItem("su_battery", SuBatteryItem::new);
    public static final DeferredItem<Item> RE_BATTERY = ITEMS.registerItem("re_battery", props -> new ReBatteryItem(new ElectricCharge.Spec(10000, 100, 1, true), props));
    public static final DeferredItem<Item> ENERGY_CRYSTAL = ITEMS.registerItem("energy_crystal", props -> new ElectricItem(new ElectricCharge.Spec(100000, 250, 2, true), props));
    public static final DeferredItem<Item> LAPOTRON_CRYSTAL = ITEMS.registerItem("lapotron_crystal", props -> new ElectricItem(new ElectricCharge.Spec(1000000, 1000, 3, true), props));
    public static final DeferredItem<Item> OVERCLOCKER_UPGRADE = ITEMS.registerItem("overclocker_upgrade", props -> new UpgradeItem(UpgradeItem.Kind.OVERCLOCKER, props));
    public static final DeferredItem<Item> TRANSFORMER_UPGRADE = ITEMS.registerItem("transformer_upgrade", props -> new UpgradeItem(UpgradeItem.Kind.TRANSFORMER, props));
    public static final DeferredItem<Item> ENERGY_STORAGE_UPGRADE = ITEMS.registerItem("energy_storage_upgrade", props -> new UpgradeItem(UpgradeItem.Kind.STORAGE, props));
    public static final DeferredItem<Item> EJECTOR_UPGRADE = ITEMS.registerItem("ejector_upgrade", props -> new UpgradeItem(UpgradeItem.Kind.EJECTOR, props));
    public static final DeferredItem<Item> ELECTRIC_WRENCH = ITEMS.registerItem("electric_wrench", ElectricWrenchItem::new);
    public static final DeferredItem<Item> ELECTRIC_TREETAP = ITEMS.registerItem("electric_treetap", ElectricTreetapItem::new);
    // Speed desvio 10/20; EU JAR 50/80; buf/transfer JAR 10k/100.
    public static final DeferredItem<Item> MINING_DRILL = ITEMS.registerItem("mining_drill", props -> new ElectricToolItem(
        new ElectricCharge.Spec(10000, 100, 1, true), DrillStats.MINING_EU, DrillStats.MINING_SPEED,
        DrillStats.properties(props, OldustryMaterials.MINING_DRILL, 1, -2.8f).enchantable(DrillStats.MINING_ENCHANT)));
    public static final DeferredItem<Item> DIAMOND_DRILL = ITEMS.registerItem("diamond_drill", props -> new ElectricToolItem(
        new ElectricCharge.Spec(10000, 100, 1, true), DrillStats.DIAMOND_EU, DrillStats.DIAMOND_SPEED,
        DrillStats.properties(props, OldustryMaterials.DIAMOND_DRILL, 1, -2.8f).enchantable(DrillStats.DIAMOND_ENCHANT)));
    // Speed desvio 15 (JAR 12); EU JAR 100; axe + shears (JAR IShearable / leaves).
    public static final DeferredItem<Item> CHAINSAW = ITEMS.registerItem("chainsaw", props -> new ChainsawItem(
        new ElectricCharge.Spec(10000, 100, 1, true),
        ChainsawStats.properties(props, OldustryMaterials.CHAINSAW)));
    public static final DeferredItem<Item> ELECTRIC_HOE = ITEMS.registerItem("electric_hoe", props -> new ElectricToolItem(new ElectricCharge.Spec(10000, 100, 1, true), 50, props.hoe(OldustryMaterials.BRONZE, -2, -1f)));
    public static final DeferredItem<Item> OD_SCANNER = ITEMS.registerItem("od_scanner", props -> new ScannerItem(new ElectricCharge.Spec(10000, 50, 1, true), false, props));
    public static final DeferredItem<Item> OV_SCANNER = ITEMS.registerItem("ov_scanner", props -> new ScannerItem(new ElectricCharge.Spec(10000, 50, 2, true), true, props));
    public static final DeferredItem<Item> BATPACK = ITEMS.registerItem("batpack", props -> new ElectricArmorItem(new ElectricCharge.Spec(60000, 100, 1, true), OldustryMaterials.utilityArmor(props, OldustryMaterials.BATPACK_EQUIPMENT, ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> LAPPACK = ITEMS.registerItem("lappack", props -> new ElectricArmorItem(new ElectricCharge.Spec(300000, 250, 2, true), OldustryMaterials.utilityArmor(props, OldustryMaterials.LAPPACK_EQUIPMENT, ArmorType.CHESTPLATE)));
    // JAR ItemArmorUtility: 0 defense / no enchantable component.
    public static final DeferredItem<Item> SOLAR_HELMET = ITEMS.registerItem("solar_helmet", props -> new SolarHelmetItem(OldustryMaterials.utilityArmor(props, OldustryMaterials.SOLAR_EQUIPMENT, ArmorType.HELMET)));
    public static final DeferredItem<Item> STATIC_BOOTS = ITEMS.registerItem("static_boots", props -> new StaticBootsItem(props.humanoidArmor(OldustryMaterials.pack(OldustryMaterials.RUBBER_EQUIPMENT), ArmorType.BOOTS)));
    // Tank = FUEL component (desvio). No armor durability: JAR damageArmor is empty.
    public static final DeferredItem<Item> FUEL_JETPACK = ITEMS.registerItem("fuel_jetpack", props -> new FuelJetpackItem(OldustryMaterials.utilityEquip(
        props.stacksTo(1).component(ModDataComponents.FUEL.get(), 0),
        OldustryMaterials.JETPACK_EQUIPMENT,
        ArmorType.CHESTPLATE)));

    private static DeferredItem<Item> item(String name) {
        return ITEMS.registerSimpleItem(name);
    }

    private static DeferredItem<Item> coloredPainter(String name) {
        DyeColor color = DyeColor.byName(name.substring(0, name.length() - "_painter".length()), DyeColor.WHITE);
        return ITEMS.registerItem(name, props -> new PainterItem(color, props.stacksTo(1).durability(32)));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {}
}
