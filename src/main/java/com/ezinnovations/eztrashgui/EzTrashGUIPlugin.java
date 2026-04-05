package com.ezinnovations.eztrashgui;

import com.ezinnovations.eztrashgui.command.EzTrashGUICommand;
import com.ezinnovations.eztrashgui.command.TrashCommand;
import com.ezinnovations.eztrashgui.config.ConfigManager;
import com.ezinnovations.eztrashgui.config.LanguageManager;
import com.ezinnovations.eztrashgui.cooldown.CooldownManager;
import com.ezinnovations.eztrashgui.gui.TrashGuiManager;
import com.ezinnovations.eztrashgui.listener.CommandVisibilityListener;
import com.ezinnovations.eztrashgui.listener.TrashGuiListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public final class EzTrashGUIPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private LanguageManager languageManager;
    private CooldownManager cooldownManager;
    private TrashGuiManager trashGuiManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.loadAll();

        this.languageManager = new LanguageManager(this, configManager);
        this.languageManager.load();

        this.cooldownManager = new CooldownManager();
        this.trashGuiManager = new TrashGuiManager(this, configManager, languageManager);

        registerCommands();
        getServer().getPluginManager().registerEvents(new TrashGuiListener(this, trashGuiManager, languageManager), this);
        getServer().getPluginManager().registerEvents(new CommandVisibilityListener(configManager), this);
    }

    @Override
    public void onDisable() {
        trashGuiManager.forceCloseAndReturnAll();
    }

    public void reloadPlugin() {
        configManager.loadAll();
        languageManager.load();
        trashGuiManager.rebuildTemplates();
        registerAliases();
    }

    private void registerCommands() {
        PluginCommand trash = Objects.requireNonNull(getCommand("trash"), "trash command missing in plugin.yml");
        PluginCommand admin = Objects.requireNonNull(getCommand("eztrashgui"), "eztrashgui command missing in plugin.yml");

        TrashCommand trashCommand = new TrashCommand(this, configManager, languageManager, cooldownManager, trashGuiManager);
        EzTrashGUICommand ezTrashGUICommand = new EzTrashGUICommand(this, configManager, languageManager);

        trash.setExecutor(trashCommand);
        trash.setTabCompleter(trashCommand);

        admin.setExecutor(ezTrashGUICommand);
        admin.setTabCompleter(ezTrashGUICommand);

        registerAliases();
    }

    private void registerAliases() {
        applyCommandConfig("trash", configManager.getTrashAliases());
        applyCommandConfig("eztrashgui", configManager.getAdminAliases());
        syncCommandsIfAvailable();
    }

    private void applyCommandConfig(String commandName, List<String> aliases) {
        PluginCommand cmd = getCommand(commandName);
        if (cmd == null) {
            return;
        }
        cmd.setAliases(aliases);
    }

    private void syncCommandsIfAvailable() {
        try {
            Method syncCommands = getServer().getClass().getMethod("syncCommands");
            syncCommands.invoke(getServer());
        } catch (ReflectiveOperationException ignored) {
            getLogger().log(Level.FINE, "Command sync not available on this server implementation.");
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
