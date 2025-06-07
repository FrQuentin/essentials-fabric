package fr.quentin.essentials.utils;

import net.fabricmc.loader.api.FabricLoader;
import fr.quentin.essentials.EssentialsClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class ResourcePackManager {
    private static final String RESOURCE_PACKS_FOLDER = "resourcepacks";
    private static final String CACHE_FILE = "essentials_resourcepacks.properties";
    private static final String[] RESOURCE_PACKS = {
            "Suspicious Highlight.zip",
            "Ores Highlight.zip"
    };

    public static void copyResourcePacks() {
        Path gameResourcePacks = FabricLoader.getInstance().getGameDir().resolve(RESOURCE_PACKS_FOLDER);
        Path cacheFile = FabricLoader.getInstance().getGameDir().resolve(CACHE_FILE);

        try {
            Files.createDirectories(gameResourcePacks);

            Properties cache = loadCache(cacheFile);
            boolean cacheUpdated = false;

            for (String packName : RESOURCE_PACKS) {
                if (shouldCopyResourcePack(packName, gameResourcePacks, cache)) {
                    if (copyResourcePack(packName, gameResourcePacks)) {
                        String hash = calculateFileHash(gameResourcePacks.resolve(packName));
                        cache.setProperty(packName + ".hash", hash);
                        cache.setProperty(packName + ".version", getCurrentModVersion());
                        cacheUpdated = true;
                        EssentialsClient.LOGGER.info("Resource pack copied: {}", packName);
                    }
                } else {
                    EssentialsClient.LOGGER.debug("Resource pack {} already up to date", packName);
                }
            }

            if (cacheUpdated) {
                saveCache(cache, cacheFile);
            }

        } catch (IOException e) {
            EssentialsClient.LOGGER.error("Error while managing resource packs", e);
        }
    }

    private static boolean shouldCopyResourcePack(String packName, Path gameResourcePacks, Properties cache) throws IOException {
        Path targetPath = gameResourcePacks.resolve(packName);

        if (!Files.exists(targetPath)) {
            EssentialsClient.LOGGER.debug("Resource pack {} not found, will copy", packName);
            return true;
        }

        String cachedVersion = cache.getProperty(packName + ".version", "");
        String currentVersion = getCurrentModVersion();
        if (!cachedVersion.equals(currentVersion)) {
            EssentialsClient.LOGGER.debug("Mod version changed for {}, will update", packName);
            return true;
        }

        String cachedHash = cache.getProperty(packName + ".hash", "");
        String currentHash = calculateFileHash(targetPath);
        if (!cachedHash.equals(currentHash)) {
            EssentialsClient.LOGGER.debug("File hash changed for {}, will update", packName);
            return true;
        }

        return false;
    }

    private static boolean copyResourcePack(String packName, Path gameResourcePacks) {
        String resourcePath = "/" + RESOURCE_PACKS_FOLDER + "/" + packName;

        try (InputStream packStream = ResourcePackManager.class.getResourceAsStream(resourcePath)) {
            if (packStream == null) {
                EssentialsClient.LOGGER.error("Resource pack {} not found in mod resources at path: {}", packName, resourcePath);
                EssentialsClient.LOGGER.error("Make sure the file exists in src/main/resources/resourcepacks/");
                return false;
            }

            Path targetPath = gameResourcePacks.resolve(packName);

            if (Files.exists(targetPath)) {
                Files.delete(targetPath);
            }

            byte[] buffer = packStream.readAllBytes();
            if (buffer.length == 0) {
                EssentialsClient.LOGGER.error("Resource pack {} is empty or corrupted in mod resources", packName);
                return false;
            }

            Files.write(targetPath, buffer);
            EssentialsClient.LOGGER.info("Successfully copied resource pack: {} ({} bytes)", packName, buffer.length);
            return true;

        } catch (IOException e) {
            EssentialsClient.LOGGER.error("Failed to copy resource pack: {}", packName, e);
            return false;
        }
    }

    private static String calculateFileHash(Path filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = md.digest(fileBytes);

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (IOException | NoSuchAlgorithmException e) {
            EssentialsClient.LOGGER.warn("Could not calculate hash for {}", filePath, e);
            return "";
        }
    }

    private static Properties loadCache(Path cacheFile) {
        Properties cache = new Properties();
        if (Files.exists(cacheFile)) {
            try (InputStream input = Files.newInputStream(cacheFile)) {
                cache.load(input);
            } catch (IOException e) {
                EssentialsClient.LOGGER.warn("Could not load resource pack cache", e);
            }
        }
        return cache;
    }

    private static void saveCache(Properties cache, Path cacheFile) {
        try {
            cache.store(Files.newOutputStream(cacheFile), "Essentials Resource Packs Cache");
        } catch (IOException e) {
            EssentialsClient.LOGGER.warn("Could not save resource pack cache", e);
        }
    }

    private static String getCurrentModVersion() {
        return FabricLoader.getInstance()
                .getModContainer("essentials")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }

    public static void cleanupOldResourcePacks() {
        Path gameResourcePacks = FabricLoader.getInstance().getGameDir().resolve(RESOURCE_PACKS_FOLDER);
        Path cacheFile = FabricLoader.getInstance().getGameDir().resolve(CACHE_FILE);

        try {
            Properties cache = loadCache(cacheFile);
            boolean cacheUpdated = false;

            for (String key : cache.stringPropertyNames()) {
                if (key.endsWith(".hash")) {
                    String packName = key.substring(0, key.length() - 5);

                    boolean stillExists = false;
                    for (String currentPack : RESOURCE_PACKS) {
                        if (currentPack.equals(packName)) {
                            stillExists = true;
                            break;
                        }
                    }

                    if (!stillExists) {
                        Path oldPackPath = gameResourcePacks.resolve(packName);
                        if (Files.exists(oldPackPath)) {
                            Files.delete(oldPackPath);
                            EssentialsClient.LOGGER.info("Removed old resource pack: {}", packName);
                        }
                        cache.remove(packName + ".hash");
                        cache.remove(packName + ".version");
                        cacheUpdated = true;
                    }
                }
            }

            if (cacheUpdated) {
                saveCache(cache, cacheFile);
            }

        } catch (IOException e) {
            EssentialsClient.LOGGER.error("Error while cleaning up old resource packs", e);
        }
    }
}