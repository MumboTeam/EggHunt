package io.github.mumboteam.egghunt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.mumboteam.egghunt.registry.ModBlocks;
import io.github.mumboteam.egghunt.registry.ModEntityTypes;
import io.github.mumboteam.egghunt.registry.ModItems;
import io.github.mumboteam.egghunt.utils.RewardsState;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
        ModItems.initialize();
        ModBlocks.initialize();
        ModEntityTypes.initialize();
        PolymerResourcePackUtils.addModAssets(ID);


        CommandRegistrationCallback.EVENT.register((CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry_access, CommandManager.RegistrationEnvironment environment) ->
        {
            dispatcher.register(CommandManager.literal("egghunt")
                    .requires(Permissions.require("egghunt.commands", 4))
                    .executes((CommandContext<ServerCommandSource> context) ->
            {
                ServerCommandSource source = context.getSource();
                ServerPlayerEntity player = source.getPlayer();
                if (player == null) {
                    return 0;
                }
                source.sendFeedback(() -> Text.translatable("text.egghunt.owed_rewards", String.valueOf(RewardsState.getServerState(source.getWorld().getServer()).totalDiamonds)), false);

                return 1;
            }));
        });
    }
}