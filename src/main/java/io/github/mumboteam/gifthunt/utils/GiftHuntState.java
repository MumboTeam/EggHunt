package io.github.mumboteam.gifthunt.utils;

import io.github.mumboteam.gifthunt.GiftHunt;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;

public class GiftHuntState extends PersistentState {
    public HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public HashSet<UUID> giftIds = new HashSet<>();
    public int totalDiamonds = 0;

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound players = new NbtCompound();
        playerData.forEach((uuid, data) -> {
            NbtCompound player = new NbtCompound();
            player.putInt("giftCount", data.getGiftCount());
            NbtList foundGifts = new NbtList();
            data.getFoundGifts().forEach(giftUuid -> {
                foundGifts.add(NbtString.of(giftUuid.toString()));
            });
            player.put("foundGifts", foundGifts);
            player.putBoolean("admin", data.isAdmin());
            players.put(uuid.toString(), player);
        });

        NbtList gifts = new NbtList();
        giftIds.forEach(uuid -> {
            gifts.add(NbtString.of(uuid.toString()));
        });

        nbt.put("gifts", gifts);
        nbt.put("players", players);
        nbt.putInt("totalDiamonds", totalDiamonds);
        return nbt;
    }

    public static GiftHuntState createFromNbt(NbtCompound tag) {
        GiftHuntState state = new GiftHuntState();
        NbtCompound players = tag.getCompoundOrEmpty("players");
        players.getKeys().forEach(uuid -> {
            NbtCompound player = players.getCompound(uuid).get();
            int giftCount = player.getInt("giftCount").orElse(0);
            Set<UUID> foundSet = new HashSet<>();
            NbtList foundGifts = player.getListOrEmpty("foundGifts");
            foundGifts.forEach(giftUuid -> {
                foundSet.add(UUID.fromString(giftUuid.toString().replace("\"", "")));
            });
            boolean admin = player.getBoolean("admin").orElse(false);

            state.playerData.put(UUID.fromString(uuid), new PlayerData(giftCount, foundSet, admin));
        });

        NbtList gifts = tag.getListOrEmpty("gifts");
        gifts.forEach(uuid -> {
            state.giftIds.add(UUID.fromString(uuid.toString().replace("\"", "")));
        });

        state.totalDiamonds = tag.getInt("totalDiamonds").orElse(0);
        return state;
    }

    private static final PersistentStateType<GiftHuntState> STATE_TYPE = new PersistentStateType<>(
            GiftHunt.ID,
            (context) -> new GiftHuntState(),
            context -> NbtCompound.CODEC.xmap(
                    GiftHuntState::createFromNbt,
                    state -> state.writeNbt(new NbtCompound())
            ),
            null
    );

    public static GiftHuntState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        GiftHuntState state = persistentStateManager.getOrCreate(STATE_TYPE);
        state.markDirty();

        return state;
    }
}
