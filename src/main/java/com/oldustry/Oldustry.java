package com.oldustry;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.oldustry.data.OldustryData;
import com.oldustry.item.construction.CfSprayerChargeRecipe;
import com.oldustry.item.utility.ScrapBoxItem;
import com.oldustry.network.KeysPayload;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryDataComponents;
import com.oldustry.registry.OldustryEffects;
import com.oldustry.registry.OldustryEntities;
import com.oldustry.registry.OldustryCreativeTabs;
import com.oldustry.registry.OldustryFeatures;
import com.oldustry.registry.OldustryItems;
import com.oldustry.registry.OldustryMenus;
import com.oldustry.registry.OldustrySounds;

import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Oldustry.MOD_ID)
public class Oldustry {
    public static final String MOD_ID = "oldustry";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Client fills this to play stoppable machine loop sounds (JAR AudioSource). */
    public static java.util.function.BiConsumer<net.minecraft.core.BlockPos, com.oldustry.machine.OldustryMachineKind> clientMachineLoop =
        (pos, kind) -> {};
    /** Client fills this to stop a machine loop when ACTIVE clears or block is gone. */
    public static java.util.function.Consumer<net.minecraft.core.BlockPos> clientMachineLoopStop = pos -> {};

    public Oldustry(IEventBus modEventBus, ModContainer modContainer) {
        OldustryEffects.register(modEventBus);
        OldustryDataComponents.register(modEventBus);
        OldustryBlocks.register(modEventBus);
        OldustryItems.register(modEventBus);
        OldustryEntities.register(modEventBus);
        OldustryBlockEntities.register(modEventBus);
        OldustryMenus.register(modEventBus);
        OldustryFeatures.register(modEventBus);
        OldustrySounds.register(modEventBus);
        OldustryCreativeTabs.register(modEventBus);
        CfSprayerChargeRecipe.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, OldustryConfig.SPEC);
        modEventBus.addListener(OldustryData::gatherClient);
        modEventBus.addListener(Oldustry::setup);
        modEventBus.addListener(Oldustry::payloads);
        LOGGER.info("Oldustry loaded");
    }

    private static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(OldustryItems.SCRAP_BOX.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ItemStack drop = ScrapBoxItem.roll(source.level().getRandom());
                if (!drop.isEmpty()) {
                    spawnItem(
                        source.level(),
                        drop,
                        6,
                        source.state().getValue(DispenserBlock.FACING),
                        DispenserBlock.getDispensePosition(source)
                    );
                }
                stack.shrink(1);
                return stack;
            }
        }));
    }

    private static void payloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(KeysPayload.TYPE, KeysPayload.STREAM, KeysPayload::handle);
    }
}
