package com.oldustry.compat.jade;

import org.jspecify.annotations.Nullable;

import com.oldustry.Oldustry;
import com.oldustry.machine.MachineBlockEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

/** Server-only Jade sync: EU + process progress for machines. */
public enum MachineEnergyProvider implements StreamServerDataProvider<BlockAccessor, MachineEnergyProvider.Data> {
    INSTANCE;

    public static final Identifier UID = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "machine_eu");

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
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
