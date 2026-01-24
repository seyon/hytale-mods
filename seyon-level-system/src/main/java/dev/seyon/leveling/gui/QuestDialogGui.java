package dev.seyon.leveling.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

/**
 * Quest dialog GUI for milestone quests
 * 
 * TODO: Implement complete quest dialog GUI with:
 * - NPC portrait/image
 * - Quest text/dialog
 * - List of required items (if item collection quest)
 * - Progress indicators for item requirements
 * - "Complete Quest" button
 * - Reward display (EXP unlock message)
 * 
 * Structure similar to MotdGui from seyon-motd
 */
public class QuestDialogGui {
    
    private final Player player;
    private final String categoryId;
    private final int level;
    
    public QuestDialogGui(Player player, String categoryId, int level) {
        this.player = player;
        this.categoryId = categoryId;
        this.level = level;
    }
    
    public void build(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder) {
        // TODO: Implement quest dialog GUI
        
        // 1. Display NPC portrait
        // 2. Show quest text
        // 3. List required items with progress
        // 4. Add "Complete Quest" button
        // 5. Show reward information
    }
    
    // UI file would be located at:
    // src/main/resources/Common/UI/Custom/Pages/SeyonLevelSystem/QuestDialog.ui
}
