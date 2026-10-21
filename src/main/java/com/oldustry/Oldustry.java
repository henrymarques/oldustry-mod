package com.oldustry;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.oldustry.data.OldustryData;
import com.oldustry.item.CfSprayerChargeRecipe;
import com.oldustry.item.ScrapBoxItem;
import com.oldustry.network.KeysPayload;
import com.oldustry.registry.ModBlockEntities;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModDataComponents;
import com.oldustry.registry.ModEffects;
import com.oldustry.registry.ModEntities;
import com.oldustry.registry.ModCreativeTabs;
import com.oldustry.registry.ModFeatures;
import com.oldustry.registry.ModItems;
import com.oldustry.registry.ModMenus;
import com.oldustry.registry.ModSounds;

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
    public static java.util.function.BiConsumer<net.minecraft.core.BlockPos, com.oldustry.machine.MachineKind> clientMachineLoop =
        (pos, kind) -> {};
    /** Client fills this to stop a machine loop when ACTIVE clears or block is gone. */
    public static java.util.function.Consumer<net.minecraft.core.BlockPos> clientMachineLoopStop = pos -> {};

    public Oldustry(IEventBus modEventBus, ModContainer modContainer) {
        ModEffects.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenus.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        CfSprayerChargeRecipe.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, OldustryConfig.SPEC);
        modEventBus.addListener(OldustryData::gatherClient);
        modEventBus.addListener(Oldustry::setup);
        modEventBus.addListener(Oldustry::payloads);
        LOGGER.info("Oldustry loaded");
    }

    private static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.SCRAP_BOX.get(), new DefaultDispenseItemBehavior() {
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
