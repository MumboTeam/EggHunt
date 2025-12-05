package io.github.mumboteam.gifthunt;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.mumboteam.gifthunt.registry.ModBlocks;
import io.github.mumboteam.gifthunt.registry.ModEntityTypes;
import io.github.mumboteam.gifthunt.registry.ModItems;
import io.github.mumboteam.gifthunt.utils.Commands;
import io.github.mumboteam.gifthunt.utils.TickScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GiftHunt implements ModInitializer {
    public static final String ID = "gifthunt";
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