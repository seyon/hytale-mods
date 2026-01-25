package dev.seyon.motd;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.seyon.motd.config.MotdConfiguration;
import dev.seyon.motd.event.MotdEventHandler;

import javax.annotation.Nonnull;

/**
 * SeyonMotd Plugin - Message of the Day system for Hytale servers
 * Configuration is managed via JSON files in the SeyonMotd/ directory
 */
public class SeyonMotdPlugin extends JavaPlugin {

    private static SeyonMotdPlugin INSTANCE;
    private MotdConfiguration configuration;

    public SeyonMotdPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        this.configuration = new MotdConfiguration();
    }

    public static SeyonMotdPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    protected void setup() {
        super.setup();

        // Create plugin directory
        var folder = new java.io.File("SeyonMotd");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Load configuration
        this.configuration.syncLoad();

        // Register event handler
        this.getEventRegistry().registerGlobal(
            PlayerReadyEvent.class, 
            event -> MotdEventHandler.onPlayerReady(event, this.configuration)
        );

        this.getLogger().at(java.util.logging.Level.INFO).log("SeyonMotd Plugin loaded successfully!");
        this.getLogger().at(java.util.logging.Level.INFO).log("Edit configuration in SeyonMotd/motd-config.json");
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        // Save configuration on shutdown
        this.configuration.syncSave();
        this.getLogger().at(java.util.logging.Level.INFO).log("SeyonMotd Plugin shutdown successfully!");
    }

    public MotdConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Get all currently loaded/active plugins from Core module
     * @return List of plugin strings in format "PluginName (version)"
     */
    public java.util.List<String> getAllPlugins() {
        try {
            dev.seyon.core.SeyonCorePlugin corePlugin = dev.seyon.core.SeyonCorePlugin.getInstance();
            if (corePlugin != null) {
                return corePlugin.getAllPlugins();
            }
        } catch (Exception e) {
            this.getLogger().at(java.util.logging.Level.WARNING)
                .withCause(e)
                .log("Failed to get plugin list from Core module");
        }
        return new java.util.ArrayList<>();
    }
}
