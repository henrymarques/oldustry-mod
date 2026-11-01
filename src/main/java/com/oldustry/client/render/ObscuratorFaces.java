package com.oldustry.client.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oldustry.item.tool.ObscuratorSample;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/** Face sprite from the copied block, drawn on hardened foam and on the item. */
public final class ObscuratorFaces {
    private static final RenderType BLOCK_LAYER = RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS);
    /** Same shader as the flat icon. Entity cutout lights the back face, and this quad faces the other way. */
    private static final RenderType ITEM_LAYER = RenderTypes.itemCutout(TextureAtlas.LOCATION_BLOCKS);

    private ObscuratorFaces() {}

    private record Layer(TextureAtlasSprite sprite, int tint) {}

    public static void submitBlock(PoseStack poseStack, SubmitNodeCollector collector, Direction face, ObscuratorSample sample, BlockPos pos, int light) {
        List<Layer> layers = layers(sample);
        if (layers.isEmpty()) {
            return;
        }
        collector.submitCustomGeometry(poseStack, BLOCK_LAYER, (pose, buffer) -> {
            for (int i = 0; i < layers.size(); i++) {
                Layer layer = layers.get(i);
                blockFace(pose, buffer, face, layer.sprite, tint(sample.state(), layer.tint, pos), light, 0.002F * (i + 1));
            }
        });
    }

    public static void submitItem(PoseStack poseStack, SubmitNodeCollector collector, ObscuratorSample sample, int light) {
        List<Layer> layers = layers(sample);
        if (layers.isEmpty()) {
            return;
        }
        collector.submitCustomGeometry(poseStack, ITEM_LAYER, (pose, buffer) -> {
            for (int i = 0; i < layers.size(); i++) {
                Layer layer = layers.get(i);
                int color = tint(sample.state(), layer.tint, BlockPos.ZERO);
                // Generated item faces sit at 7.5/16 and 8.5/16. JAR inset is pixels 2..10.
                float lift = 0.001F * (i + 1);
                itemQuad(pose, buffer, layer.sprite, color, light, 8.5F / 16.0F + lift, 1.0F);
                itemQuad(pose, buffer, layer.sprite, color, light, 7.5F / 16.0F - lift, -1.0F);
            }
        });
    }

    private static List<Layer> layers(ObscuratorSample sample) {
        List<Layer> layers = new ArrayList<>();
        for (BakedQuad quad : quads(sample.state(), sample.face())) {
            TextureAtlasSprite sprite = quad.materialInfo().sprite();
            if (sprite != null) {
                layers.add(new Layer(sprite, quad.materialInfo().tintIndex()));
            }
        }
        if (layers.isEmpty()) {
            TextureAtlasSprite particle = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(sample.state()).sprite();
            if (particle != null) {
                layers.add(new Layer(particle, -1));
            }
        }
        return layers;
    }

    private static List<BakedQuad> quads(BlockState state, Direction face) {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(BlockAndTintGetter.EMPTY, BlockPos.ZERO, state, RandomSource.create(0), parts);
        List<BakedQuad> quads = new ArrayList<>();
        for (BlockStateModelPart part : parts) {
            quads.addAll(part.getQuads(face));
        }
        return quads;
    }

    private static int tint(BlockState state, int tintIndex, BlockPos pos) {
        if (tintIndex < 0) {
            return -1;
        }
        var source = Minecraft.getInstance().getBlockColors().getTintSource(state, tintIndex);
        if (source == null) {
            return -1;
        }
        var level = Minecraft.getInstance().level;
        int color = level == null ? source.color(state) : source.colorInWorld(state, level, pos);
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    private static void itemQuad(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, int color, int light, float z, float normalZ) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float x0 = 2.0F / 16.0F;
        float x1 = 10.0F / 16.0F;
        float y0 = 6.0F / 16.0F;
        float y1 = 14.0F / 16.0F;
        if (normalZ > 0.0F) {
            vertex(buffer, pose, x0, y0, z, u0, v1, color, light, 0.0F, 0.0F, normalZ);
            vertex(buffer, pose, x1, y0, z, u1, v1, color, light, 0.0F, 0.0F, normalZ);
            vertex(buffer, pose, x1, y1, z, u1, v0, color, light, 0.0F, 0.0F, normalZ);
            vertex(buffer, pose, x0, y1, z, u0, v0, color, light, 0.0F, 0.0F, normalZ);
        } else {
            vertex(buffer, pose, x0, y1, z, u0, v0, color, light, 0.0F, 0.0F, normalZ);
            vertex(buffer, pose, x1, y1, z, u1, v0, color, light, 0.0F, 0.0F, normalZ);
            vertex(buffer, pose, x1, y0, z, u1, v1, color, light, 0.0F, 0.0F, normalZ);
            vertex(buffer, pose, x0, y0, z, u0, v1, color, light, 0.0F, 0.0F, normalZ);
        }
    }

    private static void blockFace(PoseStack.Pose pose, VertexConsumer buffer, Direction face, TextureAtlasSprite sprite, int color, int light, float out) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float n = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F + out : -out;
        switch (face) {
            case DOWN -> {
                vertex(buffer, pose, 0, n, 1, u0, v0, color, light, 0, -1, 0);
                vertex(buffer, pose, 1, n, 1, u1, v0, color, light, 0, -1, 0);
                vertex(buffer, pose, 1, n, 0, u1, v1, color, light, 0, -1, 0);
                vertex(buffer, pose, 0, n, 0, u0, v1, color, light, 0, -1, 0);
            }
            case UP -> {
                vertex(buffer, pose, 0, n, 0, u0, v0, color, light, 0, 1, 0);
                vertex(buffer, pose, 1, n, 0, u1, v0, color, light, 0, 1, 0);
                vertex(buffer, pose, 1, n, 1, u1, v1, color, light, 0, 1, 0);
                vertex(buffer, pose, 0, n, 1, u0, v1, color, light, 0, 1, 0);
            }
            case NORTH -> {
                vertex(buffer, pose, 1, 1, n, u0, v0, color, light, 0, 0, -1);
                vertex(buffer, pose, 0, 1, n, u1, v0, color, light, 0, 0, -1);
                vertex(buffer, pose, 0, 0, n, u1, v1, color, light, 0, 0, -1);
                vertex(buffer, pose, 1, 0, n, u0, v1, color, light, 0, 0, -1);
            }
            case SOUTH -> {
                vertex(buffer, pose, 0, 1, n, u0, v0, color, light, 0, 0, 1);
                vertex(buffer, pose, 1, 1, n, u1, v0, color, light, 0, 0, 1);
                vertex(buffer, pose, 1, 0, n, u1, v1, color, light, 0, 0, 1);
                vertex(buffer, pose, 0, 0, n, u0, v1, color, light, 0, 0, 1);
            }
            case WEST -> {
                vertex(buffer, pose, n, 1, 0, u0, v0, color, light, -1, 0, 0);
                vertex(buffer, pose, n, 1, 1, u1, v0, color, light, -1, 0, 0);
                vertex(buffer, pose, n, 0, 1, u1, v1, color, light, -1, 0, 0);
                vertex(buffer, pose, n, 0, 0, u0, v1, color, light, -1, 0, 0);
            }
            case EAST -> {
                vertex(buffer, pose, n, 1, 1, u0, v0, color, light, 1, 0, 0);
                vertex(buffer, pose, n, 1, 0, u1, v0, color, light, 1, 0, 0);
                vertex(buffer, pose, n, 0, 0, u1, v1, color, light, 1, 0, 0);
                vertex(buffer, pose, n, 0, 1, u0, v1, color, light, 1, 0, 0);
            }
        }
    }

    private static void vertex(
        VertexConsumer buffer, PoseStack.Pose pose,
        float x, float y, float z, float u, float v, int color, int light, float nx, float ny, float nz
    ) {
        buffer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
    }
}
