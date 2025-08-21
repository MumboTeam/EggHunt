package io.github.mumboteam.flagparticipation.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.mumboteam.flagparticipation.FlagParticipation;
import io.github.mumboteam.flagparticipation.registry.ModItems;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

public class Commands {
    public static LiteralArgumentBuilder<ServerCommandSource> registerCommands() {
        return CommandManager.literal("flags")
                .requires(Permissions.require("flagparticipation.commands", 4))
                .then(CommandManager.literal("data")
                        .executes(Commands::data))
                .then(CommandManager.literal("reset")
                        .executes(Commands::reset))
                .then(CommandManager.literal("clear_flags")
                        .executes(Commands::clearFlags))
                .then(CommandManager.literal("admin")
                        .executes(Commands::admin))
                .then(CommandManager.literal("give")
                        .executes(Commands::give))
                ;
    }

    private static int data(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        FlagState state = FlagState.getServerState(source.getServer());

        source.sendFeedback(() -> Text.literal("  Player Participation\n-------------------"), false);

        for (UUID uuid : state.playerData.keySet()) {
            Optional<GameProfile> profile = source.getServer().getUserCache().getByUuid(uuid);
            String name = (profile.isPresent() ? profile.get().getName() : "Unknown Player");

            source.sendFeedback(() -> Text.literal(" " + name + ": " + state.playerData.get(uuid).getFlagCount()), false);
        }
        return 1;
    }

    private static int reset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        FlagState state = FlagState.getServerState(source.getServer());
        clearFlags(context);
        state.playerData.clear();
        source.sendFeedback(() -> Text.literal("Flags Reset!"), true);
        return 1;
    }

    private static int clearFlags(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        FlagState state = FlagState.getServerState(source.getServer());
        FlagParticipation.LOGGER.info("Clearing Flags!");
        state.flagsIds.forEach(uuid -> {
            Entity entity = source.getPlayer().getWorld().getEntity(uuid);
            if (entity != null) {
                entity.discard();
            } else {
                FlagParticipation.LOGGER.warn("Tried to discard entity with UUID {}, but it doesn't exist!", uuid);
            }
        });
        state.playerData.forEach((uuid, playerData) -> {
            playerData.resetFoundFlags();
        });
        state.flagsIds.clear();
        state.markDirty();
        source.sendFeedback(() -> Text.literal("Removed all existing Flags"), true);
        return 1;
    }

    private static int admin(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() instanceof ServerPlayerEntity player) {
            FlagState state = FlagState.getServerState(source.getServer());
            PlayerData data = state.playerData.get(player.getUuid());
            if (data != null) {
                data.toggleAdmin();
                source.sendFeedback(() -> Text.literal("Toggled admin mode " + (data.isAdmin() ? "on" : "off")), false);
            } else {
                PlayerData newData = new PlayerData();
                newData.toggleAdmin();
                state.playerData.put(player.getUuid(), new PlayerData());
                source.sendFeedback(() -> Text.literal("Toggled admin mode on"), false);
            }

            state.markDirty();
            return 1;
        } else {
            return 0;
        }
    }

    private static int give(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity user = source.getPlayer();

        if (user != null) {
            ItemEntity item = new ItemEntity(user.getWorld(), user.getX(), user.getY() + 1, user.getZ(), new ItemStack(ModItems.FLAG_SPAWNER));
            user.getWorld().spawnEntity(item);
            return 1;
        } else {
            return 0;
        }
    }
}
