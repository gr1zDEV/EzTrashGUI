package com.ezinnovations.eztrashgui.gui;

import com.ezinnovations.eztrashgui.config.ConfigManager;
import com.ezinnovations.eztrashgui.config.LanguageManager;
import com.ezinnovations.eztrashgui.model.SoundSettings;
import com.ezinnovations.eztrashgui.util.InventoryUtil;
import com.ezinnovations.eztrashgui.util.ItemBuilder;
import com.ezinnovations.eztrashgui.util.MessageUtil;
import com.ezinnovations.eztrashgui.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TrashGuiManager {

    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final LanguageManager language;
    private final Map<UUID, TrashSession> sessions = new ConcurrentHashMap<>();

    private ItemStack fillerItem;
    private ItemStack cancelItem;
    private ItemStack infoItem;
    private ItemStack confirmItem;
    private int cancelSlot;
    private int infoSlot;
    private int confirmSlot;

    public TrashGuiManager(JavaPlugin plugin, ConfigManager config, LanguageManager language) {
        this.plugin = plugin;
        this.config = config;
        this.language = language;
        rebuildTemplates();
    }

    /**
     * Rebuild GUI template items and validated control slot mapping.
     */
    public void rebuildTemplates() {
        fillerItem = ItemBuilder.fromSection(config.getDecorationSection(), config.hexColorsEnabled());
        cancelItem = ItemBuilder.fromSection(config.getCancelSection(), config.hexColorsEnabled());
        infoItem = ItemBuilder.fromSection(config.getInfoSection(), config.hexColorsEnabled());
        confirmItem = ItemBuilder.fromSection(config.getConfirmSection(), config.hexColorsEnabled());

        int size = config.getGuiSize();
        int start = size - 9;
        cancelSlot = config.getCancelSlot();
        infoSlot = config.getInfoSlot();
        confirmSlot = config.getConfirmSlot();

        if (!validateControlSlots(size)) {
            plugin.getLogger().warning("Invalid control slot configuration detected. Falling back to safe bottom-row defaults.");
            cancelSlot = start + 3;
            infoSlot = start + 4;
            confirmSlot = start + 5;
        }
    }

    public void open(Player player) {
        // If an existing session is present, return previous items first to avoid orphaned state.
        TrashSession existing = sessions.get(player.getUniqueId());
        if (existing != null) {
            returnItems(player, existing, null, null);
            sessions.remove(player.getUniqueId());
        }

        int size = config.getGuiSize();
        String title = MessageUtil.colorize(config.getGuiTitle(), config.hexColorsEnabled());

        TrashGuiHolder holder = new TrashGuiHolder(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inventory);

        TrashSession session = new TrashSession(player.getUniqueId(), inventory);
        decorateControlRow(inventory);
        sessions.put(player.getUniqueId(), session);

        player.openInventory(inventory);
        language.send(player, "opened");
        play(player, "open");
    }

    public TrashSession getSession(UUID playerId) {
        return sessions.get(playerId);
    }

    public void confirm(Player player, TrashSession session) {
        if (session == null) return;

        if (config.logDeletions()) {
            logDeletion(player, session.inventory());
        }

        session.setConfirmed(true);
        clearTrashSlots(session.inventory());
        sessions.remove(player.getUniqueId());

        language.send(player, "confirmed");
        play(player, "confirm");
        player.closeInventory();
    }

    public void cancel(Player player, TrashSession session) {
        if (session == null) return;

        returnItems(player, session, "cancelled", "cancel");
        sessions.remove(player.getUniqueId());
        player.closeInventory();
    }

    public void onClose(Player player, TrashSession session) {
        if (session == null) {
            sessions.remove(player.getUniqueId());
            return;
        }

        if (!session.confirmed() && config.returnItemsOnClose()) {
            returnItems(player, session, "returned-on-close", "close-return");
        }

        sessions.remove(player.getUniqueId());
    }

    public void forceCloseAndReturnAll() {
        for (Map.Entry<UUID, TrashSession> entry : sessions.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                onClose(player, entry.getValue());
            }
        }
        sessions.clear();
    }

    public boolean isControlSlot(int slot, int inventorySize) {
        return slot >= inventorySize - 9;
    }

    public boolean isCancelSlot(int slot) {
        return slot == cancelSlot;
    }

    public boolean isConfirmSlot(int slot) {
        return slot == confirmSlot;
    }

    public boolean allowShiftClick() {
        return config.allowShiftClick();
    }

    public boolean allowDrag() {
        return config.allowDrag();
    }

    public boolean handleShiftMoveToTrash(Player player, Inventory top, ItemStack source) {
        if (!allowShiftClick() || source == null || source.getType() == Material.AIR) {
            return false;
        }

        if (isBlockedItem(source)) {
            language.send(player, "blocked-item");
            play(player, "blocked-item");
            return false;
        }

        ItemStack moving = source.clone();
        int max = top.getSize() - 9;

        for (int slot = 0; slot < max; slot++) {
            ItemStack current = top.getItem(slot);
            if (current == null || current.getType() == Material.AIR) {
                top.setItem(slot, moving);
                source.setAmount(0);
                return true;
            }

            if (current.isSimilar(moving) && current.getAmount() < current.getMaxStackSize()) {
                int move = Math.min(current.getMaxStackSize() - current.getAmount(), moving.getAmount());
                current.setAmount(current.getAmount() + move);
                moving.setAmount(moving.getAmount() - move);
                if (moving.getAmount() <= 0) {
                    source.setAmount(0);
                    return true;
                }
            }
        }

        source.setAmount(moving.getAmount());
        return false;
    }

    public boolean isBlockedItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || !config.blockedEnabled()) {
            return false;
        }

        Set<Material> blocked = config.getBlockedMaterials();
        boolean listed = blocked.contains(stack.getType());

        return switch (config.blockMode()) {
            case BLACKLIST -> listed;
            case WHITELIST -> !listed;
        };
    }

    public boolean validateControlSlots(int size) {
        int start = size - 9;
        return cancelSlot >= start && cancelSlot < size
                && infoSlot >= start && infoSlot < size
                && confirmSlot >= start && confirmSlot < size;
    }

    private void decorateControlRow(Inventory inventory) {
        int start = inventory.getSize() - 9;
        for (int i = start; i < inventory.getSize(); i++) {
            inventory.setItem(i, fillerItem.clone());
        }

        inventory.setItem(cancelSlot, cancelItem.clone());
        inventory.setItem(infoSlot, infoItem.clone());
        inventory.setItem(confirmSlot, confirmItem.clone());
    }

    private void clearTrashSlots(Inventory inventory) {
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, null);
        }
    }

    private void returnItems(Player player, TrashSession session, String messageKey, String soundKey) {
        if (session.returned()) {
            return;
        }
        session.setReturned(true);

        List<ItemStack> items = new ArrayList<>();
        Inventory inv = session.inventory();
        int trashSlots = inv.getSize() - 9;

        for (int i = 0; i < trashSlots; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            items.add(item.clone());
            inv.setItem(i, null);
        }

        if (!items.isEmpty()) {
            Map<Integer, ItemStack> leftovers = InventoryUtil.giveItems(player, items);
            if (!leftovers.isEmpty() && config.dropLeftovers()) {
                leftovers.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
                language.send(player, "inventory-full-drop");
            }
        }

        if (messageKey != null) {
            language.send(player, messageKey);
        }
        if (soundKey != null) {
            play(player, soundKey);
        }
    }

    private void play(Player player, String key) {
        SoundSettings settings = config.getSound(key);
        SoundUtil.play(player, settings);
    }

    private void logDeletion(Player player, Inventory inventory) {
        StringBuilder message = new StringBuilder("[EzTrashGUI] ")
                .append(player.getName())
                .append(" confirmed trash deletion");

        if (config.logItemContents()) {
            List<String> items = new ArrayList<>();
            for (int i = 0; i < inventory.getSize() - 9; i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack != null && stack.getType() != Material.AIR) {
                    items.add(stack.getType() + "x" + stack.getAmount());
                }
            }
            if (!items.isEmpty()) {
                message.append(" -> ").append(items.stream().collect(Collectors.joining(", ")));
            }
        }

        plugin.getLogger().info(message.toString());
    }
}
