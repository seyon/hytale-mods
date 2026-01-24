package dev.seyon.leveling.api;

import dev.seyon.leveling.config.LevelSystemCategory;

import java.util.UUID;

/**
 * Public API for other mods to interact with the Level System
 */
public interface LevelSystemAPI {
    
    /**
     * Register a new category
     * @param category The category to register
     */
    void registerCategory(LevelSystemCategory category);
    
    /**
     * Register an action that grants experience
     * @param actionId Unique identifier for the action (e.g., "break_diamond_ore")
     * @param categoryId Category to grant EXP in
     * @param exp Amount of EXP to grant
     */
    void registerAction(String actionId, String categoryId, double exp);
    
    /**
     * Grant experience to a player
     * @param playerId Player UUID
     * @param categoryId Category ID
     * @param amount Amount of EXP to grant
     */
    void grantExperience(UUID playerId, String categoryId, double amount);
    
    /**
     * Get player's level in a category
     * @param playerId Player UUID
     * @param categoryId Category ID
     * @return Current level
     */
    int getPlayerLevel(UUID playerId, String categoryId);
    
    /**
     * Check if player has a specific skill
     * @param playerId Player UUID
     * @param categoryId Category ID
     * @param skillId Skill ID
     * @return true if player has the skill
     */
    boolean hasSkill(UUID playerId, String categoryId, String skillId);
    
    /**
     * Get the value of a specific modifier for a player
     * @param playerId Player UUID
     * @param modifierId Modifier ID (e.g., "max_health", "damage")
     * @return Modifier value
     */
    double getModifierValue(UUID playerId, String modifierId);
    
    /**
     * Check if a category exists
     * @param categoryId Category ID
     * @return true if category exists
     */
    boolean hasCategory(String categoryId);
}
