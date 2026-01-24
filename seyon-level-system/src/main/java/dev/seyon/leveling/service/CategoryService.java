package dev.seyon.leveling.service;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.leveling.config.LevelSystemCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Service for managing categories
 */
public class CategoryService {

    private final HytaleLogger logger;
    private final Map<String, LevelSystemCategory> categoryMap;

    public CategoryService(HytaleLogger logger) {
        this.logger = logger;
        this.categoryMap = new HashMap<>();
    }

    /**
     * Load categories from config service
     */
    public void loadCategories(LevelSystemConfigService configService) {
        categoryMap.clear();
        
        for (LevelSystemCategory category : configService.getCategories()) {
            categoryMap.put(category.getId(), category);
            logger.at(Level.INFO).log("Registered category: " + category.getId() + " (" + category.getDisplayName() + ")");
        }
    }

    /**
     * Get category by ID
     */
    public LevelSystemCategory getCategory(String categoryId) {
        return categoryMap.get(categoryId);
    }

    /**
     * Get all categories
     */
    public List<LevelSystemCategory> getAllCategories() {
        return new ArrayList<>(categoryMap.values());
    }

    /**
     * Register a category (for API)
     */
    public void registerCategory(LevelSystemCategory category) {
        if (category == null || category.getId() == null) {
            logger.at(Level.WARNING).log("Attempted to register null or invalid category");
            return;
        }
        
        categoryMap.put(category.getId(), category);
        logger.at(Level.INFO).log("Registered category via API: " + category.getId());
    }

    /**
     * Check if a category exists
     */
    public boolean hasCategory(String categoryId) {
        return categoryMap.containsKey(categoryId);
    }

    /**
     * Get category IDs
     */
    public List<String> getCategoryIds() {
        return new ArrayList<>(categoryMap.keySet());
    }
}
