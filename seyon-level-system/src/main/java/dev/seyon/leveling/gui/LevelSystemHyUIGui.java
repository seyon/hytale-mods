package dev.seyon.leveling.gui;

import com.hypixel.hytale.component.Ref;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Level System GUI using HyUI
 * Displays all categories with EXP bars and skill management
 */
public class LevelSystemHyUIGui {
    
    private final Ref<EntityStore> entityRef;
    private final Store<EntityStore> store;
    private String currentCategoryView = null; // null = categories view, otherwise = skill view for category
    
    public LevelSystemHyUIGui(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        this.entityRef = entityRef;
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
        
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            SeyonLevelSystemPlugin.getInstance().getLogger()
                .at(java.util.logging.Level.WARNING)
                .log("GUI: Player component not found");
            return;
        }

        UUID playerId = PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            SeyonLevelSystemPlugin.getInstance().getLogger()
                .at(java.util.logging.Level.WARNING)
                .log("GUI: Player UUID not found");
            return;
        }
        
        PlayerLevelSystemData data = SeyonLevelSystemPlugin.getInstance().getDataService().getPlayerData(playerId);
        
        // DEBUG
        SeyonLevelSystemPlugin.getInstance().getLogger()
            .at(java.util.logging.Level.INFO)
            .log("GUI: Opening for player " + player.getDisplayName() + ", categories: " + 
                SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories().size());
        
        // Calculate total skill points across all categories
        int totalSkillPoints = 0;
        for (String categoryId : data.getCategoryProgress().keySet()) {
            totalSkillPoints += data.getAvailableSkillPoints(categoryId);
        }
        
        // Build HTML: tabular overview
        StringBuilder html = new StringBuilder();
        html.append("<div class='page-overlay'>");
        html.append("<div class='container' data-hyui-title='Level System'>");
        html.append("<div class='container-contents'>");
        
        // Header
        html.append("<div style='text-align: center; margin-bottom: 16px;'>");
        html.append("<div style='font-size: 22px; font-weight: bold; color: #FF8C00;'>Level System</div>");
        html.append("<div style='font-size: 14px; color: #FFD700; margin-top: 6px;'>");
        html.append(player.getDisplayName()).append(" — Skill Points: ").append(totalSkillPoints);
        html.append("</div>");
        html.append("</div>");
        
        // Table: Category | Level | EXP (bar + current/next) | + | »
        html.append("<table style='width:100%; border-collapse: collapse; font-size: 14px;'>");
        html.append("<thead><tr style='border-bottom: 2px solid #444;'>");
        html.append("<th style='text-align:left; padding: 10px 8px; color: #00CED1;'>Category</th>");
        html.append("<th style='text-align:center; padding: 10px 8px; color: #00CED1;'>Level</th>");
        html.append("<th style='text-align:left; padding: 10px 8px; color: #00CED1;'>EXP</th>");
        html.append("<th style='text-align:center; padding: 10px 8px; color: #00CED1; width: 48px;'>+</th>");
        html.append("<th style='text-align:center; padding: 10px 8px; color: #00CED1; width: 40px;'>»</th>");
        html.append("</tr></thead><tbody>");
        
        int categoryCount = 0;
        for (LevelSystemCategory category : SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories()) {
            String categoryId = category.getId();
            CategoryProgress progress = data.getCategoryProgress().get(categoryId);
            if (progress == null) continue;
            categoryCount++;
            
            int level = progress.getCurrentLevel();
            double exp = progress.getCurrentExp();
            double expNeeded = progress.getExpForNextLevel();
            int pendingLevelUps = progress.getPendingLevelUps();
            
            double expPercent = (expNeeded > 0) ? Math.min(100, (exp / expNeeded) * 100) : 100;
            String expText = (expNeeded <= 0) ? "MAX" : (String.format("%.0f", exp) + " / " + String.format("%.0f", expNeeded));
            
            html.append("<tr style='border-bottom: 1px solid #333;'>");
            // Category
            html.append("<td style='padding: 10px 8px; color: #E0E0E0;'>").append(escapeHtml(category.getDisplayName())).append("</td>");
            // Level
            html.append("<td style='padding: 10px 8px; text-align:center; color: #FFD700; font-weight: bold;'>").append(level).append("</td>");
            // EXP: bar + current/next
            html.append("<td style='padding: 10px 8px;'>");
            html.append("<div style='display:flex; align-items:center; gap: 8px;'>");
            html.append("<div style='flex:1; min-width: 60px; background: #1a1a1a; border-radius: 4px; height: 12px; overflow: hidden;'>");
            html.append("<div style='width: ").append(String.format("%.1f", expPercent)).append("%; height: 100%; background: linear-gradient(90deg, #00CC00, #32CD32); border-radius: 4px;'></div>");
            html.append("</div>");
            html.append("<span style='font-size: 12px; color: #888; white-space: nowrap;'>").append(expText).append("</span>");
            html.append("</div></td>");
            // + (Level-Up) — only when pending > 0
            html.append("<td style='padding: 6px 8px; text-align:center;'>");
            if (pendingLevelUps > 0) {
                String plusLabel = (pendingLevelUps > 1) ? "+" + pendingLevelUps : "+";
                html.append("<div class='button' id='levelup_").append(categoryId).append("' style='padding: 4px 10px; font-size: 16px; font-weight: bold; background: #00AA00; color: white; border-radius: 4px; cursor: pointer;'>").append(plusLabel).append("</div>");
            } else {
                html.append("<span style='color: #555;'>—</span>");
            }
            html.append("</td>");
            // » (Skills)
            html.append("<td style='padding: 6px 8px; text-align:center;'>");
            html.append("<div class='button' id='skills_").append(categoryId).append("' style='padding: 4px 8px; font-size: 14px; background: #4169E1; color: white; border-radius: 4px; cursor: pointer;'>»</div>");
            html.append("</td>");
            html.append("</tr>");
        }
        
        html.append("</tbody></table>");
        
        if (categoryCount == 0) {
            html.append("<div style='text-align: center; color: #FF4444; font-size: 16px; padding: 24px;'>No categories loaded. Check configuration.</div>");
        }
        
        html.append("</div></div></div>");
        
        PlayerRef playerRefForBuilder = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRefForBuilder == null) return;
        
        PageBuilder builder = PageBuilder.pageForPlayer(playerRefForBuilder).fromHtml(html.toString());
        
        for (LevelSystemCategory category : SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories()) {
            String categoryId = category.getId();
            CategoryProgress progress = data.getCategoryProgress().get(categoryId);
            if (progress == null) continue;
            int pendingLevelUps = progress.getPendingLevelUps();
            
            if (pendingLevelUps > 0) {
                builder.addEventListener("levelup_" + categoryId, CustomUIEventBindingType.Activating, (ctx) -> {
                    SeyonLevelSystemPlugin.getInstance().getExperienceService().processLevelUp(player, categoryId);
                    show();
                });
            }
            builder.addEventListener("skills_" + categoryId, CustomUIEventBindingType.Activating, (ctx) -> showSkills(categoryId));
        }
        
        builder.open(store);
    }
    
    /** Escape HTML for safe display in UI. */
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    /**
     * Show skills for a category
     */
    private void showSkills(String categoryId) {
        currentCategoryView = categoryId;
        
        Player player = store.getComponent(entityRef, Player.getComponentType());
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
        Map<String, Integer> activeSkills = data.getActiveSkills().getOrDefault(categoryId, new HashMap<>());
        
        // Build HTML
        StringBuilder html = new StringBuilder();
        html.append("<div class='page-overlay'>");
        html.append("<div class='container' data-hyui-title='").append(category.getDisplayName()).append(" - Skills'>");
        html.append("<div class='container-contents'>");
        
        // Back Button
        html.append("<div class='button' id='back_button' ")
            .append("style='padding: 10px; margin-bottom: 20px; background-color: #555555; color: white; ")
            .append("border-radius: 5px; text-align: center; cursor: pointer;'>")
            .append("&lt; BACK TO CATEGORIES")
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
                int maxLevel = skill.getMaxPoints();
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
                    buttonText = "MAX LEVEL";
                    buttonColor = "#00AA00";
                    enableButton = false;
                } else if (!canAfford) {
                    buttonText = "NOT ENOUGH SP";
                    buttonColor = "#AA0000";
                    enableButton = false;
                } else {
                    buttonText = currentLevel == 0 ? "UNLOCK" : "UPGRADE";
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
        PlayerRef playerRefForBuilder = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRefForBuilder == null) {
            return;
        }
        
        PageBuilder builder = PageBuilder.pageForPlayer(playerRefForBuilder).fromHtml(html.toString());
        
        // Back button
        builder.addEventListener("back_button", CustomUIEventBindingType.Activating, (ctx) -> {
            show(); // Go back to categories view
        });
        
        // Add event listeners for all skill upgrade buttons
        if (category.getSkills() != null) {
            for (SkillConfig skill : category.getSkills()) {
                String skillId = skill.getId();
                int currentLevel = activeSkills.getOrDefault(skillId, 0);
                int maxLevel = skill.getMaxPoints();
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
