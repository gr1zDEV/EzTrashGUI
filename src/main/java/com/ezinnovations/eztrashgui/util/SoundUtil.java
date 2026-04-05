package com.ezinnovations.eztrashgui.util;

import com.ezinnovations.eztrashgui.model.SoundSettings;
import org.bukkit.entity.Player;

public final class SoundUtil {

    private SoundUtil() {}

    public static void play(Player player, SoundSettings settings) {
        if (settings == null || !settings.enabled() || settings.sound() == null) {
            return;
        }
        player.playSound(player.getLocation(), settings.sound(), settings.volume(), settings.pitch());
    }
}
