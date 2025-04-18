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
    public HashMap<UUID, Integer> players = new HashMap<>();

    public NbtCompound writeNbt(NbtCompound nbt) {
        players.forEach((uuid, eggCount) -> {
            NbtCompound player = new NbtCompound();
            player.putInt("eggCount", eggCount);
            nbt.put(uuid.toString(), player);
        });
        return nbt;
    }

    public static EggHuntState createFromNbt(NbtCompound tag) {
        EggHuntState state = new EggHuntState();
        tag.getKeys().forEach(uuid -> {
            NbtCompound player = tag.getCompound(uuid).get();
            int eggCount = player.getInt("eggCount").orElse(0);
            state.players.put(UUID.fromString(uuid), eggCount);
        });
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
