package io.github.mumboteam.egghunt.utils;

import io.github.mumboteam.egghunt.EggHunt;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.Objects;

public class RewardsState extends PersistentState {
    public int totalDiamonds = 0;

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("totalDiamonds", totalDiamonds);
        return nbt;
    }

    public static RewardsState createFromNbt(NbtCompound tag) {
        RewardsState state = new RewardsState();
        state.totalDiamonds = tag.getInt("totalDiamonds").orElse(0);
        return state;
    }

    private static final PersistentStateType<RewardsState> STATE_TYPE = new PersistentStateType<>(
            EggHunt.ID,
            (context) -> new RewardsState(),
            context -> NbtCompound.CODEC.xmap(
                    RewardsState::createFromNbt,
                    state -> state.writeNbt(new NbtCompound())
            ),
            null
    );

    public static RewardsState getServerState(MinecraftServer server) {
        /*Kept getting `java.lang.ClassCastException: class io.github.mumboteam.egghunt.utils.EggHuntState cannot be cast to class io.github.mumboteam.egghunt.utils.RewardsState`
        Probably I simply reused a variable, but I can't find where, anc can't be bothered to find it, so storing it in another dimension instead fixes the issue
        To reproduce, replace `END` with `OVERWORLD` and submit an egg
        I'm totally gonna regret doing it this way later lol*/
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.END)).getPersistentStateManager();
        RewardsState state = persistentStateManager.getOrCreate(STATE_TYPE);
        state.markDirty();

        return state;
    }
}