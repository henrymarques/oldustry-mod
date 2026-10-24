package com.oldustry.compat.jade;

import org.jspecify.annotations.NonNull;

import com.oldustry.Oldustry;
import com.oldustry.block.MachineBlock;
import com.oldustry.machine.OldustryMachineBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(Oldustry.MOD_ID)
public final class OldustryJadePlugin implements IWailaPlugin {
    @Override
    public void register(@NonNull IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MachineEnergyProvider.INSTANCE, OldustryMachineBlockEntity.class);
    }

    @Override
    public void registerClient(@NonNull IWailaClientRegistration registration) {
        registration.registerBlockComponent(MachineEnergyComponentProvider.INSTANCE, MachineBlock.class);
    }
}
