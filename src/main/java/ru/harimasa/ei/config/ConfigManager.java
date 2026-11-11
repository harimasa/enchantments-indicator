package ru.harimasa.ei.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("enchantments-indicator");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("enchantments-indicator.json");

    private static final int SAVE_DELAY_TICKS = 10;

    private static EIConfig config = new EIConfig();
    private static boolean dirty;
    private static int saveDelay;

    private ConfigManager() {}

    public static EIConfig get() {
        return config;
    }

    public static void load() {
        if (!Files.exists(FILE)) {
            config = new EIConfig();
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            EIConfig loaded = GSON.fromJson(reader, EIConfig.class);
            config = loaded != null ? loaded : new EIConfig();
        } catch (Exception e) {
            LOGGER.error("Failed to read config {}, using defaults", FILE, e);
            config = new EIConfig();
        }

        config.validate();
    }

    public static void markDirty() {
        dirty = true;
        saveDelay = SAVE_DELAY_TICKS;
    }

    public static void tick() {
        if (!dirty) return;

        if (saveDelay > 0) {
            saveDelay--;
            return;
        }
        save();
    }

    public static void save() {
        dirty = false;
        saveDelay = 0;
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to write config {}", FILE, e);
        }
    }
}