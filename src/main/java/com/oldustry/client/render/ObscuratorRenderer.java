package com.oldustry.client.render;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.oldustry.Oldustry;
import com.oldustry.block.CableBlockEntity;
import com.oldustry.item.tool.ObscuratorSample;
import com.oldustry.registry.OldustryDataComponents;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/** Item overlay ({@code RenderObscurator}) and the painted face on a foamed cable. */
public final class ObscuratorRenderer implements BlockEntityRenderer<CableBlockEntity, ObscuratorRenderer.State> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "obscurator");

    public ObscuratorRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public boolean shouldRender(CableBlockEntity cable, Vec3 camera) {
        return cable.hasPaint() && BlockEntityRenderer.super.shouldRender(cable, camera);
    }

    @Override
    public void extractRenderState(
        CableBlockEntity cable,
        State state,
        float partialTick,
        Vec3 camera,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(cable, state, partialTick, camera, breakProgress);
        for (Direction face : Direction.values()) {
            state.faces[face.get3DDataValue()] = cable.face(face);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (Direction face : Direction.values()) {
            ObscuratorSample sample = state.faces[face.get3DDataValue()];
            if (sample == null) {
                continue;
            }
            ObscuratorFaces.submitBlock(poseStack, collector, face, sample, state.blockPos, state.lightCoords);
        }
    }

    public static final class State extends BlockEntityRenderState {
        private final ObscuratorSample[] faces = new ObscuratorSample[6];
    }

    /** Flat icon stays the item model. This draws the copied face inset, inventory and in hand. */
    public static final class Item implements SpecialModelRenderer<ObscuratorSample> {
        @Override
        public @Nullable ObscuratorSample extractArgument(ItemStack stack) {
            return stack.get(OldustryDataComponents.OBSCURATOR.get());
        }

        @Override
        public void submit(
            @Nullable ObscuratorSample sample,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            boolean foil,
            int outlineColor
        ) {
            if (sample == null) {
                return;
            }
            ObscuratorFaces.submitItem(poseStack, collector, sample, light);
        }

        @Override
        public void getExtents(Consumer<Vector3fc> output) {
            output.accept(new Vector3f(2.0F / 16.0F, 6.0F / 16.0F, 7.4F / 16.0F));
            output.accept(new Vector3f(10.0F / 16.0F, 14.0F / 16.0F, 8.6F / 16.0F));
        }
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<ObscuratorSample> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<ObscuratorSample> bake(SpecialModelRenderer.BakingContext context) {
            return new Item();
        }
    }
}
