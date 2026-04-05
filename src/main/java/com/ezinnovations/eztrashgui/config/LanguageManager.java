package com.ezinnovations.eztrashgui.config;

import com.ezinnovations.eztrashgui.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    private final Map<String, FileConfiguration> loadedLanguages = new HashMap<>();
    private FileConfiguration active;
    private FileConfiguration fallback;

    public LanguageManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Reload all language files under /messages and choose active language from config.
     */
    public void load() {
        if (configManager.createMissingLanguageFiles()) {
            configManager.saveMissingLanguageFile("en");
            configManager.saveMissingLanguageFile("es");
        }

        loadedLanguages.clear();

        File messagesDir = new File(plugin.getDataFolder(), "messages");
        if (!messagesDir.exists() && !messagesDir.mkdirs()) {
            plugin.getLogger().warning("Could not create messages directory.");
        }

        File[] files = messagesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String locale = file.getName().replace(".yml", "").toLowerCase();
                loadedLanguages.put(locale, YamlConfiguration.loadConfiguration(file));
            }
        }

        fallback = loadedLanguages.get("en");
        if (fallback == null) {
            // Last-resort fallback if en.yml is somehow missing.
            fallback = YamlConfiguration.loadConfiguration(configManager.getLanguageFile("en"));
        }

        String selected = configManager.getLanguage();
        active = loadedLanguages.get(selected);
        if (active == null) {
            active = fallback;
            plugin.getLogger().warning("Language file not found for '" + selected + "'. Falling back to English.");
        }
    }

    public String raw(String key) {
        String path = "messages." + key;

        String value = active != null ? active.getString(path) : null;
        if (value != null) {
            return value;
        }

        if (fallback != null) {
            return fallback.getString(path, "Missing message: " + key);
        }

        return "Missing message: " + key;
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(MessageUtil.format(raw(key), configManager.getPrefixRaw(), placeholders, configManager.hexColorsEnabled()));
    }
}
