package dev.seyon.leveling.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.seyon.leveling.config.LevelBonusConfig;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.config.SkillConfig;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import dev.seyon.core.PlayerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Service for calculating and applying modifiers
 */
public class ModifierService {

    private final HytaleLogger logger;
    private final CategoryService categoryService;
    private final LevelSystemDataService dataService;
    private final SkillService skillService;

    public ModifierService(HytaleLogger logger, CategoryService categoryService, 
                         LevelSystemDataService dataService, SkillService skillService) {
        this.logger = logger;
        this.categoryService = categoryService;
        this.dataService = dataService;
        this.skillService = skillService;
    }

    /**
     * Calculate all modifiers for a player
     * @return Map of modifier_id -> total value
     */
    public Map<String, Double> calculateGlobalModifiers(Player player) {
        return calculateGlobalModifiers(PlayerUtils.getPlayerUUID(player));
    }

    /**
     * Calculate all modifiers for a player
     * @return Map of modifier_id -> total value
     */
    public Map<String, Double> calculateGlobalModifiers(UUID playerId) {
        Map<String, Double> modifiers = new HashMap<>();
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        
        // Iterate through all categories
        for (LevelSystemCategory category : categoryService.getAllCategories()) {
            CategoryProgress progress = data.getCategoryProgress().get(category.getId());
            if (progress == null) {
                continue;
            }
            
            int level = progress.getCurrentLevel();
            
            // Add level bonuses
            for (int i = 1; i <= level; i++) {
                LevelBonusConfig bonus = category.getLevelBonus(i);
                if (bonus != null) {
                    for (Map.Entry<String, Double> entry : bonus.getModifiers().entrySet()) {
                        modifiers.merge(entry.getKey(), entry.getValue(), Double::sum);
                    }
                }
            }
            
            // Add skill bonuses
            for (SkillConfig skill : category.getSkills()) {
                int skillLevel = data.getSkillLevel(category.getId(), skill.getId());
                if (skillLevel > 0) {
                    for (Map.Entry<String, Double> entry : skill.getEffects().entrySet()) {
                        double totalEffect = entry.getValue() * skillLevel;
                        modifiers.merge(entry.getKey(), totalEffect, Double::sum);
                    }
                }
            }
        }
        
        return modifiers;
    }

    /**
     * Apply modifiers to a player
     * Note: This is a placeholder. Actual implementation depends on Hytale's attribute system
     */
    public void applyModifiers(Player player) {
        Map<String, Double> modifiers = calculateGlobalModifiers(player);
        
        // Log modifiers for debugging
        logger.at(Level.FINE).log("Applying modifiers to player " + PlayerUtils.getPlayerUUID(player) + ": " + modifiers);
        
        // TODO: Apply modifiers to player attributes
        // This requires integration with Hytale's attribute system
        // Example pseudo-code:
        // player.getAttribute("max_health").addModifier("leveling_health", modifiers.getOrDefault("max_health", 0.0));
        // player.getAttribute("damage").addModifier("leveling_damage", modifiers.getOrDefault("damage", 0.0));
        // etc.
    }

    /**
     * Get a specific modifier value for a player
     */
    public double getModifierValue(UUID playerId, String modifierId) {
        Map<String, Double> modifiers = calculateGlobalModifiers(playerId);
        return modifiers.getOrDefault(modifierId, 0.0);
    }

    /**
     * Get modifiers for a specific category only
     */
    public Map<String, Double> calculateCategoryModifiers(UUID playerId, String categoryId) {
        Map<String, Double> modifiers = new HashMap<>();
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return modifiers;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getCategoryProgress().get(categoryId);
        if (progress == null) {
            return modifiers;
        }
        
        int level = progress.getCurrentLevel();
        
        // Add level bonuses
        for (int i = 1; i <= level; i++) {
            LevelBonusConfig bonus = category.getLevelBonus(i);
            if (bonus != null) {
                for (Map.Entry<String, Double> entry : bonus.getModifiers().entrySet()) {
                    modifiers.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
        }
        
        // Add skill bonuses
        for (SkillConfig skill : category.getSkills()) {
            int skillLevel = data.getSkillLevel(categoryId, skill.getId());
            if (skillLevel > 0) {
                for (Map.Entry<String, Double> entry : skill.getEffects().entrySet()) {
                    double totalEffect = entry.getValue() * skillLevel;
                    modifiers.merge(entry.getKey(), totalEffect, Double::sum);
                }
            }
        }
        
        return modifiers;
    }
}
