package ru.harimasa.ei.lang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.locale.Language;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Lang {
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private static final Map<String, Map<String, String>> CACHE = new HashMap<>();
    private static final String FALLBACK_CODE = "en_us";

    private static String overrideCode = "";
    private static Map<String, String> active;

    private Lang() {}

    public static void setLanguage(String code) {
        String normalized = code == null ? "" : code.trim().toLowerCase(Locale.ROOT);
        overrideCode = normalized;
        if (normalized.isEmpty()) {
            active = null;
            return;
        }
        Map<String, String> translations = CACHE.computeIfAbsent(normalized, Lang::load);
        active = translations.isEmpty() ? null : translations;
    }

    public static String currentCode() {
        if (!overrideCode.isEmpty()) return overrideCode;
        return minecraftCode();
    }

    public static String get(String key) {
        if (active != null) {
            String value = active.get(key);
            if (value != null) return value;
            Map<String, String> fallback = CACHE.computeIfAbsent(FALLBACK_CODE, Lang::load);
            value = fallback.get(key);
            if (value != null) return value;
        }
        return Language.getInstance().getOrDefault(key);
    }

    private static String minecraftCode() {
        String code = Language.getInstance().getOrDefault("language.code");
        return "language.code".equals(code) ? FALLBACK_CODE : code;
    }

    private static Map<String, String> load(String code) {
        String path = "/assets/enchantments-indicator/lang/" + code + ".json";
        try (var stream = Lang.class.getResourceAsStream(path)) {
            if (stream == null) return Map.of();
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                Map<String, String> translations = GSON.fromJson(reader, MAP_TYPE);
                return translations != null ? translations : Map.of();
            }
        } catch (Exception e) {
            return Map.of();
        }
    }
}