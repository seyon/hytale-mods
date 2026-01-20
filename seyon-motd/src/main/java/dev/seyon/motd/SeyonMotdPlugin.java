package dev.seyon.motd;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.seyon.motd.command.SeyonMotdCommand;
import dev.seyon.motd.config.MotdConfiguration;
import dev.seyon.motd.event.MotdEventHandler;
import dev.seyon.motd.service.PluginDiscoveryService;

import javax.annotation.Nonnull;

public class SeyonMotdPlugin extends JavaPlugin {

    private static SeyonMotdPlugin INSTANCE;
    private MotdConfiguration configuration;
    private JavaPluginInit pluginInit;
    private PluginDiscoveryService pluginDiscoveryService;

    public SeyonMotdPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        this.pluginInit = init;
        this.configuration = new MotdConfiguration();
        this.pluginDiscoveryService = new PluginDiscoveryService(this.getLogger(), this.pluginInit);
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

        // Register command
        this.getCommandRegistry().registerCommand(new SeyonMotdCommand());

        // Register event handler
        this.getEventRegistry().registerGlobal(
            PlayerReadyEvent.class, 
            event -> MotdEventHandler.onPlayerReady(event, this.configuration)
        );

        this.getLogger().at(java.util.logging.Level.INFO).log("SeyonMotd Plugin loaded successfully!");
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
     * Get all currently loaded/active plugins
     * @return List of plugin strings in format "PluginName vVersion"
     */
    public java.util.List<String> getAllPlugins() {
        return pluginDiscoveryService.discoverAllPlugins();
    }
}
