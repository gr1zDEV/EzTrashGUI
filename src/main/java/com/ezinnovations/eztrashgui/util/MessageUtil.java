package com.ezinnovations.eztrashgui.util;

import java.util.Map;

public final class MessageUtil {

    private MessageUtil() {}

    public static String format(String raw, String prefix, Map<String, String> placeholders, boolean allowHex) {
        String output = raw == null ? "" : raw.replace("%prefix%", prefix == null ? "" : prefix);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            output = output.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return colorize(output, allowHex);
    }

    public static String colorize(String raw, boolean allowHex) {
        return ColorUtil.colorize(raw, allowHex);
    }
}
