package io.github.mumboteam.flagparticipation.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private int flagCount;
    private final Set<UUID> foundFlags;
    private boolean admin;

    public PlayerData() {
        this.flagCount = 0;
        this.foundFlags = new HashSet<>();
        this.admin = false;
    }

    public PlayerData(int flagCount, Set<UUID> foundFlags, boolean admin) {
        this.flagCount = flagCount;
        this.foundFlags = foundFlags;
        this.admin = admin;
    }

    public int getFlagCount() {
        return flagCount;
    }

    public void increaseFlagCount(int amount) {
        this.flagCount += amount;
    }

    public Set<UUID> getFoundFlags() {
        return foundFlags;
    }

    public void addToFoundFlags(UUID uuid) {
        this.foundFlags.add(uuid);
    }

    public void resetFoundFlags() {
        this.foundFlags.clear();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void toggleAdmin() {
        this.admin = !this.admin;
    }
}

