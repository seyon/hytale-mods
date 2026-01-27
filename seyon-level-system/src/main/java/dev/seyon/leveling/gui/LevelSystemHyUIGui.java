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
import dev.seyon.leveling.service.LevelEffectsDisplayHelper;
import au.ellie.hyui.builders.PageBuilder;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

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
        int maxLevel = SeyonLevelSystemPlugin.getInstance().getConfigService().getMainConfig().getGlobalSettings().getMaxLevel();
        
        // Calculate total skill points across all categories
        int totalSkillPoints = 0;
        for (String categoryId : data.getCategoryProgress().keySet()) {
            totalSkillPoints += data.getAvailableSkillPoints(categoryId);
        }
        
        // Build HTML: HyUI-conformant layout (no tables, only div/p/button/progress)
        StringBuilder html = new StringBuilder();
        
        html.append("<div class='page-overlay'>");
        html.append("<div class='container' data-hyui-title='Level System' data-hyui-width='1200' data-hyui-height='800'>");
        html.append("<div class='container-contents'>");
        
        // Header
        html.append("<p>=== Level System ===</p>");
        html.append("<p>").append(escapeHtml(player.getDisplayName())).append(" - Skill Points: ").append(totalSkillPoints).append("</p>");
        html.append("<p>---</p>");
        
        // Category cards
        Set<String> renderedCategories = new HashSet<>();
        int categoryCount = 0;
        
        for (LevelSystemCategory category : SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories()) {
            String categoryId = category.getId();
            String sanitizedId = sanitizeId(categoryId);
            CategoryProgress progress = data.getCategoryProgress().get(categoryId);
            
            // Initialize progress if null (for newly added categories)
            if (progress == null) {
                progress = data.getOrCreateCategoryProgress(categoryId);
                progress.setExpForNextLevel(category.getExpCurve().calculateExpForLevel(1));
            }
            
            categoryCount++;
            renderedCategories.add(categoryId);
            
            int level = progress.getCurrentLevel();
            double exp = progress.getCurrentExp();
            double expNeeded = progress.getExpForNextLevel();
            int pendingLevelUps = progress.getPendingLevelUps();
            boolean isMaxLevel = level >= maxLevel;
            
            // Category section with clear separators
            html.append("<p></p>");
            html.append("<p>[ ").append(escapeHtml(category.getDisplayName())).append(" ]</p>");
            html.append("<p>Level: ").append(level).append("</p>");
            html.append("<p></p>");
            
            // EXP progress bar + text
            if (isMaxLevel) {
                html.append("<p>Status: MAX LEVEL</p>");
            } else {
                double expPercent = (expNeeded > 0) ? Math.min(1.0, exp / expNeeded) : 1.0;
                html.append("<progress value='").append(String.format("%.3f", expPercent)).append("'></progress>");
                html.append("<p></p>");
                String expText = String.format("EXP: %.0f / %.0f", exp, expNeeded);
                html.append("<p>").append(expText).append("</p>");
            }
            html.append("<p></p>");
            
            // Buttons with direct text content
            if (pendingLevelUps > 0 && !isMaxLevel) {
                String plusLabel = (pendingLevelUps > 1) ? "Level Up (+" + pendingLevelUps + ")" : "Level Up";
                html.append("<button id='levelup_").append(sanitizedId).append("'>").append(plusLabel).append("</button>");
                html.append("<p></p>");
            }
            html.append("<button id='skills_").append(sanitizedId).append("'>Skills</button>");
            html.append("<p></p>");
            
            // Current level effects
            Map<String, Double> currentEffects = LevelEffectsDisplayHelper.getCumulativeLevelBonusesUpTo(category, level);
            String currentText = LevelEffectsDisplayHelper.formatModifiersForDisplay(currentEffects);
            html.append("<p>Aktuell (Lv.").append(level).append("):</p>");
            html.append("<p>").append(escapeHtml(currentText)).append("</p>");
            html.append("<p></p>");
            
            // Next level effects
            if (!isMaxLevel) {
                Map<String, Double> nextEffects = LevelEffectsDisplayHelper.getLevelBonusAtLevel(category, level + 1);
                String nextText = LevelEffectsDisplayHelper.formatModifiersForDisplay(nextEffects);
                html.append("<p>Naechstes (Lv.").append(level + 1).append("):</p>");
                html.append("<p>").append(escapeHtml(nextText)).append("</p>");
                
                // Optional: Quest hint
                if (category.hasQuestAtLevel(level + 1)) {
                    html.append("<p>! Quest bei Level ").append(level + 1).append("</p>");
                }
            } else {
                html.append("<p>Max Level erreicht</p>");
            }
            
            html.append("<p>---</p>"); // separator
        }
        
        if (categoryCount == 0) {
            html.append("<p>No categories loaded. Check configuration.</p>");
        }
        
        html.append("</div></div></div>");
        
        // Build page and register event listeners
        PlayerRef playerRefForBuilder = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRefForBuilder == null) return;
        
        PageBuilder builder = PageBuilder.pageForPlayer(playerRefForBuilder).fromHtml(html.toString());
        
        for (String categoryId : renderedCategories) {
            String sanitizedId = sanitizeId(categoryId);
            CategoryProgress progress = data.getCategoryProgress().get(categoryId);
            if (progress == null) continue;

            if (progress.getPendingLevelUps() > 0 && progress.getCurrentLevel() < maxLevel) {
                addEventListenerSafe(builder, "levelup_" + sanitizedId, CustomUIEventBindingType.Activating, (ctx) -> {
                    SeyonLevelSystemPlugin.getInstance().getExperienceService().processLevelUp(player, categoryId);
                    show();
                });
            }
            addEventListenerSafe(builder, "skills_" + sanitizedId, CustomUIEventBindingType.Activating, (ctx) -> showSkills(categoryId));
        }

        builder.open(store);
    }
    
    /** Escape HTML for safe display in UI. */
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    /**
     * Sanitize ID to only contain characters allowed in HTML IDs
     * HyUI requires alphanumeric IDs only; special characters are removed
     * @param id The original ID
     * @return Sanitized ID (only [a-zA-Z0-9_-])
     */
    private static String sanitizeId(String id) {
        if (id == null) return "";
        return id.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    /**
     * Register an event listener, skipping gracefully if HyUI reports the element is missing.
     * Some mod-provided categories may not have their UI elements available (e.g. parsing quirks).
     */
    private void addEventListenerSafe(PageBuilder builder, String elementId, CustomUIEventBindingType type, Consumer<Object> handler) {
        try {
            builder.addEventListener(elementId, type, handler);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("No element found with ID")) {
                SeyonLevelSystemPlugin.getInstance().getLogger()
                    .at(Level.WARNING)
                    .log("GUI: Skipping listener for missing element '" + elementId + "' (mod-provided category?): " + msg);
            } else {
                throw e;
            }
        }
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
        
        // Build HTML: HyUI-conformant layout (no tables, only div/p/button)
        StringBuilder html = new StringBuilder();
        
        html.append("<div class='page-overlay'>");
        html.append("<div class='container' data-hyui-title='").append(escapeHtml(category.getDisplayName())).append(" - Skills' data-hyui-width='1200' data-hyui-height='800'>");
        html.append("<div class='container-contents'>");
        
        // Back Button
        html.append("<button id='back_button'>Zurueck</button>");
        html.append("<p></p>");
        html.append("<p>---</p>");
        
        // Header with Available Skill Points
        html.append("<p>=== ").append(escapeHtml(category.getDisplayName())).append(" ===</p>");
        html.append("<p>Verfuegbare Skill-Punkte: ").append(availableSkillPoints).append("</p>");
        html.append("<p>---</p>");
        
        // Skills
        Set<String> renderedSkills = new HashSet<>();
        if (category.getSkills() == null || category.getSkills().isEmpty()) {
            html.append("<p>Keine Skills verfügbar für diese Kategorie.</p>");
        } else {
            for (SkillConfig skill : category.getSkills()) {
                String skillId = skill.getId();
                String sanitizedSkillId = sanitizeId(skillId);
                int currentLevel = activeSkills.getOrDefault(skillId, 0);
                int maxLevel = skill.getMaxPoints();
                int cost = skill.getCost();
                
                boolean isMaxLevel = currentLevel >= maxLevel;
                boolean canAfford = availableSkillPoints >= cost;
                boolean canUpgrade = !isMaxLevel && canAfford;
                
                // Skill Card
                html.append("<p></p>");
                html.append("<p>[ ").append(escapeHtml(skill.getName())).append(" ]</p>");
                html.append("<p>Level: ").append(currentLevel).append("/").append(maxLevel).append("</p>");
                html.append("<p>").append(escapeHtml(skill.getDescription())).append("</p>");
                html.append("<p>Kosten: ").append(cost).append(" SP</p>");
                html.append("<p></p>");
                
                // Upgrade Button
                String buttonText;
                
                if (isMaxLevel) {
                    buttonText = "MAX LEVEL";
                } else if (!canAfford) {
                    buttonText = "NICHT GENUG SP";
                } else {
                    buttonText = currentLevel == 0 ? "FREISCHALTEN" : "AUFWERTEN";
                    renderedSkills.add(skillId);
                }
                
                if (canUpgrade) {
                    html.append("<button id='upgrade_").append(sanitizedSkillId).append("'>").append(buttonText).append("</button>");
                } else {
                    html.append("<p>Status: ").append(buttonText).append("</p>");
                }
                
                html.append("<p>---</p>"); // separator
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
        addEventListenerSafe(builder, "back_button", CustomUIEventBindingType.Activating, (ctx) -> show());

        // Add event listeners for all skill upgrade buttons
        for (String skillId : renderedSkills) {
            String sanitizedSkillId = sanitizeId(skillId);
            String catId = categoryId;
            addEventListenerSafe(builder, "upgrade_" + sanitizedSkillId, CustomUIEventBindingType.Activating, (ctx) -> {
                boolean success = SeyonLevelSystemPlugin.getInstance().getSkillService().activateSkill(player, catId, skillId);
                if (success) {
                    showSkills(catId); // Refresh skills view
                } else {
                    player.sendMessage(Message.raw("Skill kann nicht aufgewertet werden!").color(Color.RED));
                }
            });
        }

        builder.open(store);
    }
    
    /**
     * Get the player entity from the entity store
     */
    private Player getPlayer() {
        return store.getComponent(entityRef, Player.getComponentType());
    }
}
