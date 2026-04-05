package com.ezinnovations.eztrashgui.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ItemBuilder {

    private ItemBuilder() {}

    public static ItemStack fromSection(ConfigurationSection section, boolean allowHex) {
        if (section == null) {
            return new ItemStack(Material.BARRIER);
        }

        Material material = Material.matchMaterial(section.getString("material", "BARRIER"));
        if (material == null) {
            material = Material.BARRIER;
        }

        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        meta.setDisplayName(ColorUtil.colorize(section.getString("name", " "), allowHex));

        List<String> lore = section.getStringList("lore").stream()
                .map(line -> ColorUtil.colorize(line, allowHex))
                .toList();
        meta.setLore(lore);

        if (section.getBoolean("glow", false)) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);
        return stack;
    }
}
