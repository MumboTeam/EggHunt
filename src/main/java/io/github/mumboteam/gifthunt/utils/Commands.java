package io.github.mumboteam.gifthunt.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.registry.ModItems;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Commands {
    public static LiteralArgumentBuilder<ServerCommandSource> registerCommands() {
        return CommandManager.literal("gifthunt")
                .requires(Permissions.require("gifthunt.commands", 4))
                .then(CommandManager.literal("payouts")
                        .executes(Commands::payouts))
                .then(CommandManager.literal("reset")
                        .executes(Commands::reset))
                .then(CommandManager.literal("clear_gifts")
                        .executes(Commands::clearGifts))
                .then(CommandManager.literal("admin")
                        .executes(Commands::admin))
                .then(CommandManager.literal("get_spawner")
                        .executes(Commands::getSpawner));
    }

    private static int payouts(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        long total = GiftHuntState.getServerState(source.getServer()).totalDiamonds;
        source.sendFeedback(() -> Text.translatable("text.gifthunt.owed_rewards", String.valueOf(total)), false);
        return 1;
    }

    private static int reset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        GiftHuntState state = GiftHuntState.getServerState(source.getServer());
        clearGifts(context);
        state.playerData.clear();
        source.sendFeedback(() -> Text.literal("Gift Hunt Reset!"), true);
        return 1;
    }

    private static int clearGifts(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        GiftHuntState state = GiftHuntState.getServerState(source.getServer());
        GiftHunt.LOGGER.info("Clearing gifts!");
        state.giftIds.forEach(uuid -> {
            Entity entity = source.getPlayer().getEntityWorld().getEntity(uuid);
            if (entity != null) {
                entity.discard();
            } else {
                GiftHunt.LOGGER.warn("Tried to discard entity with UUID {}, but it doesn't exist!", uuid);
            }
        });
        state.playerData.forEach((uuid, playerData) -> {
            playerData.resetFoundGifts();
        });
        state.giftIds.clear();
        state.markDirty();
        source.sendFeedback(() -> Text.literal("Removed all existing gifts"), true);
        return 1;
    }

    public static int admin(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() instanceof ServerPlayerEntity player) {
            GiftHuntState state = GiftHuntState.getServerState(source.getServer());
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

    public static int getSpawner(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() instanceof ServerPlayerEntity player) {
            player.giveItemStack(ModItems.GIFT_SPAWNER.getDefaultStack());
            return 1;
        }
        return 0;
    }
}
