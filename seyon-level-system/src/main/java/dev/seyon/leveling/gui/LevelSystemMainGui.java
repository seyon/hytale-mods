package dev.seyon.leveling.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;

/**
 * Main GUI for Level System
 * 
 * TODO: Implement InteractiveCustomUIPage once Hytale Page API is available
 * 
 * Current blockers:
 * - CustomPageLifetime enum values not available in current API
 * - PlayerRef.getPageManager() method not available
 * - EventData.string() method signature unknown
 * - Need to research correct InteractiveCustomUIPage implementation
 * 
 * Reference implementation in seyon-motd works, but uses different API calls.
 * Need to wait for Hytale Beta API updates or find alternative approach.
 */
public class LevelSystemMainGui {
    
    private final Player player;
    
    public LevelSystemMainGui(Player player) {
        this.player = player;
    }
    
    // TODO: Implement full GUI once Hytale Page Manager API is available
    // UI files are ready in resources/Common/UI/Custom/Pages/SeyonLevelSystem/
}
