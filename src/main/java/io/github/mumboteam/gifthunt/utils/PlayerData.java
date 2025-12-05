package io.github.mumboteam.gifthunt.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private int giftCount;
    private Set<UUID> foundGifts;
    private boolean admin;

    public PlayerData() {
        this.giftCount = 0;
        this.foundGifts = new HashSet<>();
        this.admin = false;
    }

    public PlayerData(int giftCount, Set<UUID> foundGifts, boolean admin) {
        this.giftCount = giftCount;
        this.foundGifts = foundGifts;
        this.admin = admin;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void increaseGiftCount(int amount) {
        this.giftCount += amount;
    }

    public Set<UUID> getFoundGifts() {
        return foundGifts;
    }

    public void addToFoundGifts(UUID uuid) {
        this.foundGifts.add(uuid);
    }

    public void resetFoundGifts() {
        this.foundGifts.clear();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void toggleAdmin() {
        this.admin = !this.admin;
    }
}

