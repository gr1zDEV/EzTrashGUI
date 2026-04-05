package com.ezinnovations.eztrashgui.gui;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class TrashSession {

    private final UUID playerId;
    private final Inventory inventory;
    private volatile boolean confirmed;
    private volatile boolean returned;

    public TrashSession(UUID playerId, Inventory inventory) {
        this.playerId = playerId;
        this.inventory = inventory;
    }

    public UUID playerId() { return playerId; }
    public Inventory inventory() { return inventory; }
    public boolean confirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    public boolean returned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}
