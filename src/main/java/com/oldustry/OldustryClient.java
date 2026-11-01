package com.oldustry;

import com.oldustry.block.CableBlock;
import com.oldustry.client.sound.JetpackLoopSound;
import com.oldustry.client.render.BatteryFill;
import com.oldustry.client.render.SaberFlicker;
import com.oldustry.client.render.ObscuratorRenderer;
import com.oldustry.client.render.MiningLaserRenderer;
import com.oldustry.client.render.OldustryBoatRenderer;
import com.oldustry.client.render.PersonalSafeRenderer;
import com.oldustry.client.screen.OldustryReactorScreen;
import com.oldustry.client.screen.PersonalSafeScreen;
import com.oldustry.client.sound.MachineLoopSound;
import com.oldustry.client.OldustryKeys;
import com.oldustry.compat.CuriosWearables;
import com.oldustry.furnace.ImprovedFurnaceScreen;
import com.oldustry.item.equipment.ElectricJetpackItem;
import com.oldustry.item.equipment.FuelJetpackItem;
import com.oldustry.item.equipment.QuantumSuitItem;
import com.oldustry.client.screen.OldustryMachineScreen;
import com.oldustry.registry.OldustryBlockEntities;
import com.oldustry.registry.OldustryBlocks;
import com.oldustry.registry.OldustryEntities;
import com.oldustry.registry.OldustryMenus;

import net.minecraft.resources.Identifier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(value = Oldustry.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Oldustry.MOD_ID, value = Dist.CLIENT)
public class OldustryClient {
    public OldustryClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(OldustryClient::clientTick);
        // Pre: thrust before travel/gravity so hover clamp sticks (Post was one tick late).
        NeoForge.EVENT_BUS.addListener(OldustryClient::playerTickPre);
        NeoForge.EVENT_BUS.addListener(OldustryClient::quantumJump);
        Oldustry.clientMachineLoop = MachineLoopSound::sync;
        Oldustry.clientMachineLoopStop = MachineLoopSound::stopAt;
    }

    @SubscribeEvent
    static void registerSaberFlicker(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "saber_flicker"), SaberFlicker.CODEC);
        event.register(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "battery_fill"), BatteryFill.CODEC);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Oldustry.LOGGER.info("Oldustry client setup");
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(OldustryMenus.IMPROVED_FURNACE.get(), ImprovedFurnaceScreen::new);
        event.register(OldustryMenus.MACHINE.get(), OldustryMachineScreen::create);
        event.register(OldustryMenus.PERSONAL_SAFE.get(), PersonalSafeScreen::new);
        event.register(OldustryMenus.NUCLEAR_REACTOR.get(), OldustryReactorScreen::new);
    }

    @SubscribeEvent
    static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OldustryEntities.MINING_LASER.get(), MiningLaserRenderer::new);
        event.registerEntityRenderer(OldustryEntities.DYNAMITE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(OldustryEntities.PRIMED_ITNT.get(), net.minecraft.client.renderer.entity.TntRenderer::new);
        event.registerEntityRenderer(OldustryEntities.PRIMED_NUKE.get(), net.minecraft.client.renderer.entity.TntRenderer::new);
        event.registerEntityRenderer(OldustryEntities.RUBBER_DINGHY.get(), context -> new OldustryBoatRenderer(context, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/entity/boat/rubber.png"), false));
        event.registerEntityRenderer(OldustryEntities.CARBON_BOAT.get(), context -> new OldustryBoatRenderer(context, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/entity/boat/carbon.png"), false));
        event.registerEntityRenderer(OldustryEntities.ELECTRIC_BOAT.get(), context -> new OldustryBoatRenderer(context, Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "textures/entity/boat/electric.png"), true));
        event.registerBlockEntityRenderer(OldustryBlockEntities.PERSONAL_SAFE.get(), PersonalSafeRenderer::new);
        event.registerBlockEntityRenderer(OldustryBlockEntities.CABLE.get(), ObscuratorRenderer::new);
    }

    @SubscribeEvent
    static void specialModels(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "personal_safe"), PersonalSafeRenderer.Unbaked.MAP_CODEC);
        event.register(ObscuratorRenderer.ID, ObscuratorRenderer.Unbaked.MAP_CODEC);
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
        Block[] cables = OldustryBlocks.cables().toArray(Block[]::new);
        event.register(java.util.List.of(new CableTint()), cables);
        event.register(java.util.List.of(BlockTintSources.foliage()), OldustryBlocks.RUBBER_LEAVES.get());
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
        boolean boost = OldustryKeys.BOOST.isDown();
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof QuantumSuitItem) {
            QuantumSuitItem.boost(player, legs, forward, jump, boost);
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof QuantumSuitItem) {
            QuantumSuitItem.hop(player, boots, jump, boost, !player.onGround());
        }
        JetpackLoopSound.sync(thrusting);
    }

    static void quantumJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player) || !player.isLocalPlayer()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof QuantumSuitItem) {
            QuantumSuitItem.hop(player, boots, mc.options.keyJump.isDown(), OldustryKeys.BOOST.isDown(), true);
        }
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
