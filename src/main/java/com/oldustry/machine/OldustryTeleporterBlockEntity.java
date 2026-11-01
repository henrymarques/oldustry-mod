package com.oldustry.machine;

import java.util.ArrayList;
import java.util.List;

import com.oldustry.Oldustry;
import com.oldustry.OldustryConfig;
import com.oldustry.registry.OldustrySounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** JAR {@code TileEntityTeleporter}. No EU buffer and no GUI. EU comes from adjacent storage. */
public final class OldustryTeleporterBlockEntity extends OldustryMachineBlockEntity {
    private static final DustParticleOptions BLUE = new DustParticleOptions(0x0000FF, 1.0F);
    private static final EquipmentSlot[] ARMOR = {
        EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private boolean targetSet;
    private int targetX;
    private int targetY;
    private int targetZ;

    OldustryTeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(OldustryMachineKind.TELEPORTER, pos, state);
    }

    public boolean linked() {
        return targetSet;
    }

    public int targetX() {
        return targetX;
    }

    public int targetY() {
        return targetY;
    }

    public int targetZ() {
        return targetZ;
    }

    public void setTarget(int x, int y, int z) {
        targetSet = true;
        targetX = x;
        targetY = y;
        targetZ = z;
        setChanged();
    }

    /** Redstone and a live target light the coil. The closest free entity is sent when the storages can pay. */
    boolean tickTeleporter(ServerLevel level, BlockPos pos) {
        if (!level.hasNeighborSignal(pos) || !targetSet) {
            return false;
        }
        if (!level.getBlockState(new BlockPos(targetX, targetY, targetZ)).is(com.oldustry.registry.OldustryBlocks.TELEPORTER.get())) {
            targetSet = false;
            setChanged();
            return false;
        }
        Entity closest = null;
        double best = Double.MAX_VALUE;
        double ox = pos.getX();
        double oy = pos.getY() + 1.0;
        double oz = pos.getZ();
        for (Entity entity : level.getEntitiesOfClass(Entity.class, OldustryTeleporter.pickupBox(pos))) {
            if (entity.getVehicle() != null) {
                continue;
            }
            double dx = ox - entity.getX();
            double dy = oy - entity.getY();
            double dz = oz - entity.getZ();
            double dist = dx * dx + dy * dy + dz * dz;
            if (dist < best) {
                best = dist;
                closest = entity;
            }
        }
        if (closest != null) {
            teleport(level, pos, closest);
        }
        return true;
    }

    private void teleport(ServerLevel level, BlockPos pos, Entity user) {
        int weight = weightOf(user);
        if (weight == 0) {
            return;
        }
        double distance = OldustryTeleporter.distance(pos.getX(), pos.getY(), pos.getZ(), targetX, targetY, targetZ);
        int cost = OldustryTeleporter.cost(weight, distance);
        List<OldustryStorageBlockEntity> sources = storages(level, pos);
        int[] stored = new int[sources.size()];
        for (int i = 0; i < sources.size(); i++) {
            stored[i] = sources.get(i).stored();
        }
        if (!OldustryTeleporter.draw(cost, stored)) {
            return;
        }
        for (int i = 0; i < sources.size(); i++) {
            sources.get(i).setStored(stored[i]);
        }
        double x = targetX + 0.5;
        double y = OldustryTeleporter.arriveY(targetY);
        double z = targetZ + 0.5;
        if (user instanceof ServerPlayer player) {
            player.teleportTo(x, y, z);
        } else {
            user.teleportTo(x, y, z);
        }
        burst(level, pos.getX(), pos.getY(), pos.getZ());
        burst(level, targetX, targetY, targetZ);
        OldustrySounds.play(level, null, pos, OldustrySounds.TELEPORTER_USE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        OldustrySounds.play(level, null, new BlockPos(targetX, targetY, targetZ), OldustrySounds.TELEPORTER_USE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        if (user instanceof ServerPlayer player && OldustryTeleporter.far(distance)) {
            var holder = level.getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "teleport_far_away"));
            if (holder != null) {
                player.getAdvancements().award(holder, "far");
            }
        }
    }

    private static List<OldustryStorageBlockEntity> storages(ServerLevel level, BlockPos pos) {
        List<OldustryStorageBlockEntity> sources = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof OldustryStorageBlockEntity storage && storage.stored() > 0) {
                sources.add(storage);
            }
        }
        return sources;
    }

    private static int weightOf(Entity user) {
        int weight = 0;
        Entity current = user;
        boolean inventory = OldustryConfig.TELEPORTER_INVENTORY.get();
        while (current != null) {
            boolean mount = current instanceof Animal || current instanceof AbstractMinecart || current instanceof AbstractBoat;
            weight += OldustryTeleporter.bodyWeight(
                mount,
                current instanceof Player,
                current instanceof Ghast,
                current instanceof EnderDragon,
                current instanceof PathfinderMob
            );
            weight += carried(current, inventory);
            var passengers = current.getPassengers();
            current = passengers.isEmpty() ? null : passengers.get(0);
        }
        return weight;
    }

    private static int carried(Entity entity, boolean inventory) {
        if (entity instanceof ItemEntity item) {
            ItemStack stack = item.getItem();
            return OldustryTeleporter.stackWeight(stack.getCount(), stack.getMaxStackSize());
        }
        if (!inventory || !(entity instanceof LivingEntity living)) {
            return 0;
        }
        int weight = 0;
        if (living instanceof Player player) {
            for (int i = 0; i < 36; i++) {
                weight += stackWeight(player.getInventory().getItem(i));
            }
        } else {
            weight += stackWeight(living.getMainHandItem());
        }
        for (EquipmentSlot slot : ARMOR) {
            weight += stackWeight(living.getItemBySlot(slot));
        }
        return weight;
    }

    private static int stackWeight(ItemStack stack) {
        return stack.isEmpty() ? 0 : OldustryTeleporter.stackWeight(stack.getCount(), stack.getMaxStackSize());
    }

    static void idleParticles(Level level, BlockPos pos) {
        var random = level.getRandom();
        for (int i = 0; i < 2; i++) {
            level.addParticle(BLUE, pos.getX() + random.nextFloat(), pos.getY() + 1 + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
            level.addParticle(BLUE, pos.getX() + random.nextFloat(), pos.getY() + 2 + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
    }

    private static void burst(ServerLevel level, int x, int y, int z) {
        var random = level.getRandom();
        for (int i = 0; i < 20; i++) {
            level.sendParticles(BLUE, x + random.nextFloat(), y + 1 + random.nextFloat(), z + random.nextFloat(), 1, 0, 0, 0, 0);
            level.sendParticles(BLUE, x + random.nextFloat(), y + 2 + random.nextFloat(), z + random.nextFloat(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("targetSet", targetSet);
        output.putInt("targetX", targetX);
        output.putInt("targetY", targetY);
        output.putInt("targetZ", targetZ);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        targetSet = input.getBooleanOr("targetSet", false);
        targetX = input.getIntOr("targetX", 0);
        targetY = input.getIntOr("targetY", 0);
        targetZ = input.getIntOr("targetZ", 0);
    }
}
