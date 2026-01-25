package dev.seyon.leveling.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import dev.seyon.core.PlayerUtils;

import java.awt.Color;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Service for managing experience and Level System
 */
public class ExperienceService {

    private final HytaleLogger logger;
    private final CategoryService categoryService;
    private final LevelSystemDataService dataService;
    private final LevelSystemConfigService configService;

    public ExperienceService(HytaleLogger logger, CategoryService categoryService, 
                           LevelSystemDataService dataService, LevelSystemConfigService configService) {
        this.logger = logger;
        this.categoryService = categoryService;
        this.dataService = dataService;
        this.configService = configService;
    }

    /**
     * Grant experience to a player in a category
     */
    public void grantExp(Player player, String categoryId, double amount) {
        if (player == null || categoryId == null || amount <= 0) {
            return;
        }
        
        grantExp(PlayerUtils.getPlayerUUID(player), categoryId, amount, player);
    }

    /**
     * Grant experience to a player in a category
     */
    public void grantExp(UUID playerId, String categoryId, double amount, Player player) {
        if (!categoryService.hasCategory(categoryId)) {
            logger.at(Level.WARNING).log("Attempted to grant EXP for unknown category: " + categoryId);
            return;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getOrCreateCategoryProgress(categoryId);
        
        // Check if player can gain EXP
        if (!progress.isCanGainExp()) {
            if (player != null) {
                player.sendMessage(Message.raw("You need to complete a quest to continue leveling " + categoryId + "!").color(Color.ORANGE));
            }
            return;
        }
        
        // Check max level
        int maxLevel = configService.getMainConfig().getGlobalSettings().getMaxLevel();
        if (progress.getCurrentLevel() >= maxLevel) {
            return; // Max level reached
        }
        
        // Add EXP
        boolean levelUp = progress.addExp(amount);
        
        // Save data
        dataService.savePlayerData(playerId, data);
        
        // Handle level up
        if (levelUp && player != null) {
            player.sendMessage(Message.join(
                Message.raw("Level Up! ").color(Color.ORANGE).bold(true),
                Message.raw(categoryId + " is now ready to level up to ").color(Color.YELLOW),
                Message.raw(String.valueOf(progress.getCurrentLevel() + 1)).color(Color.GREEN).bold(true)
            ));
        }
    }

    /**
     * Check if player can gain EXP in a category
     */
    public boolean canGainExp(Player player, String categoryId) {
        return canGainExp(PlayerUtils.getPlayerUUID(player), categoryId);
    }

    /**
     * Check if player can gain EXP in a category
     */
    public boolean canGainExp(UUID playerId, String categoryId) {
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getOrCreateCategoryProgress(categoryId);
        return progress.isCanGainExp();
    }

    /**
     * Process a level up for a player
     */
    public void processLevelUp(Player player, String categoryId) {
        if (player == null || categoryId == null) {
            return;
        }
        
        processLevelUp(PlayerUtils.getPlayerUUID(player), categoryId, player);
    }

    /**
     * Process a level up for a player
     */
    public void processLevelUp(UUID playerId, String categoryId, Player player) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getOrCreateCategoryProgress(categoryId);
        
        if (progress.getPendingLevelUps() <= 0) {
            return; // No pending level ups
        }
        
        // Process level up
        int oldLevel = progress.getCurrentLevel();
        progress.levelUp();
        int newLevel = progress.getCurrentLevel();
        
        // Grant skill points
        int skillPoints = configService.getMainConfig().getGlobalSettings().getSkillPointsPerLevel();
        data.addSkillPoints(categoryId, skillPoints);
        
        // Calculate new EXP requirement
        double newExpRequired = category.getExpCurve().calculateExpForLevel(newLevel);
        progress.setExpForNextLevel(newExpRequired);
        
        // Check if quest is required at this level
        if (category.hasQuestAtLevel(newLevel)) {
            progress.setCanGainExp(false);
            if (player != null) {
                player.sendMessage(Message.join(
                    Message.raw("Quest Required! ").color(Color.RED).bold(true),
                    Message.raw("Complete the milestone quest to continue leveling.").color(Color.YELLOW)
                ));
            }
        }
        
        // Save data
        dataService.savePlayerData(playerId, data);
        
        // Send message
        if (player != null) {
            player.sendMessage(Message.join(
                Message.raw("✨ Level Up! ").color(Color.ORANGE).bold(true),
                Message.raw(category.getDisplayName() + " ").color(Color.YELLOW),
                Message.raw(oldLevel + " → " + newLevel).color(Color.GREEN).bold(true)
            ));
            
            if (skillPoints > 0) {
                player.sendMessage(Message.join(
                    Message.raw("+ ").color(Color.GREEN),
                    Message.raw(String.valueOf(skillPoints)).color(Color.CYAN).bold(true),
                    Message.raw(" Skill Point" + (skillPoints > 1 ? "s" : "")).color(Color.CYAN)
                ));
            }
        }
        
        logger.at(Level.INFO).log("Player " + playerId + " leveled up " + categoryId + " to level " + newLevel);
    }

    /**
     * Calculate EXP required for a level in a category
     */
    public double calculateExpForLevel(String categoryId, int level) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return 100.0; // Default
        }
        
        return category.getExpCurve().calculateExpForLevel(level);
    }

    /**
     * Get player's level in a category
     */
    public int getPlayerLevel(UUID playerId, String categoryId) {
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getCategoryProgress().get(categoryId);
        return progress != null ? progress.getCurrentLevel() : 1;
    }

    /**
     * Set player's level in a category (admin command)
     */
    public void setPlayerLevel(UUID playerId, String categoryId, int level, Player player) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getOrCreateCategoryProgress(categoryId);
        
        progress.setCurrentLevel(level);
        progress.setCurrentExp(0);
        progress.setExpForNextLevel(category.getExpCurve().calculateExpForLevel(level));
        progress.setPendingLevelUps(0);
        progress.setCanGainExp(true);
        
        dataService.savePlayerData(playerId, data);
        
        if (player != null) {
            player.sendMessage(Message.join(
                Message.raw("Level set to ").color(Color.GREEN),
                Message.raw(String.valueOf(level)).color(Color.CYAN).bold(true),
                Message.raw(" for category ").color(Color.GREEN),
                Message.raw(categoryId).color(Color.YELLOW)
            ));
        }
    }
}
