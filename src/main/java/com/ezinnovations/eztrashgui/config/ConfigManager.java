package com.ezinnovations.eztrashgui.config;

import com.ezinnovations.eztrashgui.model.SoundSettings;
import com.ezinnovations.eztrashgui.util.ValidationUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

public class ConfigManager {

    public enum BlockMode { BLACKLIST, WHITELIST }

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration gui;
    private FileConfiguration blocked;
    private FileConfiguration sounds;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        plugin.saveDefaultConfig();
        saveDefault("gui.yml");
        saveDefault("blocked-items.yml");
        saveDefault("sounds.yml");
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.gui = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "gui.yml"));
        this.blocked = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "blocked-items.yml"));
        this.sounds = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "sounds.yml"));
    }

    private void saveDefault(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    public String getPrefixRaw() { return config.getString("plugin.prefix", "&8[EzTrashGUI]&r"); }
    public boolean hexColorsEnabled() { return config.getBoolean("settings.hex-colors", true); }

    public String getLanguage() { return config.getString("language", "en").toLowerCase(Locale.ROOT); }
    public boolean createMissingLanguageFiles() { return config.getBoolean("settings.create-missing-language-files", true); }

    public String getUsePermission() { return config.getString("permissions.use", "eztrashgui.use"); }
    public String getAdminPermission() { return config.getString("permissions.admin", "eztrashgui.admin"); }
    public String getCooldownBypassPermission() { return config.getString("cooldown.bypass-permission", "eztrashgui.cooldown.bypass"); }

    public boolean cooldownEnabled() { return config.getBoolean("cooldown.enabled", false); }
    public long cooldownSeconds() { return Math.max(0L, config.getLong("cooldown.seconds", 5)); }

    public boolean logDeletions() { return config.getBoolean("settings.log-deletions", false); }
    public boolean logItemContents() { return config.getBoolean("settings.log-item-contents", true); }

    public boolean isTrashCommandEnabled() { return config.getBoolean("commands.trash.enabled", true); }
    public boolean isAdminCommandEnabled() { return config.getBoolean("commands.eztrashgui.enabled", true); }

    public String getTrashCommandName() { return config.getString("commands.trash.name", "trash"); }

    public List<String> getTrashAliases() { return config.getStringList("commands.trash.aliases"); }
    public List<String> getAdminAliases() { return config.getStringList("commands.eztrashgui.aliases"); }

    public int getGuiSize() {
        int size = gui.getInt("gui.size", 27);
        if (!ValidationUtil.isValidInventorySize(size)) {
            plugin.getLogger().warning("Invalid GUI size in gui.yml. Falling back to 27.");
            return 27;
        }
        return size;
    }

    public String getGuiTitle() { return gui.getString("gui.title", "&cTrash GUI"); }
    public boolean allowShiftClick() { return gui.getBoolean("gui.allow-shift-click", true); }
    public boolean allowDrag() { return gui.getBoolean("gui.allow-drag", true); }
    public boolean returnItemsOnClose() { return gui.getBoolean("gui.return-items-on-close", true); }
    public boolean dropLeftovers() { return gui.getBoolean("gui.drop-leftovers-if-inventory-full", true); }

    public ConfigurationSection getDecorationSection() { return gui.getConfigurationSection("decoration"); }
    public ConfigurationSection getCancelSection() { return gui.getConfigurationSection("controls.cancel"); }
    public ConfigurationSection getInfoSection() { return gui.getConfigurationSection("controls.info"); }
    public ConfigurationSection getConfirmSection() { return gui.getConfigurationSection("controls.confirm"); }

    public int getCancelSlot() { return gui.getInt("controls.cancel.slot", 21); }
    public int getInfoSlot() { return gui.getInt("controls.info.slot", 22); }
    public int getConfirmSlot() { return gui.getInt("controls.confirm.slot", 23); }

    public Set<Material> getBlockedMaterials() {
        Set<Material> result = new HashSet<>();
        for (String name : blocked.getStringList("blocked-items.materials")) {
            Material material = Material.matchMaterial(name);
            if (material == null) {
                plugin.getLogger().warning("Invalid blocked material in blocked-items.yml: " + name);
                continue;
            }
            result.add(material);
        }
        return Collections.unmodifiableSet(result);
    }

    public boolean blockedEnabled() { return blocked.getBoolean("blocked-items.enabled", false); }

    public BlockMode blockMode() {
        try {
            return BlockMode.valueOf(blocked.getString("blocked-items.mode", "BLACKLIST").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid blocked-items mode. Using BLACKLIST.");
            return BlockMode.BLACKLIST;
        }
    }

    public SoundSettings getSound(String key) {
        String path = "sounds." + key;
        boolean enabled = sounds.getBoolean(path + ".enabled", false);
        String soundName = sounds.getString(path + ".sound", "UI_BUTTON_CLICK");
        float volume = (float) sounds.getDouble(path + ".volume", 1.0D);
        float pitch = (float) sounds.getDouble(path + ".pitch", 1.0D);
        return SoundSettings.fromConfig(soundName, enabled, volume, pitch, plugin.getLogger());
    }

    public void saveMissingLanguageFile(String locale) {
        String path = "messages/" + locale + ".yml";
        try {
            File file = new File(plugin.getDataFolder(), path);
            if (!file.exists()) {
                plugin.saveResource(path, false);
            }
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().log(Level.WARNING, "Bundled language file not found: " + locale, ex);
        }
    }

    public File getLanguageFile(String locale) {
        return new File(plugin.getDataFolder(), "messages/" + locale + ".yml");
    }
}
