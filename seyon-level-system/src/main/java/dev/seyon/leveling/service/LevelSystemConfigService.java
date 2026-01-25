package dev.seyon.leveling.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.leveling.config.ActionConfig;
import dev.seyon.leveling.config.FarmingHarvestOverrideConfig;
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
 * Service for managing Level System configuration. All configs are defined as Java defaults in
 * DefaultConfigProvider. On load: Java defaults are merged with files (files have higher priority),
 * then the merged result is saved. Replaces the previous migrations.
 */
public class LevelSystemConfigService {

    private final HytaleLogger logger;
    private final Gson gson;
    private final File configRoot;
    private final DefaultConfigProvider defaultProvider;

    private LevelSystemMainConfig mainConfig;
    private List<LevelSystemCategory> categories;
    private List<ActionConfig> actionConfigs;
    private FarmingHarvestOverrideConfig farmingHarvestOverrideConfig;

    public LevelSystemConfigService(HytaleLogger logger) {
        this.logger = logger;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configRoot = new File("SeyonLevelSystem/config");
        this.defaultProvider = new DefaultConfigProvider();
        this.categories = new ArrayList<>();
        this.actionConfigs = new ArrayList<>();
    }

    /**
     * Load: merge Java defaults with files (file wins), apply farming overrides, then save merged configs.
     */
    public void load() {
        logger.at(Level.INFO).log("LevelSystemConfigService: Loading configuration...");

        if (!configRoot.exists()) configRoot.mkdirs();
        File categoriesDir = new File(configRoot, "categories");
        File actionsDir = new File(configRoot, "actions");
        if (!categoriesDir.exists()) categoriesDir.mkdirs();
        if (!actionsDir.exists()) actionsDir.mkdirs();

        // Main: default + merge from main.json
        mainConfig = defaultProvider.getDefaultMainConfig();
        mergeFromFile(new File(configRoot, "main.json"), LevelSystemMainConfig.class, mainConfig, "main.json");

        // Categories: for each default, merge from file; then add custom category files not in defaults
        categories.clear();
        java.util.Set<String> defaultIds = new java.util.HashSet<>();
        for (LevelSystemCategory d : defaultProvider.getDefaultCategories()) {
            defaultIds.add(d.getId());
            LevelSystemCategory c = d;
            mergeFromFile(new File(categoriesDir, d.getId() + ".json"), LevelSystemCategory.class, c, "categories/" + d.getId() + ".json");
            categories.add(c);
            logger.at(Level.INFO).log("Loaded category: " + c.getId());
        }
        File[] catFiles = categoriesDir.listFiles((dir, name) -> name != null && name.endsWith(".json"));
        if (catFiles != null) {
            for (File f : catFiles) {
                String id = f.getName().replace(".json", "");
                if (defaultIds.contains(id)) continue;
                LevelSystemCategory loaded = readFile(f, LevelSystemCategory.class);
                if (loaded != null && loaded.getId() != null) {
                    categories.add(loaded);
                    logger.at(Level.INFO).log("Loaded custom category: " + loaded.getId());
                }
            }
        }

        // Actions: for each default, merge from file; then add custom action files
        actionConfigs.clear();
        java.util.Set<String> defaultActionCats = new java.util.HashSet<>();
        for (ActionConfig d : defaultProvider.getDefaultActionConfigs()) {
            defaultActionCats.add(d.getCategory());
            ActionConfig c = d;
            mergeFromFile(new File(actionsDir, d.getCategory() + ".json"), ActionConfig.class, c, "actions/" + d.getCategory() + ".json");
            actionConfigs.add(c);
            logger.at(Level.INFO).log("Loaded actions for category: " + c.getCategory());
        }
        File[] actFiles = actionsDir.listFiles((dir, name) -> name != null && name.endsWith(".json"));
        if (actFiles != null) {
            for (File f : actFiles) {
                String cat = f.getName().replace(".json", "");
                if (defaultActionCats.contains(cat)) continue;
                ActionConfig loaded = readFile(f, ActionConfig.class);
                if (loaded != null && loaded.getCategory() != null) {
                    actionConfigs.add(loaded);
                    logger.at(Level.INFO).log("Loaded custom actions: " + loaded.getCategory());
                }
            }
        }

        // Farming harvest overrides: default (empty) + merge from farming_harvest.json
        farmingHarvestOverrideConfig = defaultProvider.getDefaultFarmingHarvestOverrides();
        mergeFromFile(new File(configRoot, "farming_harvest.json"), FarmingHarvestOverrideConfig.class, farmingHarvestOverrideConfig, "farming_harvest.json");

        // Apply overrides onto farming ActionConfig (in-memory)
        applyFarmingHarvestOverrides();

        // Save merged configs back to disk
        save();
        logger.at(Level.INFO).log("LevelSystemConfigService: Configuration loaded and saved");
    }

    private <T> void mergeFromFile(File file, Class<T> clazz, T target, String logName) {
        T loaded = readFile(file, clazz);
        if (loaded == null) return;
        if (target instanceof LevelSystemMainConfig) ((LevelSystemMainConfig) target).mergeFrom((LevelSystemMainConfig) loaded);
        else if (target instanceof LevelSystemCategory) ((LevelSystemCategory) target).mergeFrom((LevelSystemCategory) loaded);
        else if (target instanceof ActionConfig) ((ActionConfig) target).mergeFrom((ActionConfig) loaded);
        else if (target instanceof FarmingHarvestOverrideConfig) ((FarmingHarvestOverrideConfig) target).mergeFrom((FarmingHarvestOverrideConfig) loaded);
    }

    private <T> T readFile(File file, Class<T> clazz) {
        if (file == null || !file.exists()) return null;
        try (FileReader r = new FileReader(file)) {
            return gson.fromJson(r, clazz);
        } catch (Exception e) {
            logger.at(Level.WARNING).withCause(e).log("Failed to read " + file.getName());
            return null;
        }
    }

    /** Apply farmingHarvestOverrideConfig onto the farming ActionConfig. */
    private void applyFarmingHarvestOverrides() {
        ActionConfig farming = null;
        for (ActionConfig ac : actionConfigs) {
            if ("farming".equals(ac.getCategory())) { farming = ac; break; }
        }
        if (farming == null || farmingHarvestOverrideConfig == null || farmingHarvestOverrideConfig.getOverrides() == null) return;
        for (java.util.Map.Entry<String, Double> e : farmingHarvestOverrideConfig.getOverrides().entrySet()) {
            String blockId = e.getKey();
            Double exp = e.getValue();
            if (blockId == null || blockId.isEmpty() || exp == null) continue;
            String actionId = "harvest_" + blockId;
            ActionConfig.ActionMapping found = null;
            for (ActionConfig.ActionMapping m : farming.getActions()) {
                if (actionId.equals(m.getActionId())) { found = m; break; }
            }
            if (found != null) found.setExp(exp);
            else {
                ActionConfig.ActionMapping added = new ActionConfig.ActionMapping();
                added.setActionId(actionId);
                added.setExp(exp);
                farming.getActions().add(added);
            }
        }
    }

    /**
     * Save all configurations to disk (merged state).
     */
    public void save() {
        logger.at(Level.INFO).log("LevelSystemConfigService: Saving configuration...");
        File categoriesDir = new File(configRoot, "categories");
        File actionsDir = new File(configRoot, "actions");

        saveConfigFile(new File(configRoot, "main.json"), mainConfig);
        for (LevelSystemCategory c : categories) {
            saveConfigFile(new File(categoriesDir, c.getId() + ".json"), c);
        }
        for (ActionConfig a : actionConfigs) {
            saveConfigFile(new File(actionsDir, a.getCategory() + ".json"), a);
        }
        saveConfigFile(new File(configRoot, "farming_harvest.json"), farmingHarvestOverrideConfig != null ? farmingHarvestOverrideConfig : new FarmingHarvestOverrideConfig());
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
