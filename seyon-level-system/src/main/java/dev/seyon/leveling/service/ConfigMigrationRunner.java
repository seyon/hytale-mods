package dev.seyon.leveling.service;

import com.google.gson.Gson;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.leveling.config.ActionConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Runs config migrations: adds missing entries only. Existing or differing entries are never changed.
 * Tracks applied migrations in config/migrations.json. On fresh install (no categories, no actions),
 * writes all migration IDs as already applied so no migrations run.
 */
public class ConfigMigrationRunner {

    private static final String MIGRATIONS_FILE = "migrations.json";

    /** All migration IDs in order. New ones go at the end. */
    private static final List<String> ALL_MIGRATION_IDS = Arrays.asList(
        "add_explore_steps",
        "add_discover_zone"
    );

    /**
     * Run pending migrations. Call at start of config load, after config dirs exist.
     */
    public static void run(File configRoot, Gson gson, HytaleLogger logger) {
        File migrationsFile = new File(configRoot, MIGRATIONS_FILE);
        File categoriesDir = new File(configRoot, "categories");
        File actionsDir = new File(configRoot, "actions");

        // Fresh install: no migrations.json and no category/action files -> mark all as applied, skip running
        if (!migrationsFile.exists() && isDirEmpty(categoriesDir) && isDirEmpty(actionsDir)) {
            MigrationsData data = new MigrationsData();
            data.applied = new ArrayList<>(ALL_MIGRATION_IDS);
            saveMigrations(migrationsFile, data, gson, logger);
            logger.at(Level.INFO).log("Config migrations: fresh install, marked all " + ALL_MIGRATION_IDS.size() + " migrations as applied");
            return;
        }

        MigrationsData data = loadMigrations(migrationsFile, gson, logger);
        boolean changed = false;

        for (String id : ALL_MIGRATION_IDS) {
            if (data.applied.contains(id)) {
                continue;
            }
            try {
                runMigration(id, configRoot, gson, logger);
                data.applied.add(id);
                changed = true;
                logger.at(Level.INFO).log("Config migration applied: " + id);
            } catch (Exception e) {
                logger.at(Level.WARNING).withCause(e).log("Config migration failed: " + id);
            }
        }

        if (changed) {
            saveMigrations(migrationsFile, data, gson, logger);
        }
    }

    private static boolean isDirEmpty(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return true;
        }
        File[] jsons = dir.listFiles((d, n) -> n != null && n.endsWith(".json"));
        return jsons == null || jsons.length == 0;
    }

    private static MigrationsData loadMigrations(File file, Gson gson, HytaleLogger logger) {
        MigrationsData data = new MigrationsData();
        data.applied = new ArrayList<>();
        if (!file.exists()) {
            return data;
        }
        try (FileReader r = new FileReader(file)) {
            MigrationsData loaded = gson.fromJson(r, MigrationsData.class);
            if (loaded != null && loaded.applied != null) {
                data.applied = loaded.applied;
            }
        } catch (Exception e) {
            logger.at(Level.WARNING).withCause(e).log("Could not load " + MIGRATIONS_FILE + ", starting with empty applied list");
        }
        return data;
    }

    private static void saveMigrations(File file, MigrationsData data, Gson gson, HytaleLogger logger) {
        try (FileWriter w = new FileWriter(file)) {
            gson.toJson(data, w);
        } catch (IOException e) {
            logger.at(Level.WARNING).withCause(e).log("Could not save " + MIGRATIONS_FILE);
        }
    }

    private static void runMigration(String id, File configRoot, Gson gson, HytaleLogger logger) {
        switch (id) {
            case "add_explore_steps" -> addActionIfMissing(configRoot, gson, "exploration", "explore_steps", 2.0);
            case "add_discover_zone" -> addActionIfMissing(configRoot, gson, "exploration", "discover_zone", 15.0);
            default -> logger.at(Level.WARNING).log("Unknown migration id: " + id);
        }
    }

    /**
     * Add an action to actions/{category}.json if the action_id is missing. Idempotent.
     * If the file does not exist, we do nothing (DefaultConfigCreator or existing flow will create it).
     */
    private static void addActionIfMissing(File configRoot, Gson gson, String category, String actionId, double exp) {
        File f = new File(new File(configRoot, "actions"), category + ".json");
        if (!f.exists()) {
            return;
        }
        ActionConfig cfg;
        try (FileReader r = new FileReader(f)) {
            cfg = gson.fromJson(r, ActionConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read " + f.getName(), e);
        }
        if (cfg == null || cfg.getActions() == null) {
            return;
        }
        boolean has = cfg.getActions().stream().anyMatch(a -> actionId.equals(a != null ? a.getActionId() : null));
        if (has) {
            return;
        }
        ActionConfig.ActionMapping m = new ActionConfig.ActionMapping();
        m.setActionId(actionId);
        m.setExp(exp);
        cfg.getActions().add(m);
        try (FileWriter w = new FileWriter(f)) {
            gson.toJson(cfg, w);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write " + f.getName(), e);
        }
    }

    @SuppressWarnings("unused")
    private static class MigrationsData {
        public List<String> applied;
    }
}
