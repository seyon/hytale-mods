package dev.seyon.leveling.gui;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.core.PlayerUtils;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.config.SkillConfig;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import au.ellie.hyui.builders.PageBuilder;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;

/**
 * Level System GUI using HyUI
 * Displays all categories with EXP bars and skill management
 */
public class LevelSystemHyUIGui {
    
    private final PlayerRef playerRef;
    private final Store<EntityStore> store;
    private String currentCategoryView = null; // null = categories view, otherwise = skill view for category
    
    public LevelSystemHyUIGui(PlayerRef playerRef, Store<EntityStore> store) {
        this.playerRef = playerRef;
        this.store = store;
    }
    
    /**
     * Open the GUI
     */
    public void open() {
        show();
    }
    
    /**
     * Show the main categories view
     */
    private void show() {
        currentCategoryView = null;
        
        Player player = store.getComponent(playerRef.getRef(), Player.getComponentType());
        if (player == null) {
            return;
        }

        UUID playerId = PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            return;
        }
        
        PlayerLevelSystemData data = SeyonLevelSystemPlugin.getInstance().getDataService().getPlayerData(playerId);
        
        // Calculate total skill points across all categories
        int totalSkillPoints = 0;
        for (String categoryId : data.getCategoryProgress().keySet()) {
            totalSkillPoints += data.getAvailableSkillPoints(categoryId);
        }
        
        // Build HTML
        StringBuilder html = new StringBuilder();
        html.append("<div class='page-overlay'>");
        html.append("<div class='container' data-hyui-title='Level System'>");
        html.append("<div class='container-contents'>");
        
        // Header
        html.append("<div style='text-align: center; margin-bottom: 20px;'>");
        html.append("<div style='font-size: 24px; font-weight: bold; color: #FF8C00;'>Level System</div>");
        html.append("<div style='font-size: 16px; color: #FFD700; margin-top: 10px;'>");
        html.append(player.getDisplayName()).append(" - Total Skill Points: ").append(totalSkillPoints);
        html.append("</div>");
        html.append("</div>");
        
        // Categories
        for (LevelSystemCategory category : SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories()) {
            String categoryId = category.getId();
            CategoryProgress progress = data.getCategoryProgress().get(categoryId);
            
            if (progress == null) {
                continue;
            }
            
            int level = progress.getCurrentLevel();
            double exp = progress.getCurrentExp();
            double expNeeded = progress.getExpForNextLevel();
            int skillPoints = data.getAvailableSkillPoints(categoryId);
            int pendingLevelUps = progress.getPendingLevelUps();
            boolean canGainExp = progress.canGainExp();
            
            double expPercent = expNeeded > 0 ? (exp / expNeeded) * 100 : 0;
            
            // Category Card
            html.append("<div class='button' style='padding: 15px; margin: 10px 0; background-color: #2a2a2a; border-radius: 8px;'>");
            
            // Category Name and Level
            html.append("<div style='font-size: 20px; font-weight: bold; color: #00CED1; margin-bottom: 10px;'>");
            html.append(category.getDisplayName()).append(" - Level ").append(level);
            html.append("</div>");
            
            // EXP Bar
            html.append("<div style='margin-bottom: 10px;'>");
            html.append("<div style='background-color: #1a1a1a; border-radius: 5px; padding: 2px; height: 24px;'>");
            html.append("<div style='background: linear-gradient(90deg, #00FF00, #32CD32); width: ")
                .append(String.format("%.1f", expPercent))
                .append("%; height: 20px; border-radius: 3px; transition: width 0.3s;'></div>");
            html.append("</div>");
            html.append("<div style='font-size: 14px; color: #808080; margin-top: 5px;'>")
                .append(String.format("%.0f", exp)).append(" / ").append(String.format("%.0f", expNeeded)).append(" EXP")
                .append("</div>");
            html.append("</div>");
            
            // Info Row
            html.append("<div style='display: flex; justify-content: space-between; margin-bottom: 10px;'>");
            html.append("<div style='color: #FFD700;'>Skill Points: ").append(skillPoints).append("</div>");
            
            if (!canGainExp) {
                html.append("<div style='color: #FF4444; font-weight: bold;'>⚠ Level Blocked!</div>");
            } else if (pendingLevelUps > 0) {
                html.append("<div style='color: #00FF00; font-weight: bold;'>✓ ").append(pendingLevelUps).append(" Level-Ups Available!</div>");
            }
            html.append("</div>");
            
            // Buttons Row
            html.append("<div style='display: flex; gap: 10px;'>");
            
            // Level Up Button
            if (pendingLevelUps > 0) {
                html.append("<div class='button' id='levelup_").append(categoryId).append("' ")
                    .append("style='flex: 1; padding: 10px; text-align: center; background-color: #00AA00; color: white; ")
                    .append("border-radius: 5px; font-weight: bold; cursor: pointer;'>")
                    .append("⬆ Level Up (").append(pendingLevelUps).append(")")
                    .append("</div>");
            }
            
            // Manage Skills Button
            html.append("<div class='button' id='skills_").append(categoryId).append("' ")
                .append("style='flex: 1; padding: 10px; text-align: center; background-color: #4169E1; color: white; ")
                .append("border-radius: 5px; font-weight: bold; cursor: pointer;'>")
                .append("🎯 Manage Skills")
                .append("</div>");
            
            html.append("</div>");
            html.append("</div>");
        }
        
        html.append("</div></div></div>");
        
        // Build page and register event listeners
        PageBuilder builder = PageBuilder.pageForPlayer(playerRef).fromHtml(html.toString());
        
        // Add event listeners for all categories
        for (LevelSystemCategory category : SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories()) {
            String categoryId = category.getId();
            
            // Level Up button
            builder.addEventListener("levelup_" + categoryId, CustomUIEventBindingType.Activating, (ctx) -> {
                SeyonLevelSystemPlugin.getInstance().getExperienceService().processLevelUp(player, categoryId);
                show(); // Refresh GUI
            });
            
            // Manage Skills button
            builder.addEventListener("skills_" + categoryId, CustomUIEventBindingType.Activating, (ctx) -> {
                showSkills(categoryId);
            });
        }
        
        builder.open(store);
    }
    
    /**
     * Show skills for a category
     */
    private void showSkills(String categoryId) {
        currentCategoryView = categoryId;
        
        Player player = store.getComponent(playerRef.getRef(), Player.getComponentType());
        if (player == null) {
            return;
        }

        UUID playerId = PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            return;
        }
        
        PlayerLevelSystemData data = SeyonLevelSystemPlugin.getInstance().getDataService().getPlayerData(playerId);
        LevelSystemCategory category = SeyonLevelSystemPlugin.getInstance().getCategoryService().getCategory(categoryId);
        
        if (category == null) {
            show(); // Go back if category not found
            return;
        }
        
        int availableSkillPoints = data.getAvailableSkillPoints(categoryId);
        Map<String, Integer> activeSkills = data.getActiveSkills(categoryId);
        
        // Build HTML
        StringBuilder html = new StringBuilder();
        html.append("<div class='page-overlay'>");
        html.append("<div class='container' data-hyui-title='").append(category.getDisplayName()).append(" - Skills'>");
        html.append("<div class='container-contents'>");
        
        // Back Button
        html.append("<div class='button' id='back_button' ")
            .append("style='padding: 10px; margin-bottom: 20px; background-color: #555555; color: white; ")
            .append("border-radius: 5px; text-align: center; cursor: pointer;'>")
            .append("⬅ Back to Categories")
            .append("</div>");
        
        // Header with Available Skill Points
        html.append("<div style='text-align: center; margin-bottom: 20px;'>");
        html.append("<div style='font-size: 24px; font-weight: bold; color: #00CED1;'>").append(category.getDisplayName()).append("</div>");
        html.append("<div style='font-size: 18px; color: #FFD700; margin-top: 10px;'>Available Skill Points: ").append(availableSkillPoints).append("</div>");
        html.append("</div>");
        
        // Skills
        if (category.getSkills() == null || category.getSkills().isEmpty()) {
            html.append("<div style='text-align: center; color: #808080; font-size: 16px; padding: 40px;'>");
            html.append("No skills available for this category yet.");
            html.append("</div>");
        } else {
            for (SkillConfig skill : category.getSkills()) {
                String skillId = skill.getId();
                int currentLevel = activeSkills.getOrDefault(skillId, 0);
                int maxLevel = skill.getMaxLevel();
                int cost = skill.getCost();
                
                boolean isMaxLevel = currentLevel >= maxLevel;
                boolean canAfford = availableSkillPoints >= cost;
                boolean canUpgrade = !isMaxLevel && canAfford;
                
                // Skill Card
                html.append("<div class='button' style='padding: 15px; margin: 10px 0; background-color: #2a2a2a; border-radius: 8px;'>");
                
                // Skill Name and Level
                html.append("<div style='display: flex; justify-content: space-between; margin-bottom: 10px;'>");
                html.append("<div style='font-size: 18px; font-weight: bold; color: #FF8C00;'>").append(skill.getName()).append("</div>");
                html.append("<div style='color: ")
                    .append(isMaxLevel ? "#00FF00" : "#FFFF00")
                    .append("; font-weight: bold;'>Level ").append(currentLevel).append("/").append(maxLevel).append("</div>");
                html.append("</div>");
                
                // Description
                html.append("<div style='color: #CCCCCC; margin-bottom: 10px; font-size: 14px;'>")
                    .append(skill.getDescription())
                    .append("</div>");
                
                // Cost
                html.append("<div style='margin-bottom: 10px;'>");
                html.append("<span style='color: #808080;'>Cost: </span>");
                html.append("<span style='color: ").append(canAfford ? "#FFD700" : "#FF4444").append("; font-weight: bold;'>")
                    .append(cost).append(" SP</span>");
                html.append("</div>");
                
                // Upgrade Button
                String buttonText;
                String buttonColor;
                boolean enableButton;
                
                if (isMaxLevel) {
                    buttonText = "✓ MAX LEVEL";
                    buttonColor = "#00AA00";
                    enableButton = false;
                } else if (!canAfford) {
                    buttonText = "✗ Not Enough SP";
                    buttonColor = "#AA0000";
                    enableButton = false;
                } else {
                    buttonText = currentLevel == 0 ? "🔓 Unlock" : "⬆ Upgrade";
                    buttonColor = "#4169E1";
                    enableButton = true;
                }
                
                String buttonId = enableButton ? "id='upgrade_" + skillId + "' " : "";
                String cursorStyle = enableButton ? "cursor: pointer;" : "opacity: 0.6;";
                
                html.append("<div class='button' ").append(buttonId)
                    .append("style='padding: 10px; text-align: center; background-color: ").append(buttonColor)
                    .append("; color: white; border-radius: 5px; font-weight: bold; ").append(cursorStyle).append("'>")
                    .append(buttonText)
                    .append("</div>");
                
                html.append("</div>");
            }
        }
        
        html.append("</div></div></div>");
        
        // Build page and register event listeners
        PageBuilder builder = PageBuilder.pageForPlayer(playerRef).fromHtml(html.toString());
        
        // Back button
        builder.addEventListener("back_button", CustomUIEventBindingType.Activating, (ctx) -> {
            show(); // Go back to categories view
        });
        
        // Add event listeners for all skill upgrade buttons
        if (category.getSkills() != null) {
            for (SkillConfig skill : category.getSkills()) {
                String skillId = skill.getId();
                int currentLevel = activeSkills.getOrDefault(skillId, 0);
                int maxLevel = skill.getMaxLevel();
                int cost = skill.getCost();
                
                boolean canUpgrade = currentLevel < maxLevel && availableSkillPoints >= cost;
                
                if (canUpgrade) {
                    builder.addEventListener("upgrade_" + skillId, CustomUIEventBindingType.Activating, (ctx) -> {
                        boolean success = SeyonLevelSystemPlugin.getInstance().getSkillService().activateSkill(player, categoryId, skillId);
                        if (success) {
                            showSkills(categoryId); // Refresh skills view
                        } else {
                            player.sendMessage(Message.raw("Cannot upgrade this skill!").color(Color.RED));
                        }
                    });
                }
            }
        }
        
        builder.open(store);
    }
}
