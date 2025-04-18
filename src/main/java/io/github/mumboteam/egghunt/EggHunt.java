package io.github.mumboteam.egghunt;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.mumboteam.egghunt.registry.ModBlocks;
import io.github.mumboteam.egghunt.registry.ModEntityTypes;
import io.github.mumboteam.egghunt.registry.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EggHunt implements ModInitializer {
    public static final String ID = "egghunt";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        ModEntityTypes.initialize();
        PolymerResourcePackUtils.addModAssets(ID);
    }
}