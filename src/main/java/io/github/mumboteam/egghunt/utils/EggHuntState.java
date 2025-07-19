package io.github.mumboteam.egghunt.utils;

import io.github.mumboteam.egghunt.EggHunt;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EggHuntState extends PersistentState {
    public HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public HashSet<UUID> eggIds = new HashSet<>();
    public int totalDiamonds = 0;

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound players = new NbtCompound();
        playerData.forEach((uuid, data) -> {
            NbtCompound player = new NbtCompound();
            player.putInt("eggCount", data.getEggCount());
            NbtList foundEggs = new NbtList();
            data.getFoundEggs().forEach(eggUuid -> {
                foundEggs.add(NbtString.of(eggUuid.toString()));
            });
            player.put("foundEggs", foundEggs);
            player.putBoolean("admin", data.isAdmin());
            players.put(uuid.toString(), player);
        });

        NbtList eggs = new NbtList();
        eggIds.forEach(uuid -> {
            eggs.add(NbtString.of(uuid.toString()));
        });

        nbt.put("eggs", eggs);
        nbt.put("players", players);
        nbt.putInt("totalDiamonds", totalDiamonds);
        return nbt;
    }

    public static EggHuntState createFromNbt(NbtCompound tag) {
        EggHuntState state = new EggHuntState();
        NbtCompound players = tag.getCompoundOrEmpty("players");
        players.getKeys().forEach(uuid -> {
            NbtCompound player = players.getCompound(uuid).get();
            int eggCount = player.getInt("eggCount").orElse(0);
            Set<UUID> foundSet = new HashSet<>();
            NbtList foundEggs = player.getListOrEmpty("foundEggs");
            AtomicInteger tmp = new AtomicInteger(1);
            foundEggs.forEach(eggUuid -> {
                foundSet.add(UUID.fromString(eggUuid.toString().replace("\"", "")));
            });
            boolean admin = player.getBoolean("admin").orElse(false);

            state.playerData.put(UUID.fromString(uuid), new PlayerData(eggCount, foundSet, admin));
        });

        NbtList eggs = tag.getListOrEmpty("eggs");
        eggs.forEach(uuid -> {
            state.eggIds.add(UUID.fromString(uuid.toString().replace("\"", "")));
        });

        state.totalDiamonds = tag.getInt("totalDiamonds").orElse(0);
        return state;
    }

    private static final PersistentStateType<EggHuntState> STATE_TYPE = new PersistentStateType<>(
            EggHunt.ID,
            (context) -> new EggHuntState(),
            context -> NbtCompound.CODEC.xmap(
                    EggHuntState::createFromNbt,
                    state -> state.writeNbt(new NbtCompound())
            ),
            null
    );

    public static EggHuntState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        EggHuntState state = persistentStateManager.getOrCreate(STATE_TYPE);
        state.markDirty();

        return state;
    }
}
