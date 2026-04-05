package com.ezinnovations.eztrashgui.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtil() {}

    public static String colorize(String text, boolean allowHex) {
        if (text == null) {
            return "";
        }
        String result = text;
        if (allowHex) {
            Matcher matcher = HEX_PATTERN.matcher(result);
            StringBuilder out = new StringBuilder();
            while (matcher.find()) {
                matcher.appendReplacement(out, ChatColor.of("#" + matcher.group(1)).toString());
            }
            matcher.appendTail(out);
            result = out.toString();
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }
}
