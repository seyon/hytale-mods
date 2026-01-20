package dev.seyon.magic.event;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.seyon.magic.SeyonMagicPlugin;

import java.awt.Color;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Event handler for magic-related events
 */
public class MagicEventHandler {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Handle player ready event - send welcome message
     */
    public static void onPlayerReady(PlayerReadyEvent event, SeyonMagicPlugin plugin) {
        var player = event.getPlayer();
        
        // Send welcome message after 3 seconds
        scheduler.schedule(() -> {
            if (player != null && player.getReference() != null && player.getReference().isValid()) {
                player.sendMessage(Message.join(
                    Message.raw("✨ [Arcane Arts] ").color(Color.ORANGE),
                    Message.raw("Magic system initialized!").color(Color.GRAY)
                ));
                plugin.getLogger().at(java.util.logging.Level.INFO)
                    .log("Magic system initialized for player: " + player.getDisplayName());
            }
        }, 3, TimeUnit.SECONDS);
    }
}
