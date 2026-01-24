package dev.seyon.leveling.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.leveling.config.ActionConfig;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.config.LevelSystemMainConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Service for managing Level System configuration
 */
public class LevelSystemConfigService {

    private final HytaleLogger logger;
    private final Gson gson;
    private final File configRoot;

    private LevelSystemMainConfig mainConfig;
    private List<LevelSystemCategory> categories;
    private List<ActionConfig> actionConfigs;

    public LevelSystemConfigService(HytaleLogger logger) {
        this.logger = logger;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configRoot = new File("SeyonLevelSystem/config");
        this.categories = new ArrayList<>();
        this.actionConfigs = new ArrayList<>();
    }

    /**
     * Load configuration from disk
     */
    public void load() {
        logger.at(Level.INFO).log("LevelSystemConfigService: Loading configuration...");
        
        // Create config directory if it doesn't exist
        if (!configRoot.exists()) {
            if (configRoot.mkdirs()) {
                logger.at(Level.INFO).log("Created config directory: " + configRoot.getAbsolutePath());
            }
        }
        
        // Create subdirectories
        File categoriesDir = new File(configRoot, "categories");
        File actionsDir = new File(configRoot, "actions");
        if (!categoriesDir.exists()) categoriesDir.mkdirs();
        if (!actionsDir.exists()) actionsDir.mkdirs();

        // Load main config
        mainConfig = loadConfig(new File(configRoot, "main.json"), LevelSystemMainConfig.class, new LevelSystemMainConfig());
        
        // Load categories
        loadCategories(categoriesDir);
        
        // Load actions
        loadActions(actionsDir);
        
        logger.at(Level.INFO).log("LevelSystemConfigService: Configuration loaded successfully");
    }

    /**
     * Load categories from directory
     */
    private void loadCategories(File categoriesDir) {
        categories.clear();
        
        File[] files = categoriesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null && files.length > 0) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    LevelSystemCategory category = gson.fromJson(reader, LevelSystemCategory.class);
                    if (category != null && category.getId() != null) {
                        categories.add(category);
                        logger.at(Level.INFO).log("Loaded category: " + category.getId());
                    }
                } catch (Exception e) {
                    logger.at(Level.SEVERE).withCause(e).log("Failed to load category: " + file.getName());
                }
            }
        } else {
            logger.at(Level.INFO).log("No category files found, creating defaults...");
            createDefaultCategories(categoriesDir);
        }
    }

    /**
     * Load actions from directory
     */
    private void loadActions(File actionsDir) {
        actionConfigs.clear();
        
        File[] files = actionsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null && files.length > 0) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    ActionConfig actionConfig = gson.fromJson(reader, ActionConfig.class);
                    if (actionConfig != null && actionConfig.getCategory() != null) {
                        actionConfigs.add(actionConfig);
                        logger.at(Level.INFO).log("Loaded actions for category: " + actionConfig.getCategory());
                    }
                } catch (Exception e) {
                    logger.at(Level.SEVERE).withCause(e).log("Failed to load actions: " + file.getName());
                }
            }
        } else {
            logger.at(Level.INFO).log("No action files found, will be created with categories");
        }
    }

    /**
     * Create default categories
     */
    private void createDefaultCategories(File categoriesDir) {
        logger.at(Level.INFO).log("Creating default categories...");
        DefaultConfigCreator creator = new DefaultConfigCreator();
        creator.createDefaultConfigs(configRoot);
        
        // Reload categories
        loadCategories(categoriesDir);
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
        logger.at(Level.INFO).log("LevelSystemConfigService: Saving configuration...");
        
        File categoriesDir = new File(configRoot, "categories");
        File actionsDir = new File(configRoot, "actions");
        
        saveConfigFile(new File(configRoot, "main.json"), mainConfig);
        
        // Save categories
        for (LevelSystemCategory category : categories) {
            File categoryFile = new File(categoriesDir, category.getId() + ".json");
            saveConfigFile(categoryFile, category);
        }
        
        // Save actions
        for (ActionConfig actionConfig : actionConfigs) {
            File actionFile = new File(actionsDir, actionConfig.getCategory() + ".json");
            saveConfigFile(actionFile, actionConfig);
        }
        
        logger.at(Level.INFO).log("LevelSystemConfigService: Configuration saved successfully");
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
        logger.at(Level.INFO).log("LevelSystemConfigService: Reloading configuration...");
        load();
    }

    /**
     * Add a category (for API)
     */
    public void addCategory(LevelSystemCategory category) {
        categories.add(category);
        File categoriesDir = new File(configRoot, "categories");
        File categoryFile = new File(categoriesDir, category.getId() + ".json");
        saveConfigFile(categoryFile, category);
    }

    // Getters

    public LevelSystemMainConfig getMainConfig() {
        return mainConfig;
    }

    public List<LevelSystemCategory> getCategories() {
        return categories;
    }

    public List<ActionConfig> getActionConfigs() {
        return actionConfigs;
    }

    public File getConfigRoot() {
        return configRoot;
    }
}
