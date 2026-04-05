package com.ezinnovations.eztrashgui.command;

import com.ezinnovations.eztrashgui.EzTrashGUIPlugin;
import com.ezinnovations.eztrashgui.config.ConfigManager;
import com.ezinnovations.eztrashgui.config.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class EzTrashGUICommand implements CommandExecutor, TabCompleter {

    private final EzTrashGUIPlugin plugin;
    private final ConfigManager config;
    private final LanguageManager language;

    public EzTrashGUICommand(EzTrashGUIPlugin plugin, ConfigManager config, LanguageManager language) {
        this.plugin = plugin;
        this.config = config;
        this.language = language;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.isAdminCommandEnabled()) {
            return true;
        }

        if (!sender.hasPermission(config.getAdminPermission())) {
            language.send(sender, "reload-no-permission");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            language.send(sender, "reload");
            return true;
        }

        sender.sendMessage("/" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }
}
