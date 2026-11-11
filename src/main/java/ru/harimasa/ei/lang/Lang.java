package ru.harimasa.ei.lang;

import net.minecraft.locale.Language;

public final class Lang {
    private Lang() {}

    public static String get(String key) {
        return Language.getInstance().getOrDefault(key);
    }
}