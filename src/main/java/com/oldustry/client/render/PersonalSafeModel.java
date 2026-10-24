package com.oldustry.client.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/** Techne safe from {@code ModelPersonalChest}. 64×64, every cube mirrored. The door swings on Y. */
public class PersonalSafeModel extends Model<Float> {
    private static final float HALF = (float) (Math.PI / 2.0);
    private static final float TURN = (float) Math.PI;
    private final ModelPart door;

    public PersonalSafeModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.door = root.getChild("door");
    }

    public static PersonalSafeModel create() {
        return new PersonalSafeModel(layer().bakeRoot());
    }

    public static LayerDefinition layer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();
        root.addOrReplaceChild("wall_r", cube(0, 0, 0, 0, 0, 14, 16, 1), PartPose.offsetAndRotation(1, 0, 15, 0, HALF, 0));
        root.addOrReplaceChild("wall_l", cube(0, 0, 0, 0, 0, 14, 16, 1), PartPose.offsetAndRotation(15, 0, 1, 0, -HALF, 0));
        root.addOrReplaceChild("wall_b", cube(1, 1, 1, 1, 0, 12, 14, 1), PartPose.offsetAndRotation(15, 0, 15, 0, TURN, 0));
        root.addOrReplaceChild("wall_u", cube(1, 17, 1, 0, 0, 12, 14, 1), PartPose.offsetAndRotation(1, 0, 15, -HALF, 0, 0));
        root.addOrReplaceChild("wall_d", cube(1, 17, 1, 0, 0, 12, 14, 1), PartPose.offsetAndRotation(15, 15, 1, -HALF, TURN, 0));
        root.addOrReplaceChild("door", cube(30, 0, 0, 0, 0, 12, 14, 1), PartPose.offset(2, 1, 2));
        return LayerDefinition.create(mesh, 64, 64);
    }

    private static CubeListBuilder cube(int u, int v, float x, float y, float z, int width, int height, int depth) {
        return CubeListBuilder.create().texOffs(u, v).mirror().addBox(x, y, z, width, height, depth);
    }

    @Override
    public void setupAnim(Float open) {
        super.setupAnim(open);
        door.yRot = open * HALF;
    }
}
