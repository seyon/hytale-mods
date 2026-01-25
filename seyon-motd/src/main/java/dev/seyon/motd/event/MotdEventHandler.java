package dev.seyon.motd.event;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.motd.SeyonMotdPlugin;
import dev.seyon.motd.config.MotdConfiguration;
import dev.seyon.motd.config.TranslationManager;

import java.awt.Color;

public class MotdEventHandler {

    /**
     * Called when a player joins the server
     */
    public static void onPlayerReady(PlayerReadyEvent event, MotdConfiguration config) {
        Player player = event.getPlayer();
        
        // Schedule MOTD to be sent after 5 seconds
        // Use a scheduled executor service to delay the execution
        try {
            // Get player reference and world for later execution
            Ref<EntityStore> ref = player.getReference();
            final World world = (ref != null && ref.isValid()) ? 
                ref.getStore().getExternalData().getWorld() : null;
            
            // Schedule delayed task using ScheduledExecutorService
            java.util.concurrent.ScheduledExecutorService scheduler = 
                java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "SeyonMotd-DelayedMOTD");
                    t.setDaemon(true);
                    return t;
                });
            
            // Schedule to run after 5 seconds
            scheduler.schedule(() -> {
                try {
                    // Check if player reference is still valid
                    if (ref != null && ref.isValid()) {
                        // Execute on the world thread using CompletableFuture
                        if (world != null) {
                            java.util.concurrent.CompletableFuture.runAsync(() -> {
                                if (ref.isValid()) {
                                    sendMotd(player, config);
                                    
                                    // Schedule plugin list to be sent after configured delay
                                    if (config.shouldShowPluginList()) {
                                        schedulePluginList(player, config, ref, world);
                                    }
                                }
                            }, world);
                        } else {
                            // Fallback: execute directly if world is not available
                            sendMotd(player, config);
                            
                            if (config.shouldShowPluginList()) {
                                schedulePluginList(player, config, ref, null);
                            }
                        }
                    }
                } finally {
                    scheduler.shutdown();
                }
            }, 5, java.util.concurrent.TimeUnit.SECONDS);
            
        } catch (Exception e) {
            // Fallback: if scheduling fails, send immediately
            SeyonMotdPlugin.getInstance().getLogger()
                .at(java.util.logging.Level.WARNING)
                .log("Failed to schedule delayed MOTD, sending immediately: " + e.getMessage());
            sendMotd(player, config);
            
            if (config.shouldShowPluginList()) {
                sendPluginList(player, config);
            }
        }
    }
    
    /**
     * Schedules the plugin list to be sent after the configured delay
     */
    private static void schedulePluginList(Player player, MotdConfiguration config, Ref<EntityStore> ref, World world) {
        int delaySeconds = config.getShowPluginListAfterSeconds();
        
        java.util.concurrent.ScheduledExecutorService pluginScheduler = 
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "SeyonMotd-DelayedPluginList");
                t.setDaemon(true);
                return t;
            });
        
        pluginScheduler.schedule(() -> {
            try {
                if (ref != null && ref.isValid()) {
                    if (world != null) {
                        java.util.concurrent.CompletableFuture.runAsync(() -> {
                            if (ref.isValid()) {
                                sendPluginList(player, config);
                            }
                        }, world);
                    } else {
                        sendPluginList(player, config);
                    }
                }
            } finally {
                pluginScheduler.shutdown();
            }
        }, delaySeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * Send MOTD to a player (can be used for testing)
     */
    public static void sendMotd(Player player, MotdConfiguration config) {
        if (config.getMessages().isEmpty()) {
            return; // No messages to display
        }

        TranslationManager t = config.getTranslations();

        // Debug log
        dev.seyon.motd.SeyonMotdPlugin.getInstance().getLogger()
            .at(java.util.logging.Level.INFO)
            .log("MOTD Config - showInChat: " + config.isShowInChat() + 
                 ", showAsNotification: " + config.isShowAsNotification() + 
                 ", showPluginListAfterSeconds: " + config.getShowPluginListAfterSeconds());

        // Show in CHAT if enabled
        if (config.isShowInChat()) {
            // Show MOTD messages
            for (MotdConfiguration.MotdMessage message : config.getMessages()) {
                Color color = MotdConfiguration.hexToColor(message.color());
                player.sendMessage(Message.raw(message.text()).color(color));
            }
        }

        // Show as NOTIFICATION if enabled
        if (config.isShowAsNotification()) {
            var playerRef = dev.seyon.core.PlayerUtils.getPlayerRef(player);
            var packetHandler = playerRef.getPacketHandler();
            
            // Use a default item icon
            var iconItem = new com.hypixel.hytale.server.core.inventory.ItemStack("Common_Grass", 1).toPacket();
            
            // Build primary message from first MOTD message
            MotdConfiguration.MotdMessage firstMsg = config.getMessages().get(0);
            Color firstColor = MotdConfiguration.hexToColor(firstMsg.color());
            Message primaryMessage = Message.raw(firstMsg.text()).color(firstColor);
            
            // Build secondary message from remaining messages (each on new line)
            Message secondaryMessage = null;
            if (config.getMessages().size() > 1) {
                // Join remaining messages with newline character
                java.util.List<Message> messageList = new java.util.ArrayList<>();
                for (int i = 1; i < config.getMessages().size(); i++) {
                    MotdConfiguration.MotdMessage msg = config.getMessages().get(i);
                    Color msgColor = MotdConfiguration.hexToColor(msg.color());
                    if (i > 1) {
                        messageList.add(Message.raw("\n")); // Newline between messages
                    }
                    messageList.add(Message.raw(msg.text()).color(msgColor));
                }
                secondaryMessage = Message.join(messageList.toArray(new Message[0]));
            }
            
            com.hypixel.hytale.server.core.util.NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                iconItem,
                com.hypixel.hytale.protocol.packets.interface_.NotificationStyle.Default
            );
        }
    }
    
    /**
     * Send plugin list to a player (delayed after MOTD)
     */
    public static void sendPluginList(Player player, MotdConfiguration config) {
        TranslationManager t = config.getTranslations();
        var plugins = SeyonMotdPlugin.getInstance().getAllPlugins();
        
        // Show plugin list in chat
        if (config.isShowInChat()) {
            player.sendMessage(Message.raw("").color(Color.WHITE)); // Empty line
            player.sendMessage(Message.raw(t.get("chat.installed_plugins_header")).color(Color.YELLOW).bold(true));
            
            for (var pluginInfo : plugins) {
                // Plugin info is already formatted as "Name (version)" or just "Name"
                // Check if version is present
                if (pluginInfo.contains("(") && pluginInfo.contains(")")) {
                    // Has version - extract name and version
                    int openParen = pluginInfo.indexOf("(");
                    String name = pluginInfo.substring(0, openParen).trim();
                    String version = pluginInfo.substring(openParen + 1, pluginInfo.indexOf(")"));
                    
                    player.sendMessage(
                        Message.join(
                            Message.raw("• ").color(Color.GRAY),
                            Message.raw(name).color(Color.GREEN),
                            Message.raw(" (" + version + ")").color(Color.DARK_GRAY)
                        )
                    );
                } else {
                    // No version - just show name
                    player.sendMessage(
                        Message.join(
                            Message.raw("• ").color(Color.GRAY),
                            Message.raw(pluginInfo).color(Color.GREEN)
                        )
                    );
                }
            }
        }
        
        // Show as notification if enabled
        if (config.isShowAsNotification()) {
            var playerRef = dev.seyon.core.PlayerUtils.getPlayerRef(player);
            var packetHandler = playerRef.getPacketHandler();
            
            // Use a default item icon
            var iconItem = new com.hypixel.hytale.server.core.inventory.ItemStack("Common_Grass", 1).toPacket();
            
            StringBuilder pluginNames = new StringBuilder();
            int count = 0;
            for (var pluginInfo : plugins) {
                // Extract name only (remove version if present)
                String name = pluginInfo.contains("(") ? pluginInfo.substring(0, pluginInfo.indexOf("(")).trim() : pluginInfo;
                if (count > 0) pluginNames.append(", ");
                pluginNames.append(name);
                count++;
            }
            
            Message primaryMessage = Message.raw(t.get("notification.plugins_prefix")).color(Color.YELLOW);
            Message secondaryMessage = Message.raw(pluginNames.toString()).color(Color.GREEN);
            
            com.hypixel.hytale.server.core.util.NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                iconItem,
                com.hypixel.hytale.protocol.packets.interface_.NotificationStyle.Default
            );
        }
    }
}
