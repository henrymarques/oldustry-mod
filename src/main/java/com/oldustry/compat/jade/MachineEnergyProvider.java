package com.oldustry.compat.jade;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.oldustry.Oldustry;
import com.oldustry.machine.MachineBlockEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

/**
 * Server-only Jade sync: EU + process progress for machines.
 * Jade's default {@code appendServerData} disagrees with {@code IServerDataProvider} on nullness.
 */
@SuppressWarnings("null")
public enum MachineEnergyProvider implements StreamServerDataProvider<BlockAccessor, MachineEnergyProvider.Data> {
    INSTANCE;

    public static final @NonNull Identifier UID = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "machine_eu");

    public record Data(int energy, int capacity, int progress, int duration) {}

    private static final StreamCodec<RegistryFriendlyByteBuf, Data> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, Data::energy,
        ByteBufCodecs.VAR_INT, Data::capacity,
        ByteBufCodecs.VAR_INT, Data::progress,
        ByteBufCodecs.VAR_INT, Data::duration,
        Data::new
    );

    @Override
    public @Nullable Data streamData(BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof MachineBlockEntity machine)) {
            return null;
        }
        return new Data(
            machine.energy(),
            machine.energyCapacity(),
            machine.processProgress(),
            machine.processDuration()
        );
    }

    @Override
    public @NonNull StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return CODEC;
    }

    @Override
    public @NonNull Identifier getUid() {
        return UID;
    }
}
