package com.ezinnovations.eztrashgui.model;

import org.bukkit.Sound;

import java.util.Locale;
import java.util.logging.Logger;

public record SoundSettings(boolean enabled, Sound sound, float volume, float pitch) {

    public static SoundSettings fromConfig(String rawSound, boolean enabled, float volume, float pitch, Logger logger) {
        Sound parsed = null;
        if (enabled) {
            try {
                parsed = Sound.valueOf(rawSound.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                logger.warning("Invalid sound in sounds.yml: " + rawSound + " (sound disabled)");
                enabled = false;
            }
        }
        return new SoundSettings(enabled, parsed, volume, pitch);
    }
}
