package dev.seyon.leveling.service;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.seyon.leveling.config.ActionConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Service for managing action-to-EXP mappings
 */
public class ActionRegistryService {

    private final HytaleLogger logger;
    private final Map<String, ActionMapping> actionMap;

    public ActionRegistryService(HytaleLogger logger) {
        this.logger = logger;
        this.actionMap = new HashMap<>();
    }

    /**
     * Load actions from config service
     */
    public void loadActions(LevelSystemConfigService configService) {
        actionMap.clear();
        
        for (ActionConfig actionConfig : configService.getActionConfigs()) {
            String category = actionConfig.getCategory();
            for (ActionConfig.ActionMapping mapping : actionConfig.getActions()) {
                registerAction(mapping.getActionId(), category, mapping.getExp(), mapping.getDifficultyFactor());
            }
        }
        
        logger.at(Level.INFO).log("Loaded " + actionMap.size() + " action mappings");
    }

    /**
     * Register an action with EXP reward (difficulty factor 1.0).
     */
    public void registerAction(String actionId, String categoryId, double exp) {
        registerAction(actionId, categoryId, exp, 1.0);
    }

    /**
     * Register an action with EXP reward and difficulty factor.
     */
    public void registerAction(String actionId, String categoryId, double exp, double difficultyFactor) {
        if (actionId == null || categoryId == null) {
            logger.at(Level.WARNING).log("Attempted to register invalid action");
            return;
        }
        double factor = difficultyFactor <= 0 ? 1.0 : difficultyFactor;
        actionMap.put(actionId, new ActionMapping(categoryId, exp, factor));
        logger.at(Level.FINE).log("Registered action: " + actionId + " -> " + categoryId + " (" + exp + " EXP, difficulty=" + factor + ")");
    }

    /**
     * Get action mapping
     */
    public ActionMapping getActionMapping(String actionId) {
        return actionMap.get(actionId);
    }

    /**
     * Check if action is registered
     */
    public boolean hasAction(String actionId) {
        return actionMap.containsKey(actionId);
    }

    /**
     * Get all registered action IDs
     */
    public java.util.Set<String> getRegisteredActions() {
        return actionMap.keySet();
    }

    /**
     * Action mapping data class
     */
    public static class ActionMapping {
        private final String categoryId;
        private final double exp;
        private final double difficultyFactor;

        public ActionMapping(String categoryId, double exp, double difficultyFactor) {
            this.categoryId = categoryId;
            this.exp = exp;
            this.difficultyFactor = difficultyFactor <= 0 ? 1.0 : difficultyFactor;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public double getExp() {
            return exp;
        }

        /** EXP multiplier for harder blocks (e.g. special trees). */
        public double getDifficultyFactor() {
            return difficultyFactor;
        }
    }
}
