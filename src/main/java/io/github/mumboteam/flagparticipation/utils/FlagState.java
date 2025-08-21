package io.github.mumboteam.flagparticipation.utils;

import io.github.mumboteam.flagparticipation.FlagParticipation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;

public class FlagState extends PersistentState {
    public HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public HashSet<UUID> flagsIds = new HashSet<>();

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound players = new NbtCompound();
        playerData.forEach((uuid, data) -> {
            NbtCompound player = new NbtCompound();
            player.putInt("flagCount", data.getFlagCount());
            NbtList foundFlags = new NbtList();
            data.getFoundFlags().forEach(flagUuid -> {
                foundFlags.add(NbtString.of(flagUuid.toString()));
            });
            player.put("foundFlags", foundFlags);
            player.putBoolean("admin", data.isAdmin());
            players.put(uuid.toString(), player);
        });

        NbtList flags = new NbtList();
        flagsIds.forEach(uuid -> {
            flags.add(NbtString.of(uuid.toString()));
        });

        nbt.put("flags", flags);
        nbt.put("players", players);
        return nbt;
    }

    public static FlagState createFromNbt(NbtCompound tag) {
        FlagState state = new FlagState();
        NbtCompound players = tag.getCompoundOrEmpty("players");
        players.getKeys().forEach(uuid -> {
            NbtCompound player = players.getCompound(uuid).get();
            int flagCount = player.getInt("flagCount").orElse(0);
            Set<UUID> foundSet = new HashSet<>();
            NbtList foundFlags = player.getListOrEmpty("foundFlags");
            foundFlags.forEach(flagUuid -> {
                foundSet.add(UUID.fromString(flagUuid.toString().replace("\"", "")));
            });
            boolean admin = player.getBoolean("admin").orElse(false);

            state.playerData.put(UUID.fromString(uuid), new PlayerData(flagCount, foundSet, admin));
        });

        NbtList flags = tag.getListOrEmpty("flags");
        flags.forEach(uuid -> {
            state.flagsIds.add(UUID.fromString(uuid.toString().replace("\"", "")));
        });

        return state;
    }

    private static final PersistentStateType<FlagState> STATE_TYPE = new PersistentStateType<>(
            FlagParticipation.ID,
            (context) -> new FlagState(),
            context -> NbtCompound.CODEC.xmap(
                    FlagState::createFromNbt,
                    state -> state.writeNbt(new NbtCompound())
            ),
            null
    );

    public static FlagState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        FlagState state = persistentStateManager.getOrCreate(STATE_TYPE);
        state.markDirty();

        return state;
    }
}
