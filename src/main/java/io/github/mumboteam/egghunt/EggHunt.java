package io.github.mumboteam.egghunt;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.mumboteam.egghunt.registry.ModBlocks;
import io.github.mumboteam.egghunt.registry.ModEntityTypes;
import io.github.mumboteam.egghunt.registry.ModItems;
import io.github.mumboteam.egghunt.utils.Commands;
import io.github.mumboteam.egghunt.utils.TickScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EggHunt implements ModInitializer {
    public static final String ID = "egghunt";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static final Set<UUID> dailyPlayerSubmissions =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void onInitialize() {
        TickScheduler.init();
        ModItems.initialize();
        ModBlocks.initialize();
        ModEntityTypes.initialize();
        PolymerResourcePackUtils.addModAssets(ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.registerCommands());
        });
    }
}