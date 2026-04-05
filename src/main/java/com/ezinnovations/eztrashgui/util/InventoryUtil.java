package com.ezinnovations.eztrashgui.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class InventoryUtil {

    private InventoryUtil() {}

    public static Map<Integer, ItemStack> giveItems(Player player, List<ItemStack> items) {
        return player.getInventory().addItem(items.toArray(new ItemStack[0]));
    }
}
