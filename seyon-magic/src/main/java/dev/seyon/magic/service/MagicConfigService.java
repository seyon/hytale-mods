package dev.seyon.magic.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.magic.config.MagicItemConfig;
import dev.seyon.magic.config.MagicMainConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Service for managing magic system configuration
 */
public class MagicConfigService {

    private final HytaleLogger logger;
    private final Gson gson;
    private final File configRoot;

    private MagicMainConfig mainConfig;
    private MagicItemConfig wandConfig;
    private MagicItemConfig grimoireConfig;

    public MagicConfigService(HytaleLogger logger) {
        this.logger = logger;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configRoot = new File("SeyonMagic/config");
    }

    /**
     * Load configuration from disk
     */
    public void load() {
        logger.at(Level.INFO).log("MagicConfigService: Loading configuration...");
        
        // Create config directory if it doesn't exist
        if (!configRoot.exists()) {
            if (configRoot.mkdirs()) {
                logger.at(Level.INFO).log("Created config directory: " + configRoot.getAbsolutePath());
            }
        }
        
        // Create items subdirectory
        File itemsDir = new File(configRoot, "items");
        if (!itemsDir.exists()) {
            itemsDir.mkdirs();
        }

        // Load main config
        mainConfig = loadConfig(new File(configRoot, "main.json"), MagicMainConfig.class, new MagicMainConfig());
        
        // Load wand config
        wandConfig = loadConfig(new File(itemsDir, "wands.json"), MagicItemConfig.class, createDefaultWandConfig());
        
        // Load grimoire config
        grimoireConfig = loadConfig(new File(itemsDir, "grimoires.json"), MagicItemConfig.class, createDefaultGrimoireConfig());
        
        logger.at(Level.INFO).log("MagicConfigService: Configuration loaded successfully");
    }

    /**
     * Load a single config file with default fallback
     */
    private <T> T loadConfig(File file, Class<T> clazz, T defaultConfig) {
        if (!file.exists()) {
            logger.at(Level.INFO).log("Config file not found, creating default: " + file.getName());
            saveConfigFile(file, defaultConfig);
            return defaultConfig;
        }
        
        try (FileReader reader = new FileReader(file)) {
            T config = gson.fromJson(reader, clazz);
            logger.at(Level.INFO).log("Loaded config: " + file.getName());
            return config;
        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Failed to load config: " + file.getName() + ", using defaults");
            return defaultConfig;
        }
    }

    /**
     * Save all configurations to disk
     */
    public void save() {
        logger.at(Level.INFO).log("MagicConfigService: Saving configuration...");
        
        File itemsDir = new File(configRoot, "items");
        
        saveConfigFile(new File(configRoot, "main.json"), mainConfig);
        saveConfigFile(new File(itemsDir, "wands.json"), wandConfig);
        saveConfigFile(new File(itemsDir, "grimoires.json"), grimoireConfig);
        
        logger.at(Level.INFO).log("MagicConfigService: Configuration saved successfully");
    }

    /**
     * Save a single config file
     */
    private <T> void saveConfigFile(File file, T config) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(config, writer);
            logger.at(Level.FINE).log("Saved config: " + file.getName());
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Failed to save config: " + file.getName());
        }
    }

    /**
     * Reload configuration from disk
     */
    public void reload() {
        logger.at(Level.INFO).log("MagicConfigService: Reloading configuration...");
        load();
    }

    /**
     * Create default wand configuration
     */
    private MagicItemConfig createDefaultWandConfig() {
        MagicItemConfig config = new MagicItemConfig();
        // Defaults are already set in the constructor
        return config;
    }

    /**
     * Create default grimoire configuration
     */
    private MagicItemConfig createDefaultGrimoireConfig() {
        MagicItemConfig config = new MagicItemConfig();
        // Grimoires have slightly different base stats
        return config;
    }

    // Getters

    public MagicMainConfig getMainConfig() {
        return mainConfig;
    }

    public MagicItemConfig getWandConfig() {
        return wandConfig;
    }

    public MagicItemConfig getGrimoireConfig() {
        return grimoireConfig;
    }
}
