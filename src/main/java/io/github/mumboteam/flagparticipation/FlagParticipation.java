package io.github.mumboteam.flagparticipation;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.mumboteam.flagparticipation.registry.ModEntityTypes;
import io.github.mumboteam.flagparticipation.registry.ModItems;
import io.github.mumboteam.flagparticipation.utils.TickScheduler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mumboteam.flagparticipation.utils.Commands;

public class FlagParticipation implements ModInitializer {
    public static final String ID = "flagparticipation";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
    public void onInitialize() {
        TickScheduler.init();
        ModItems.initialize();
        ModEntityTypes.initialize();
        PolymerResourcePackUtils.addModAssets(ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.registerCommands());
        });
    }
}