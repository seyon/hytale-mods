package dev.seyon.leveling.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.config.MilestoneQuestConfig;
import dev.seyon.leveling.config.QuestItemRequirement;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import dev.seyon.utils.PlayerUtils;

import java.awt.Color;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Service for managing milestone quests
 */
public class QuestService {

    private final HytaleLogger logger;
    private final CategoryService categoryService;
    private final LevelSystemDataService dataService;

    public QuestService(HytaleLogger logger, CategoryService categoryService, LevelSystemDataService dataService) {
        this.logger = logger;
        this.categoryService = categoryService;
        this.dataService = dataService;
    }

    /**
     * Check if a quest requirement is met
     */
    public boolean checkQuestRequirement(Player player, String categoryId, int level) {
        return checkQuestRequirement(PlayerUtils.getPlayerUUID(player), categoryId, level, player);
    }

    /**
     * Check if a quest requirement is met
     */
    public boolean checkQuestRequirement(UUID playerId, String categoryId, int level, Player player) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return false;
        }
        
        MilestoneQuestConfig quest = category.getMilestoneQuest(level);
        if (quest == null) {
            return true; // No quest required
        }
        
        // Check quest type
        if ("item_collection".equals(quest.getType()) || "both".equals(quest.getType())) {
            return checkItemRequirements(player, quest.getRequiredItems());
        }
        
        // For simple_talk, always return true (just needs NPC interaction)
        return true;
    }

    /**
     * Check if player has required items
     */
    private boolean checkItemRequirements(Player player, List<QuestItemRequirement> requirements) {
        if (player == null || requirements == null || requirements.isEmpty()) {
            return true;
        }
        
        // TODO: Implement item checking via player inventory
        // This requires knowing the exact item IDs and inventory API
        // Placeholder implementation:
        return true;
    }

    /**
     * Complete a quest
     */
    public boolean completeQuest(Player player, String categoryId, int level) {
        if (player == null) {
            return false;
        }
        
        return completeQuest(PlayerUtils.getPlayerUUID(player), categoryId, level, player);
    }

    /**
     * Complete a quest
     */
    public boolean completeQuest(UUID playerId, String categoryId, int level, Player player) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return false;
        }
        
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        CategoryProgress progress = data.getOrCreateCategoryProgress(categoryId);
        
        // Check if already completed
        if (data.isQuestCompleted(categoryId, level)) {
            return false;
        }
        
        // Check requirements
        if (!checkQuestRequirement(playerId, categoryId, level, player)) {
            if (player != null) {
                player.sendMessage(Message.raw("You don't have all required items!").color(Color.RED));
            }
            return false;
        }
        
        // Mark quest as completed
        data.completeQuest(categoryId, level);
        
        // Re-enable EXP gain
        progress.setCanGainExp(true);
        
        // Remove required items if applicable
        MilestoneQuestConfig quest = category.getMilestoneQuest(level);
        if (quest != null && !quest.getRequiredItems().isEmpty()) {
            removeQuestItems(player, quest.getRequiredItems());
        }
        
        // Save data
        dataService.savePlayerData(playerId, data);
        
        // Send message
        if (player != null) {
            player.sendMessage(Message.join(
                Message.raw("✅ Quest Complete! ").color(Color.GREEN).bold(true),
                Message.raw("You can now continue leveling ").color(Color.YELLOW),
                Message.raw(category.getDisplayName()).color(Color.CYAN)
            ));
        }
        
        logger.at(Level.INFO).log("Player " + playerId + " completed quest for " + categoryId + " level " + level);
        return true;
    }

    /**
     * Remove quest items from player inventory
     */
    private void removeQuestItems(Player player, List<QuestItemRequirement> requirements) {
        if (player == null || requirements == null || requirements.isEmpty()) {
            return;
        }
        
        // TODO: Implement item removal via player inventory
        // This requires knowing the exact item IDs and inventory API
        // Placeholder implementation
    }

    /**
     * Get quest for a specific level
     */
    public MilestoneQuestConfig getQuestForLevel(String categoryId, int level) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return null;
        }
        
        return category.getMilestoneQuest(level);
    }

    /**
     * Check if quest is completed
     */
    public boolean isQuestCompleted(UUID playerId, String categoryId, int level) {
        PlayerLevelSystemData data = dataService.getPlayerData(playerId);
        return data.isQuestCompleted(categoryId, level);
    }

    /**
     * Check if level requires a quest
     */
    public boolean requiresQuest(String categoryId, int level) {
        LevelSystemCategory category = categoryService.getCategory(categoryId);
        if (category == null) {
            return false;
        }
        
        return category.hasQuestAtLevel(level);
    }
}
