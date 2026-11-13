package com.oldustry.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Packed booze state for the mug and the portable barrel item.
 * Bit layout matches JAR {@code ItemBooze} meta / {@code TileEntityBarrel.calculateMetaValue}.
 */
public record BoozeContents(int type, int amount, int solidRatio, int hopsRatio, int timeRatio, int rumProgress) {
    public static final BoozeContents EMPTY = new BoozeContents(0, 0, 0, 0, 0, 0);

    public static final Codec<BoozeContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("type").forGetter(BoozeContents::type),
        Codec.INT.fieldOf("amount").forGetter(BoozeContents::amount),
        Codec.INT.fieldOf("solid").forGetter(BoozeContents::solidRatio),
        Codec.INT.fieldOf("hops").forGetter(BoozeContents::hopsRatio),
        Codec.INT.fieldOf("time").forGetter(BoozeContents::timeRatio),
        Codec.INT.fieldOf("rum").forGetter(BoozeContents::rumProgress)
    ).apply(instance, BoozeContents::new));

    public static final StreamCodec<ByteBuf, BoozeContents> STREAM = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, BoozeContents::type,
        ByteBufCodecs.VAR_INT, BoozeContents::amount,
        ByteBufCodecs.VAR_INT, BoozeContents::solidRatio,
        ByteBufCodecs.VAR_INT, BoozeContents::hopsRatio,
        ByteBufCodecs.VAR_INT, BoozeContents::timeRatio,
        ByteBufCodecs.VAR_INT, BoozeContents::rumProgress,
        BoozeContents::new
    );

    public boolean isEmpty() {
        return type == 0 || amount <= 0;
    }

    public static BoozeContents beer(int amount, int solid, int hops, int time) {
        return new BoozeContents(1, Math.max(0, amount), solid, hops, time, 0);
    }

    public static BoozeContents rum(int amount, int progress) {
        return new BoozeContents(2, Math.max(0, amount), 0, 0, 0, Math.clamp(progress, 0, 100));
    }

    /** JAR {@code TileEntityBarrel.calculateMetaValue}. */
    public int pack() {
        if (isEmpty()) {
            return 0;
        }
        if (type == 1) {
            int value = 0;
            value |= timeRatio & 7;
            value <<= 3;
            value |= hopsRatio & 7;
            value <<= 3;
            value |= solidRatio & 7;
            value <<= 5;
            value |= (amount - 1) & 31;
            value <<= 2;
            value |= type & 3;
            return value;
        }
        if (type == 2) {
            int value = 0;
            value |= rumProgress & 127;
            value <<= 5;
            value |= (amount - 1) & 31;
            value <<= 2;
            value |= type & 3;
            return value;
        }
        return 0;
    }

    /** JAR {@code ItemBooze.get*OfValue} helpers. */
    public static BoozeContents unpack(int value) {
        int type = bits(value, 0, 2);
        if (type == 0) {
            return EMPTY;
        }
        int amount = bits(value, 2, 5) + 1;
        if (type == 1) {
            return beer(amount, bits(value, 7, 3), bits(value, 10, 3), bits(value, 13, 3));
        }
        if (type == 2) {
            return rum(amount, bits(value, 7, 7));
        }
        return EMPTY;
    }

    private static int bits(int value, int shift, int take) {
        return (value >> shift) & ((1 << take) - 1);
    }
}
