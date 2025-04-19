package io.github.mumboteam.egghunt.utils;

import io.github.mumboteam.egghunt.EggHunt;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;

public class EggHuntState extends PersistentState {
    public HashMap<UUID, Integer> playerScores = new HashMap<>();
    public int totalDiamonds = 0;

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound players = new NbtCompound();
        playerScores.forEach((uuid, eggCount) -> {
            NbtCompound player = new NbtCompound();
            player.putInt("eggCount", eggCount);
            players.put(uuid.toString(), player);
        });
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
            state.playerScores.put(UUID.fromString(uuid), eggCount);
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
