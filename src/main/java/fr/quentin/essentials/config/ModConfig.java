package fr.quentin.essentials.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        } catch (IOException e) {
            EssentialsClient.LOGGER.error("Error loading Essentials configuration: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(configData, writer);
            }
        } catch (IOException e) {
            EssentialsClient.LOGGER.error("Error saving Essentials configuration: {}", e.getMessage());
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
}