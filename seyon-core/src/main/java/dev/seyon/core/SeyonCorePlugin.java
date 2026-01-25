package dev.seyon.core;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.seyon.core.service.PluginDiscoveryService;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Core plugin for Seyon mods - provides shared services
 * No GUI - all configuration is done via JSON files
 */
public class SeyonCorePlugin extends JavaPlugin {
    
    private static SeyonCorePlugin instance;
    private PluginDiscoveryService pluginDiscoveryService;
    private JavaPluginInit pluginInit;
    
    public SeyonCorePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        this.pluginInit = init;
        this.pluginDiscoveryService = new PluginDiscoveryService(this.getLogger(), init);
    }
    
    @Override
    protected void setup() {
        super.setup();
        
        this.getLogger().at(java.util.logging.Level.INFO).log("Seyon Core Plugin enabled");
        
        // Discover and cache all plugins
        pluginDiscoveryService.discoverAndCachePlugins();
        
        this.getLogger().at(java.util.logging.Level.INFO).log("All Seyon mods use JSON configuration files in their respective directories");
    }
    
    @Override
    protected void shutdown() {
        super.shutdown();
        this.getLogger().at(java.util.logging.Level.INFO).log("Seyon Core Plugin disabled");
    }
    
    @Nonnull
    public static SeyonCorePlugin getInstance() {
        return instance;
    }
    
    /**
     * Get the cached list of all loaded plugins
     * @return List of plugin strings in format "PluginName (version)"
     */
    public List<String> getAllPlugins() {
        return pluginDiscoveryService.getCachedPluginList();
    }
}
