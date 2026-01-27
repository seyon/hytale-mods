package dev.seyon.leveling.service;

import dev.seyon.leveling.config.LevelBonusConfig;
import dev.seyon.leveling.config.LevelSystemCategory;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for displaying level effects in the GUI
 * Provides methods to calculate and format level bonuses
 */
public class LevelEffectsDisplayHelper {

    // Map of modifier IDs to human-readable names
    private static final Map<String, String> MODIFIER_NAMES = new HashMap<>();
    
    static {
        // Combat & Health
        MODIFIER_NAMES.put("max_health", "Max Health");
        MODIFIER_NAMES.put("health_regen", "Health Regen");
        MODIFIER_NAMES.put("damage", "Damage");
        MODIFIER_NAMES.put("attack_speed", "Attack Speed");
        MODIFIER_NAMES.put("critical_chance", "Critical Chance");
        MODIFIER_NAMES.put("critical_damage", "Critical Damage");
        
        // Resources
        MODIFIER_NAMES.put("max_mana", "Max Mana");
        MODIFIER_NAMES.put("mana_regen", "Mana Regen");
        MODIFIER_NAMES.put("max_stamina", "Max Stamina");
        MODIFIER_NAMES.put("stamina_regen", "Stamina Regen");
        
        // Movement
        MODIFIER_NAMES.put("movement_speed", "Movement Speed");
        MODIFIER_NAMES.put("sprint_speed", "Sprint Speed");
        
        // Mining & Gathering
        MODIFIER_NAMES.put("mining_speed", "Mining Speed");
        MODIFIER_NAMES.put("mining_speed_ores", "Ore Mining Speed");
        MODIFIER_NAMES.put("mining_fortune", "Mining Fortune");
        MODIFIER_NAMES.put("woodcutting_speed", "Woodcutting Speed");
        MODIFIER_NAMES.put("woodcutting_fortune", "Woodcutting Fortune");
        
        // Farming
        MODIFIER_NAMES.put("farming_speed", "Farming Speed");
        MODIFIER_NAMES.put("farming_yield", "Farming Yield");
        
        // Crafting
        MODIFIER_NAMES.put("crafting_speed", "Crafting Speed");
        MODIFIER_NAMES.put("crafting_yield", "Crafting Yield");
        
        // Magic (from Seyon Arcane Arts)
        MODIFIER_NAMES.put("spell_power", "Spell Power");
        MODIFIER_NAMES.put("spell_damage", "Spell Damage");
        MODIFIER_NAMES.put("mana_cost_reduction", "Mana Cost Reduction");
        MODIFIER_NAMES.put("spell_cooldown_reduction", "Cooldown Reduction");
        MODIFIER_NAMES.put("elemental_damage", "Elemental Damage");
        
        // Exploration
        MODIFIER_NAMES.put("discovery_range", "Discovery Range");
        MODIFIER_NAMES.put("exp_bonus", "EXP Bonus");
    }

    /**
     * Get level bonus modifiers for a specific level only
     * @param category The category to check
     * @param level The level to get bonuses for
     * @return Map of modifier_id -> value for this level only
     */
    public static Map<String, Double> getLevelBonusAtLevel(LevelSystemCategory category, int level) {
        Map<String, Double> modifiers = new HashMap<>();
        
        if (category == null || level < 1) {
            return modifiers;
        }
        
        LevelBonusConfig bonus = category.getLevelBonus(level);
        if (bonus != null && bonus.getModifiers() != null) {
            modifiers.putAll(bonus.getModifiers());
        }
        
        return modifiers;
    }

    /**
     * Get cumulative level bonus modifiers from level 1 up to the specified level
     * Does NOT include skill bonuses, only level bonuses
     * @param category The category to check
     * @param level The level to sum bonuses up to
     * @return Map of modifier_id -> cumulative value
     */
    public static Map<String, Double> getCumulativeLevelBonusesUpTo(LevelSystemCategory category, int level) {
        Map<String, Double> modifiers = new HashMap<>();
        
        if (category == null || level < 1) {
            return modifiers;
        }
        
        // Sum up all level bonuses from 1 to level
        for (int i = 1; i <= level; i++) {
            LevelBonusConfig bonus = category.getLevelBonus(i);
            if (bonus != null && bonus.getModifiers() != null) {
                for (Map.Entry<String, Double> entry : bonus.getModifiers().entrySet()) {
                    modifiers.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
        }
        
        return modifiers;
    }

    /**
     * Format modifiers for display in the GUI
     * Converts technical IDs to human-readable names and formats values
     * @param modifiers Map of modifier_id -> value
     * @return Formatted string for display (e.g. "Max Health +20, Mining Speed +5%")
     */
    public static String formatModifiersForDisplay(Map<String, Double> modifiers) {
        if (modifiers == null || modifiers.isEmpty()) {
            return "—";
        }
        
        StringBuilder result = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, Double> entry : modifiers.entrySet()) {
            if (!first) {
                result.append(", ");
            }
            first = false;
            
            String modifierId = entry.getKey();
            double value = entry.getValue();
            
            // Get human-readable name (or use raw ID if not in map)
            String displayName = MODIFIER_NAMES.getOrDefault(modifierId, modifierId);
            
            // Format value based on modifier type
            String formattedValue;
            if (isPercentageModifier(modifierId)) {
                // Display as percentage (e.g. 0.05 -> +5%)
                formattedValue = String.format("%+.0f%%", value * 100);
            } else {
                // Display as flat value (e.g. 10 -> +10)
                formattedValue = String.format("%+.0f", value);
            }
            
            result.append(displayName).append(" ").append(formattedValue);
        }
        
        return result.toString();
    }

    /**
     * Check if a modifier should be displayed as a percentage
     * @param modifierId The modifier ID to check
     * @return true if this modifier is a percentage-based modifier
     */
    private static boolean isPercentageModifier(String modifierId) {
        return modifierId.contains("speed") 
            || modifierId.contains("chance") 
            || modifierId.contains("fortune")
            || modifierId.contains("reduction")
            || modifierId.contains("bonus")
            || modifierId.contains("yield");
    }

    /**
     * Add a custom modifier name mapping
     * Allows mods to register their own modifier display names
     * @param modifierId The technical modifier ID
     * @param displayName The human-readable display name
     */
    public static void registerModifierName(String modifierId, String displayName) {
        MODIFIER_NAMES.put(modifierId, displayName);
    }
}
