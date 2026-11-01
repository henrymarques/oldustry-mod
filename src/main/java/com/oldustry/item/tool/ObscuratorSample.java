package com.oldustry.item.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Copied face. JAR NBT {@code referencedBlockId} / {@code referencedMeta} / {@code referencedSide}. */
public record ObscuratorSample(BlockState state, Direction face) {
    public static final Codec<ObscuratorSample> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockState.CODEC.fieldOf("state").forGetter(ObscuratorSample::state),
        Direction.CODEC.fieldOf("face").forGetter(ObscuratorSample::face)
    ).apply(instance, ObscuratorSample::new));

    public static final StreamCodec<ByteBuf, ObscuratorSample> STREAM = StreamCodec.composite(
        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), ObscuratorSample::state,
        Direction.STREAM_CODEC, ObscuratorSample::face,
        ObscuratorSample::new
    );

    public boolean sameBlock(ObscuratorSample other) {
        return other != null && other.state.getBlock() == state.getBlock();
    }

    public boolean sameState(ObscuratorSample other) {
        return other != null && other.state.equals(state);
    }

    public boolean sameFace(ObscuratorSample other) {
        return other != null && other.face == face;
    }
}
