package io.github.mumboteam.egghunt.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private int eggCount;
    private Set<UUID> foundEggs;
    private boolean admin;

    public PlayerData() {
        this.eggCount = 0;
        this.foundEggs = new HashSet<>();
        this.admin = false;
    }

    public PlayerData(int eggCount, Set<UUID> foundEggs, boolean admin) {
        this.eggCount = eggCount;
        this.foundEggs = foundEggs;
        this.admin = admin;
    }

    public int getEggCount() {
        return eggCount;
    }

    public void increaseEggCount(int amount) {
        this.eggCount += amount;
    }

    public Set<UUID> getFoundEggs() {
        return foundEggs;
    }

    public void addToFoundEggs(UUID uuid) {
        this.foundEggs.add(uuid);
    }

    public void resetFoundEggs() {
        this.foundEggs.clear();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void toggleAdmin() {
        this.admin = !this.admin;
    }
}

