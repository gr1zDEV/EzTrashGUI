package com.ezinnovations.eztrashgui.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * Holder used to reliably identify EzTrashGUI inventories.
 */
public class TrashGuiHolder implements InventoryHolder {

    private final UUID playerId;
    private Inventory inventory;

    public TrashGuiHolder(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID playerId() {
        return playerId;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
