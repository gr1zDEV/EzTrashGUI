package com.ezinnovations.eztrashgui.listener;

import com.ezinnovations.eztrashgui.EzTrashGUIPlugin;
import com.ezinnovations.eztrashgui.config.LanguageManager;
import com.ezinnovations.eztrashgui.gui.TrashGuiHolder;
import com.ezinnovations.eztrashgui.gui.TrashGuiManager;
import com.ezinnovations.eztrashgui.gui.TrashSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class TrashGuiListener implements Listener {

    private final TrashGuiManager guiManager;
    private final LanguageManager language;

    public TrashGuiListener(EzTrashGUIPlugin plugin, TrashGuiManager guiManager, LanguageManager language) {
        this.guiManager = guiManager;
        this.language = language;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryView view = event.getView();
        if (!(view.getTopInventory().getHolder() instanceof TrashGuiHolder holder)) return;

        TrashSession session = guiManager.getSession(holder.playerId());
        if (session == null) return;

        int topSize = view.getTopInventory().getSize();
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0) return;

        if (rawSlot < topSize) {
            if (guiManager.isControlSlot(rawSlot, topSize)) {
                event.setCancelled(true);
                if (guiManager.isConfirmSlot(rawSlot)) {
                    guiManager.confirm(player, session);
                } else if (guiManager.isCancelSlot(rawSlot)) {
                    guiManager.cancel(player, session);
                }
                return;
            }

            ItemStack cursor = event.getCursor();
            ItemStack hotbar = event.getHotbarButton() >= 0 ? player.getInventory().getItem(event.getHotbarButton()) : null;
            if (guiManager.isBlockedItem(cursor) || guiManager.isBlockedItem(hotbar)) {
                event.setCancelled(true);
                language.send(player, "blocked-item");
                return;
            }
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
            if (!guiManager.allowShiftClick()) {
                return;
            }
            ItemStack current = event.getCurrentItem();
            if (current == null) {
                return;
            }
            Inventory top = session.inventory();
            guiManager.handleShiftMoveToTrash(player, top, current);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof TrashGuiHolder)) return;

        int topSize = event.getView().getTopInventory().getSize();
        if (!guiManager.allowDrag()) {
            event.setCancelled(true);
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < topSize && guiManager.isControlSlot(slot, topSize)) {
                event.setCancelled(true);
                return;
            }
        }

        if (guiManager.isBlockedItem(event.getOldCursor())) {
            event.setCancelled(true);
            language.send(player, "blocked-item");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof TrashGuiHolder holder)) return;

        TrashSession session = guiManager.getSession(holder.playerId());
        guiManager.onClose(player, session);
    }
}
