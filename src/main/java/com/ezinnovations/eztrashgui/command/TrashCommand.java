package com.ezinnovations.eztrashgui.command;

import com.ezinnovations.eztrashgui.EzTrashGUIPlugin;
import com.ezinnovations.eztrashgui.config.ConfigManager;
import com.ezinnovations.eztrashgui.config.LanguageManager;
import com.ezinnovations.eztrashgui.cooldown.CooldownManager;
import com.ezinnovations.eztrashgui.gui.TrashGuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TrashCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager config;
    private final LanguageManager language;
    private final CooldownManager cooldownManager;
    private final TrashGuiManager trashGuiManager;

    public TrashCommand(EzTrashGUIPlugin plugin, ConfigManager config, LanguageManager language, CooldownManager cooldownManager, TrashGuiManager trashGuiManager) {
        this.config = config;
        this.language = language;
        this.cooldownManager = cooldownManager;
        this.trashGuiManager = trashGuiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.isTrashCommandEnabled()) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            language.send(sender, "player-only");
            return true;
        }

        if (!player.hasPermission(config.getUsePermission())) {
            language.send(player, "no-permission");
            return true;
        }

        if (config.cooldownEnabled() && !player.hasPermission(config.getCooldownBypassPermission())) {
            long remaining = cooldownManager.getRemaining(player.getUniqueId(), config.cooldownSeconds());
            if (remaining > 0) {
                language.send(player, "cooldown", Map.of("seconds", String.valueOf(remaining)));
                return true;
            }
            cooldownManager.apply(player.getUniqueId(), config.cooldownSeconds());
        }

        trashGuiManager.open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
