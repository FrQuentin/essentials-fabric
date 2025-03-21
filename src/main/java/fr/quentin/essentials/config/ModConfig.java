package fr.quentin.essentials.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import fr.quentin.essentials.EssentialsClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("essentials.json").toFile();
    private static ConfigData configData = new ConfigData();

    public static class ConfigData {
        public boolean gammaEnabled = false;
        public double gammaValue = 1.0;
        public boolean coordinatesEnabled = false;
        public boolean zoomEnabled = false;
        public boolean zoomOverlayEnabled = true;
        public boolean darknessEffectEnabled = false;
    }

    public static void load() {
        try {
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    configData = GSON.fromJson(reader, ConfigData.class);
                }
            } else {
                save();
            }
        } catch (JsonSyntaxException syntaxException) {
            EssentialsClient.LOGGER.error("Invalid JSON format in config file, resetting to default");
            configData = new ConfigData();
            save();
        } catch (IOException exception) {
            EssentialsClient.LOGGER.error("Error loading Essentials configuration: {}", exception.getMessage());
        }
    }

    public static void save() {
        try {
            File parentDir = CONFIG_FILE.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                EssentialsClient.LOGGER.error("Failed to create config directory: {}", parentDir.getAbsolutePath());
                return;
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(configData, writer);
            }
            if (!CONFIG_FILE.exists()) {
                EssentialsClient.LOGGER.error("Failed to save config file: File does not exist after write attempt.");
            }
        } catch (IOException exception) {
            EssentialsClient.LOGGER.error("Error saving Essentials configuration: {}", exception.getMessage());
        }
    }

    public static boolean isGammaEnabled() {
        return configData.gammaEnabled;
    }

    public static void setGammaEnabled(boolean enabled) {
        configData.gammaEnabled = enabled;
        save();
    }

    public static double getGammaValue() {
        return configData.gammaValue;
    }

    public static void setGammaValue(double value) {
        configData.gammaValue = value;
        save();
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    public static boolean isCoordinatesEnabled() {
        return !configData.coordinatesEnabled;
    }

    public static void setCoordinatesEnabled(boolean enabled) {
        configData.coordinatesEnabled = !enabled;
        save();
    }

    public static boolean isZoomEnabled() {
        return configData.zoomEnabled;
    }

    public static void setZoomEnabled(boolean enabled) {
        configData.zoomEnabled = enabled;
    }

    public static boolean isZoomOverlayEnabled() {
        return configData.zoomOverlayEnabled;
    }

    public static void setZoomOverlayEnabled(boolean enabled) {
        configData.zoomOverlayEnabled = enabled;
        save();
    }

    public static boolean isDarknessEffectEnabled() {
        return configData.darknessEffectEnabled;
    }

    public static void setDarknessEffectEnabled(boolean enabled) {
        configData.darknessEffectEnabled = enabled;
        save();
    }
}