package com.oldustry.compat.jade;

import com.oldustry.Oldustry;
import com.oldustry.block.MachineBlock;
import com.oldustry.machine.MachineBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(Oldustry.MOD_ID)
public final class OldustryJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MachineEnergyProvider.INSTANCE, MachineBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MachineEnergyComponentProvider.INSTANCE, MachineBlock.class);
    }
}
