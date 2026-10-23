package com.oldustry;

import com.oldustry.block.CableBlock;
import com.oldustry.client.JetpackLoopSound;
import com.oldustry.client.SaberFlicker;
import com.oldustry.client.PersonalSafeRenderer;
import com.oldustry.client.PersonalSafeScreen;
import com.oldustry.client.MachineLoopSound;
import com.oldustry.client.OldustryKeys;
import com.oldustry.compat.CuriosWearables;
import com.oldustry.furnace.ImprovedFurnaceScreen;
import com.oldustry.item.ElectricJetpackItem;
import com.oldustry.item.FuelJetpackItem;
import com.oldustry.machine.MachineScreen;
import com.oldustry.registry.ModBlockEntities;
import com.oldustry.registry.ModBlocks;
import com.oldustry.registry.ModEntities;
import com.oldustry.registry.ModMenus;

import net.minecraft.resources.Identifier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(value = Oldustry.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Oldustry.MOD_ID, value = Dist.CLIENT)
public class OldustryClient {
    public OldustryClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(OldustryClient::clientTick);
        // Pre: thrust before travel/gravity so hover clamp sticks (Post was one tick late).
        NeoForge.EVENT_BUS.addListener(OldustryClient::playerTickPre);
        Oldustry.clientMachineLoop = MachineLoopSound::sync;
        Oldustry.clientMachineLoopStop = MachineLoopSound::stopAt;
    }

    @SubscribeEvent
    static void registerSaberFlicker(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "saber_flicker"), SaberFlicker.CODEC);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Oldustry.LOGGER.info("Oldustry client setup");
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.IMPROVED_FURNACE.get(), ImprovedFurnaceScreen::new);
        event.register(ModMenus.MACHINE.get(), MachineScreen::new);
        event.register(ModMenus.PERSONAL_SAFE.get(), PersonalSafeScreen::new);
    }

    @SubscribeEvent
    static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DYNAMITE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.PRIMED_ITNT.get(), net.minecraft.client.renderer.entity.TntRenderer::new);
        event.registerEntityRenderer(ModEntities.PRIMED_NUKE.get(), net.minecraft.client.renderer.entity.TntRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PERSONAL_SAFE.get(), PersonalSafeRenderer::new);
    }

    @SubscribeEvent
    static void specialModels(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "personal_safe"), PersonalSafeRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    static void keys(RegisterKeyMappingsEvent event) {
        event.registerCategory(OldustryKeys.CATEGORY);
        event.register(OldustryKeys.MODE);
        event.register(OldustryKeys.ALT);
        event.register(OldustryKeys.BOOST);
    }

    @SubscribeEvent
    static void colors(RegisterColorHandlersEvent.BlockTintSources event) {
        Block[] cables = ModBlocks.cables().toArray(Block[]::new);
        event.register(java.util.List.of(new CableTint()), cables);
        event.register(java.util.List.of(BlockTintSources.foliage()), ModBlocks.RUBBER_LEAVES.get());
    }

    static void clientTick(ClientTickEvent.Post event) {
        OldustryKeys.tick();
        JetpackLoopSound.tickCleanup();
        MachineLoopSound.tickCleanup();
    }

    static void playerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() || !player.isLocalPlayer()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) {
            return;
        }
        ItemStack chest = CuriosWearables.wornJetpack(player);
        boolean thrusting = false;
        boolean jump = mc.options.keyJump.isDown();
        boolean forward = mc.options.keyUp.isDown();
        if (chest.getItem() instanceof FuelJetpackItem) {
            thrusting = FuelJetpackItem.tickFlight(player, chest, jump, forward, false);
        } else if (chest.getItem() instanceof ElectricJetpackItem) {
            thrusting = ElectricJetpackItem.tickFlight(player, chest, jump, forward, false);
        }
        JetpackLoopSound.sync(thrusting);
    }

    private record CableTint() implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            if (!state.hasProperty(CableBlock.COLOR)) {
                return -1;
            }
            int c = state.getValue(CableBlock.COLOR);
            if (c <= 0) {
                boolean foamHard = state.hasProperty(CableBlock.FOAM) && state.getValue(CableBlock.FOAM) == 2;
                return foamHard ? DyeColor.LIGHT_GRAY.getTextureDiffuseColor() : 0xFFFFFFFF;
            }
            return DyeColor.byId(c - 1).getTextureDiffuseColor();
        }

        @Override
        public java.util.Set<Property<?>> relevantProperties() {
            return java.util.Set.of(CableBlock.COLOR, CableBlock.FOAM);
        }
    }
}
