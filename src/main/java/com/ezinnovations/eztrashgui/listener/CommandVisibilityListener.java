package com.ezinnovations.eztrashgui.listener;

import com.ezinnovations.eztrashgui.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.Locale;

public class CommandVisibilityListener implements Listener {

    private final ConfigManager config;

    public CommandVisibilityListener(ConfigManager config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().hasPermission(config.getAdminPermission())) {
            return;
        }

        Collection<String> commands = event.getCommands();
        String trashName = config.getTrashCommandName().toLowerCase(Locale.ROOT);

        commands.remove("eztrashgui:" + trashName);
    }
}
