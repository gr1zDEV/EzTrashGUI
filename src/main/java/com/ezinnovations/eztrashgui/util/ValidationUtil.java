package com.ezinnovations.eztrashgui.util;

public final class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isValidInventorySize(int size) {
        return size >= 18 && size <= 54 && size % 9 == 0;
    }
}
