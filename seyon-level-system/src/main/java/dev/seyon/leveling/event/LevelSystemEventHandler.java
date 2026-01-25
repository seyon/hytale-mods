package dev.seyon.leveling.event;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Event handler for Level System events
 */
public class LevelSystemEventHandler {

    /**
     * Handle player ready event
     */
    public static void onPlayerReady(PlayerReadyEvent event, SeyonLevelSystemPlugin plugin) {
        Player player = event.getPlayer();
        UUID playerId = dev.seyon.core.PlayerUtils.getPlayerUUID(player);
        if (playerId == null) return;

        try {
            // Initialize player data
            plugin.getDataService().initializePlayerCategories(playerId, plugin.getCategoryService());

            // Apply modifiers
            plugin.getModifierService().applyModifiers(player);

            // Welcome message
            player.sendMessage(Message.join(
                Message.raw("⭐ [Level System] ").color(Color.ORANGE),
                Message.raw("Level System initialized. Use ").color(Color.GRAY),
                Message.raw("/seyon-level").color(Color.CYAN).bold(true),
                Message.raw(" for your overview.").color(Color.GRAY)
            ));

            // If at least one level-up is ready: remind player to open GUI
            PlayerLevelSystemData data = plugin.getDataService().getPlayerData(playerId);
            int total = 0;
            List<String> parts = new ArrayList<>();
            for (java.util.Map.Entry<String, CategoryProgress> e : data.getCategoryProgress().entrySet()) {
                CategoryProgress p = e.getValue();
                if (p.getPendingLevelUps() > 0) {
                    String categoryId = e.getKey();
                    LevelSystemCategory cat = plugin.getCategoryService().getCategory(categoryId);
                    String name = (cat != null && cat.getDisplayName() != null) ? cat.getDisplayName() : categoryId;
                    total += p.getPendingLevelUps();
                    parts.add(name + " (" + p.getPendingLevelUps() + ")");
                }
            }
            if (total > 0) {
                player.sendMessage(Message.join(
                    Message.raw("Level-up(s) ready: ").color(Color.ORANGE),
                    Message.raw(String.join(", ", parts)).color(Color.YELLOW),
                    Message.raw(" — Use ").color(Color.GRAY),
                    Message.raw("/seyon-level").color(Color.CYAN).bold(true),
                    Message.raw(" to claim them.").color(Color.GRAY)
                ));
            }

            plugin.getLogger().at(java.util.logging.Level.INFO)
                .log("Level System initialized for player: " + player.getDisplayName());

        } catch (Exception e) {
            plugin.getLogger().at(java.util.logging.Level.SEVERE)
                .withCause(e)
                .log("Failed to initialize Level System for player: " + player.getDisplayName());
        }
    }

    /**
     * Handle player disconnect: unload data and clean exploration walk tracker
     */
    public static void onPlayerDisconnect(PlayerDisconnectEvent event, SeyonLevelSystemPlugin plugin) {
        UUID playerId = event.getPlayerRef().getUuid();
        try {
            plugin.getDataService().unloadPlayerData(playerId);
        } catch (Exception e) {
            plugin.getLogger().at(java.util.logging.Level.WARNING)
                .withCause(e)
                .log("Failed to unload player data for: " + playerId);
        }
        plugin.getExplorationWalkTracker().remove(playerId);
    }
}
