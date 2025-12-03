package io.github.mumboteam.egghunt.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.mumboteam.egghunt.EggHunt;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Commands {
    public static LiteralArgumentBuilder<ServerCommandSource> registerCommands() {
        return CommandManager.literal("egghunt")
                .requires(Permissions.require("egghunt.commands", 4))
                .then(CommandManager.literal("payouts")
                        .executes(Commands::payouts))
                .then(CommandManager.literal("reset")
                        .executes(Commands::reset))
                .then(CommandManager.literal("clear_eggs")
                        .executes(Commands::clearEggs))
                .then(CommandManager.literal("admin")
                        .executes(Commands::admin))
                ;
    }

    private static int payouts(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        long total = EggHuntState.getServerState(source.getServer()).totalDiamonds;
        source.sendFeedback(() -> Text.translatable("text.egghunt.owed_rewards", String.valueOf(total)), false);
        return 1;
    }

    private static int reset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        EggHuntState state = EggHuntState.getServerState(source.getServer());
        clearEggs(context);
        state.playerData.clear();
        source.sendFeedback(() -> Text.literal("Egg Hunt Reset!"), true);
        return 1;
    }

    private static int clearEggs(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        EggHuntState state = EggHuntState.getServerState(source.getServer());
        EggHunt.LOGGER.info("Clearing eggs!");
        state.eggIds.forEach(uuid -> {
            Entity entity = source.getPlayer().getEntityWorld().getEntity(uuid);
            if (entity != null) {
                entity.discard();
            } else {
                EggHunt.LOGGER.warn("Tried to discard entity with UUID {}, but it doesn't exist!", uuid);
            }
        });
        state.playerData.forEach((uuid, playerData) -> {
            playerData.resetFoundEggs();
        });
        state.eggIds.clear();
        state.markDirty();
        source.sendFeedback(() -> Text.literal("Removed all existing eggs"), true);
        return 1;
    }

    public static int admin(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() instanceof ServerPlayerEntity player) {
            EggHuntState state = EggHuntState.getServerState(source.getServer());
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
}
