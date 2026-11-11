package ru.harimasa.ei.config.enums;

public enum LanguageOption {
    ENGLISH("en_us", "English", "textures/gui/flags/us.png"),
    RUSSIAN("ru_ru", "Русский", "textures/gui/flags/ru.png"),
    BELARUSIAN("be_by", "Беларуская", "textures/gui/flags/by.png"),
    UKRAINIAN("uk_ua", "Українська", "textures/gui/flags/ua.png"),
    POLISH("pl_pl", "Polski", "textures/gui/flags/pl.png"),
    GERMAN("de_de", "Deutsch", "textures/gui/flags/d.png");

    private final String code;
    private final String displayName;
    private final String texturePath;

    LanguageOption(String code, String displayName, String texturePath) {
        this.code = code;
        this.displayName = displayName;
        this.texturePath = texturePath;
    }

    public String code() {
        return code;
    }

    public String displayName() {
        return displayName;
    }

    public String texturePath() {
        return texturePath;
    }

    public static LanguageOption byCode(String code) {
        if (code == null) return null;
        for (LanguageOption option : values()) {
            if (option.code.equalsIgnoreCase(code)) return option;
        }
        return null;
    }

    public static LanguageOption fromCodeOrDefault(String code) {
        LanguageOption option = byCode(code);
        return option != null ? option : ENGLISH;
    }
}