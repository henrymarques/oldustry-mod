package com.oldustry.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Bred seed. The JAR stored this in the item damage and NBT; here it is one component. */
public record CropSeed(int id, int growth, int gain, int resistance, int scan) {
    public static final Codec<CropSeed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("id").forGetter(CropSeed::id),
        Codec.INT.fieldOf("growth").forGetter(CropSeed::growth),
        Codec.INT.fieldOf("gain").forGetter(CropSeed::gain),
        Codec.INT.fieldOf("resistance").forGetter(CropSeed::resistance),
        Codec.INT.fieldOf("scan").forGetter(CropSeed::scan)
    ).apply(instance, CropSeed::new));

    public static final StreamCodec<ByteBuf, CropSeed> STREAM = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, CropSeed::id,
        ByteBufCodecs.VAR_INT, CropSeed::growth,
        ByteBufCodecs.VAR_INT, CropSeed::gain,
        ByteBufCodecs.VAR_INT, CropSeed::resistance,
        ByteBufCodecs.VAR_INT, CropSeed::scan,
        CropSeed::new
    );

    public CropSeed withScan(int next) {
        return new CropSeed(id, growth, gain, resistance, next);
    }
}
