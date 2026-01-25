package dev.seyon.leveling.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.config.SkillConfig;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import dev.seyon.core.PlayerUtils;

import java.awt.Color;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Service for managing skills
 */
public class SkillService {

    private final HytaleLogger logger;
    private final CategoryService categoryService;
    private final LevelSystemDataService dataService;

    public SkillService(HytaleLogger logger, CategoryService categoryService, LevelSystemDataService dataService) {
        this.logger = logger;
        this.categoryService = categoryService;
        this.dataService = dataService;
    }

    /**
     * Check if player can activate a skill
     */
    public boolean canActivateSkill(Player player, String categoryId, String skillId) {
        return canActivateSkill(PlayerUtils.getPlayerUUID(player), categoryId, skillId);
    }

    /**
     * Check if player can activate a skill
     */
    public boolean canActivateSkill(UUID playerId, String categoryId, String skillId) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return false;
        }
        
        SkillConfig skill = category.getSkill(skillId);
        if (skill == null) {
            return false;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        
        // Check skill points
        int availablePoints = data.getAvailableSkillPoints(categoryId);
        if (availablePoints < skill.getCost()) {
            return false;
        }
        
        // Check max points
        int currentLevel = data.getSkillLevel(categoryId, skillId);
        if (currentLevel >= skill.getMaxPoints()) {
            return false;
        }
        
        return true;
    }

    /**
     * Activate or upgrade a skill
     */
    public boolean activateSkill(Player player, String categoryId, String skillId) {
        if (player == null) {
            return false;
        }
        
        return activateSkill(PlayerUtils.getPlayerUUID(player), categoryId, skillId, player);
    }

    /**
     * Activate or upgrade a skill
     */
    public boolean activateSkill(UUID playerId, String categoryId, String skillId, Player player) {
        if (!canActivateSkill(playerId, categoryId, skillId)) {
            return false;
        }
        
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        SkillConfig skill = category.getSkill(skillId);
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        
        // Spend skill points
        if (!data.spendSkillPoints(categoryId, skill.getCost())) {
            return false;
        }
        
        // Increase skill level
        int oldLevel = data.getSkillLevel(categoryId, skillId);
        int newLevel = oldLevel + 1;
        data.setSkillLevel(categoryId, skillId, newLevel);
        
        // Save data
        dataService.savePlayerData(playerId, data);
        
        // Send message
        if (player != null) {
            if (oldLevel == 0) {
                player.sendMessage(Message.join(
                    Message.raw("✨ Skill Unlocked! ").color(Color.GREEN).bold(true),
                    Message.raw(skill.getName()).color(Color.CYAN)
                ));
            } else {
                player.sendMessage(Message.join(
                    Message.raw("⬆ Skill Upgraded! ").color(Color.GREEN).bold(true),
                    Message.raw(skill.getName() + " ").color(Color.CYAN),
                    Message.raw(oldLevel + " → " + newLevel).color(Color.YELLOW)
                ));
            }
        }
        
        logger.at(Level.INFO).log("Player " + playerId + " activated skill " + skillId + " in " + categoryId + " (level " + newLevel + ")");
        return true;
    }

    /**
     * Deactivate a skill (for respec)
     */
    public boolean deactivateSkill(Player player, String categoryId, String skillId) {
        return deactivateSkill(PlayerUtils.getPlayerUUID(player), categoryId, skillId, player);
    }

    /**
     * Deactivate a skill (for respec)
     */
    public boolean deactivateSkill(UUID playerId, String categoryId, String skillId, Player player) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return false;
        }
        
        SkillConfig skill = category.getSkill(skillId);
        if (skill == null) {
            return false;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        int currentLevel = data.getSkillLevel(categoryId, skillId);
        
        if (currentLevel <= 0) {
            return false; // Skill not active
        }
        
        // Remove skill
        data.setSkillLevel(categoryId, skillId, 0);
        
        // Refund skill points
        int pointsToRefund = skill.getCost() * currentLevel;
        data.addSkillPoints(categoryId, pointsToRefund);
        
        // Save data
        dataService.savePlayerData(playerId, data);
        
        // Send message
        if (player != null) {
            player.sendMessage(Message.join(
                Message.raw("Skill Reset: ").color(Color.ORANGE),
                Message.raw(skill.getName()).color(Color.CYAN),
                Message.raw(" (+" + pointsToRefund + " skill points refunded)").color(Color.GREEN)
            ));
        }
        
        logger.at(Level.INFO).log("Player " + playerId + " deactivated skill " + skillId + " in " + categoryId);
        return true;
    }

    /**
     * Reset all skills in a category
     */
    public void resetAllSkills(UUID playerId, String categoryId, Player player) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        int totalRefund = 0;
        
        // Calculate refund
        for (SkillConfig skill : category.getSkills()) {
            int level = data.getSkillLevel(categoryId, skill.getId());
            if (level > 0) {
                totalRefund += skill.getCost() * level;
                data.setSkillLevel(categoryId, skill.getId(), 0);
            }
        }
        
        // Add refund
        data.addSkillPoints(categoryId, totalRefund);
        
        // Save data
        dataService.savePlayerData(playerId, data);
        
        // Send message
        if (player != null) {
            player.sendMessage(Message.join(
                Message.raw("All skills reset for ").color(Color.ORANGE),
                Message.raw(category.getDisplayName()).color(Color.CYAN),
                Message.raw(" (+" + totalRefund + " skill points refunded)").color(Color.GREEN)
            ));
        }
        
        logger.at(Level.INFO).log("Player " + playerId + " reset all skills in " + categoryId);
    }

    /**
     * Get skill level for a player
     */
    public int getSkillLevel(UUID playerId, String categoryId, String skillId) {
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        return data.getSkillLevel(categoryId, skillId);
    }

    /**
     * Check if player has a skill
     */
    public boolean hasSkill(UUID playerId, String categoryId, String skillId) {
        return getSkillLevel(playerId, categoryId, skillId) > 0;
    }
}
