package dev.seyon.leveling.event;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.seyon.leveling.SeyonLevelSystemPlugin;

import java.awt.Color;

/**
 * Event handler for Level System events
 */
public class LevelSystemEventHandler {

    /**
     * Handle player ready event
     */
    public static void onPlayerReady(PlayerReadyEvent event, SeyonLevelSystemPlugin plugin) {
        Player player = event.getPlayer();
        
        try {
            // Initialize player data
            // Note: Using deprecated getUuid() as PlayerUtils.getPlayerUUID() may cause threading issues in events
            plugin.getDataService().initializePlayerCategories(player.getUuid(), plugin.getCategoryService());
            
            // Apply modifiers
            plugin.getModifierService().applyModifiers(player);
            
            // Send welcome message immediately (no scheduler to avoid connection issues)
            player.sendMessage(Message.join(
                Message.raw("⭐ [Level System] ").color(Color.ORANGE),
                Message.raw("Level System initialized! Use ").color(Color.GRAY),
                Message.raw("/leveling stats").color(Color.CYAN).bold(true),
                Message.raw(" to view your progress.").color(Color.GRAY)
            ));
            
            plugin.getLogger().at(java.util.logging.Level.INFO)
                .log("Level System initialized for player: " + player.getDisplayName());
                
        } catch (Exception e) {
            plugin.getLogger().at(java.util.logging.Level.SEVERE)
                .withCause(e)
                .log("Failed to initialize Level System for player: " + player.getDisplayName());
        }
    }

    /**
     * Handle player disconnect - save data
     */
    public static void onPlayerDisconnect(Player player, SeyonLevelSystemPlugin plugin) {
        try {
            // Note: Using deprecated getUuid() as PlayerUtils.getPlayerUUID() may cause threading issues
            plugin.getDataService().unloadPlayerData(player.getUuid());
        } catch (Exception e) {
            plugin.getLogger().at(java.util.logging.Level.WARNING)
                .withCause(e)
                .log("Failed to unload player data for: " + player.getDisplayName());
        }
    }

    // TODO: Implement event handlers for EXP-gaining actions:
    // - BlockBreakEvent -> grant EXP based on block type
    // - EntityKillEvent -> grant EXP based on entity type
    // - ItemCraftEvent -> grant EXP based on crafted item
    // - ExploreEvent -> grant EXP for discovering new areas
    // 
    // These will be implemented once we have access to the actual Hytale event types
}
