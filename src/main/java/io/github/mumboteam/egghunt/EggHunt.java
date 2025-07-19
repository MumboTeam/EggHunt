package io.github.mumboteam.egghunt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.mumboteam.egghunt.entity.EggEntity;
import io.github.mumboteam.egghunt.registry.ModBlocks;
import io.github.mumboteam.egghunt.registry.ModEntityTypes;
import io.github.mumboteam.egghunt.registry.ModItems;
import io.github.mumboteam.egghunt.utils.EggHuntState;
import io.github.mumboteam.egghunt.utils.TickScheduler;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import io.github.mumboteam.egghunt.utils.Commands;

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