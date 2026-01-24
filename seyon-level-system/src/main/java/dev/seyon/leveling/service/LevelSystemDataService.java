package dev.seyon.leveling.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.leveling.model.PlayerLevelSystemData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Service for managing player Level System data
 * Handles persistence via file storage
 */
public class LevelSystemDataService {

    private final HytaleLogger logger;
    private final Gson gson;
    private final File dataRoot;
    private final Map<UUID, PlayerLevelSystemData> playerDataCache;

    public LevelSystemDataService(HytaleLogger logger) {
        this.logger = logger;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataRoot = new File("SeyonLevelSystem/playerdata");
        this.playerDataCache = new HashMap<>();
        
        // Create data directory
        if (!dataRoot.exists()) {
            dataRoot.mkdirs();
        }
    }

    /**
     * Get player data (loads from cache or disk)
     */
    public PlayerLevelSystemData getPlayerData(UUID playerId) {
        // Check cache first
        if (playerDataCache.containsKey(playerId)) {
            return playerDataCache.get(playerId);
        }
        
        // Load from disk
        PlayerLevelSystemData data = loadPlayerData(playerId);
        playerDataCache.put(playerId, data);
        return data;
    }

    /**
     * Load player data from disk
     */
    private PlayerLevelSystemData loadPlayerData(UUID playerId) {
        File playerFile = new File(dataRoot, playerId.toString() + ".json");
        
        if (!playerFile.exists()) {
            logger.at(Level.INFO).log("Creating new player data for: " + playerId);
            return new PlayerLevelSystemData(playerId);
        }
        
        try (FileReader reader = new FileReader(playerFile)) {
            PlayerLevelSystemData data = gson.fromJson(reader, PlayerLevelSystemData.class);
            if (data != null) {
                data.setPlayerId(playerId); // Ensure UUID is set
                logger.at(Level.FINE).log("Loaded player data for: " + playerId);
                return data;
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).withCause(e).log("Failed to load player data for: " + playerId);
        }
        
        return new PlayerLevelSystemData(playerId);
    }

    /**
     * Save player data to disk
     */
    public void savePlayerData(UUID playerId) {
        PlayerLevelSystemData data = playerDataCache.get(playerId);
        if (data == null) {
            logger.at(Level.WARNING).log("Attempted to save non-existent player data: " + playerId);
            return;
        }
        
        savePlayerData(playerId, data);
    }

    /**
     * Save player data to disk
     */
    public void savePlayerData(UUID playerId, PlayerLevelSystemData data) {
        File playerFile = new File(dataRoot, playerId.toString() + ".json");
        
        try (FileWriter writer = new FileWriter(playerFile)) {
            gson.toJson(data, writer);
            logger.at(Level.FINE).log("Saved player data for: " + playerId);
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Failed to save player data for: " + playerId);
        }
    }

    /**
     * Save all cached player data
     */
    public void saveAll() {
        logger.at(Level.INFO).log("Saving all player data...");
        for (Map.Entry<UUID, PlayerLevelSystemData> entry : playerDataCache.entrySet()) {
            savePlayerData(entry.getKey(), entry.getValue());
        }
        logger.at(Level.INFO).log("Saved " + playerDataCache.size() + " player data files");
    }

    /**
     * Unload player data from cache (call on disconnect)
     */
    public void unloadPlayerData(UUID playerId) {
        savePlayerData(playerId);
        playerDataCache.remove(playerId);
        logger.at(Level.FINE).log("Unloaded player data for: " + playerId);
    }

    /**
     * Initialize player with all categories
     */
    public void initializePlayerCategories(UUID playerId, CategoryService categoryService) {
        PlayerLevelSystemData data = getPlayerData(playerId);
        
        for (String categoryId : categoryService.getCategoryIds()) {
            data.initializeCategory(categoryId);
        }
        
        savePlayerData(playerId, data);
    }
}
