package com.oldustry.registry;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oldustry.Oldustry;
import com.oldustry.item.explosive.RemoteLink;
import com.oldustry.item.tool.ObscuratorSample;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OldustryDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Oldustry.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = COMPONENTS.register(
        "energy",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    /** Remaining construction foam. Sprayer max 1600, pack max 260. Both crafted at 0. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FOAM = COMPONENTS.register(
        "foam",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    /** Heat sitting in a coolant cell, exchanger or vent. Absent means 0. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REACTOR_HEAT = COMPONENTS.register(
        "reactor_heat",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    /** Mining laser mode 0–6. Absent means Mining. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LASER_MODE = COMPONENTS.register(
        "laser_mode",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    /** Pulses left on a reflector, or heat swallowed by a condensator. Absent means 0. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REACTOR_WEAR = COMPONENTS.register(
        "reactor_wear",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FUEL = COMPONENTS.register(
        "fuel",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EJECTOR_DIR = COMPONENTS.register(
        "ejector_dir",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SABER_ACTIVE = COMPONENTS.register(
        "saber_active",
        () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HOVER = COMPONENTS.register(
        "hover",
        () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LOSSLESS = COMPONENTS.register(
        "lossless",
        () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> MEASURE_POS = COMPONENTS.register(
        "measure_pos",
        () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> FREQUENCY_TARGET = COMPONENTS.register(
        "frequency_target",
        () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FREQUENCY_LATCHED = COMPONENTS.register(
        "frequency_latched",
        () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MEASURE_TICK = COMPONENTS.register(
        "measure_tick",
        () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MEASURE_EMITTED = COMPONENTS.register(
        "measure_emitted",
        () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MEASURE_SUNKEN = COMPONENTS.register(
        "measure_sunken",
        () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStack>>> BOX = COMPONENTS.register(
        "box",
        () -> DataComponentType.<List<ItemStack>>builder()
            .persistent(ItemStack.OPTIONAL_CODEC.listOf())
            .networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()))
            .build()
    );

    private static final Codec<RemoteLink> LINK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("dimension").forGetter(RemoteLink::dimension),
        Codec.INT.fieldOf("x").forGetter(RemoteLink::x),
        Codec.INT.fieldOf("y").forGetter(RemoteLink::y),
        Codec.INT.fieldOf("z").forGetter(RemoteLink::z)
    ).apply(instance, RemoteLink::new));
    private static final StreamCodec<io.netty.buffer.ByteBuf, RemoteLink> LINK_STREAM = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, RemoteLink::dimension,
        ByteBufCodecs.VAR_INT, RemoteLink::x,
        ByteBufCodecs.VAR_INT, RemoteLink::y,
        ByteBufCodecs.VAR_INT, RemoteLink::z,
        RemoteLink::new
    );

    /** Dynamite-O-Mote links. Dimension id plus block, so the Nether does not share overworld coords. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<RemoteLink>>> REMOTE_LINKS = COMPONENTS.register(
        "remote_links",
        () -> DataComponentType.<List<RemoteLink>>builder()
            .persistent(LINK_CODEC.listOf())
            .networkSynchronized(LINK_STREAM.apply(ByteBufCodecs.list()))
            .build()
    );

    /** JAR ItemArmorStaticBoots NBT x/z walk anchors. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WALK_X = COMPONENTS.register(
        "walk_x",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WALK_Z = COMPONENTS.register(
        "walk_z",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    /** Obscurator copy. Block state plus the face whose texture was read. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ObscuratorSample>> OBSCURATOR = COMPONENTS.register(
        "obscurator",
        () -> DataComponentType.<ObscuratorSample>builder()
            .persistent(ObscuratorSample.CODEC)
            .networkSynchronized(ObscuratorSample.STREAM)
            .build()
    );

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }

    private OldustryDataComponents() {}
}
