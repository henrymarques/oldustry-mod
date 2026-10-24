package com.oldustry.registry;

import com.oldustry.Oldustry;
import com.oldustry.block.CableBlockEntity;
import com.oldustry.block.PersonalSafeBlockEntity;
import com.oldustry.furnace.ImprovedFurnaceBlockEntity;
import com.oldustry.machine.OldustryMachineBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OldustryBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Oldustry.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ImprovedFurnaceBlockEntity>> IMPROVED_FURNACE =
        BLOCK_ENTITIES.register("improved_furnace", () -> new BlockEntityType<>(
            ImprovedFurnaceBlockEntity::new,
            java.util.Set.of(OldustryBlocks.IMPROVED_FURNACE.get())
        ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldustryMachineBlockEntity>> MACHINE =
        BLOCK_ENTITIES.register("machine", () -> new BlockEntityType<>(
            OldustryMachineBlockEntity::create,
            OldustryBlocks.machines()
        ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PersonalSafeBlockEntity>> PERSONAL_SAFE =
        BLOCK_ENTITIES.register("personal_safe", () -> new BlockEntityType<>(
            PersonalSafeBlockEntity::new,
            java.util.Set.of(OldustryBlocks.PERSONAL_SAFE.get())
        ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> CABLE =
        BLOCK_ENTITIES.register("cable", () -> new BlockEntityType<>(
            CableBlockEntity::new,
            OldustryBlocks.cables()
        ));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    private OldustryBlockEntities() {}
}
