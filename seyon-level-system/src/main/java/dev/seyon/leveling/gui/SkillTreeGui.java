package dev.seyon.leveling.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

/**
 * Skill tree GUI for a specific category
 * 
 * TODO: Implement complete skill tree GUI with:
 * - Tabs for different tiers (Tier 1, 2, 3)
 * - Grid layout with skill cards
 * - Each skill card shows: name, description, current/max points, cost, effects
 * - Button to activate/upgrade skills
 * - Available skill points display at top
 * - Visual indication of which skills are active
 * 
 * Structure similar to MotdGui from seyon-motd
 */
public class SkillTreeGui {
    
    private final Player player;
    private final String categoryId;
    
    public SkillTreeGui(Player player, String categoryId) {
        this.player = player;
        this.categoryId = categoryId;
    }
    
    public void build(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder) {
        // TODO: Implement skill tree GUI
        
        // 1. Create tier tabs (Tier 1, 2, 3)
        // 2. Create skill cards grid
        // 3. Add skill activation buttons
        // 4. Show available skill points
        // 5. Add visual feedback for active skills
    }
    
    // UI file would be located at:
    // src/main/resources/Common/UI/Custom/Pages/SeyonLevelSystem/SkillTree.ui
}
