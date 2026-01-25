package dev.seyon.core.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service for discovering and retrieving loaded plugins from the Hytale server.
 * Initialized once at server start and cached for all mods to use.
 */
public class PluginDiscoveryService {
    
    private final HytaleLogger logger;
    private final JavaPluginInit pluginInit;
    private List<String> cachedPluginList = null;
    
    public PluginDiscoveryService(HytaleLogger logger, JavaPluginInit pluginInit) {
        this.logger = logger;
        this.pluginInit = pluginInit;
    }
    
    /**
     * Discovers all loaded plugins and caches the result
     * @return List of formatted plugin strings
     */
    public List<String> discoverAndCachePlugins() {
        if (cachedPluginList != null) {
            return cachedPluginList;
        }
        
        logger.at(java.util.logging.Level.INFO).log("[PluginDiscovery] Starting plugin discovery...");
        
        List<String> formattedPlugins = new ArrayList<>();
        
        if (pluginInit == null) {
            logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] pluginInit is null");
            cachedPluginList = formattedPlugins;
            return cachedPluginList;
        }
        
        try {
            // Access classLoader field from pluginInit
            java.lang.reflect.Field classLoaderField = pluginInit.getClass().getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            Object classLoader = classLoaderField.get(pluginInit);
            
            if (classLoader == null) {
                logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] classLoader is null");
                cachedPluginList = formattedPlugins;
                return cachedPluginList;
            }
            
            // Access pluginManager field from classLoader
            java.lang.reflect.Field pluginManagerField = classLoader.getClass().getDeclaredField("pluginManager");
            pluginManagerField.setAccessible(true);
            Object pluginManager = pluginManagerField.get(classLoader);
            
            if (pluginManager == null) {
                logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] pluginManager is null");
                cachedPluginList = formattedPlugins;
                return cachedPluginList;
            }
            
            // Call getPlugins() method on pluginManager
            java.lang.reflect.Method getPluginsMethod = pluginManager.getClass().getMethod("getPlugins");
            Object result = getPluginsMethod.invoke(pluginManager);
            
            if (result instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<? extends JavaPlugin> plugins = (Collection<? extends JavaPlugin>) result;
                
                if (plugins != null && !plugins.isEmpty()) {
                    formattedPlugins = formatPlugins(plugins);
                    logger.at(java.util.logging.Level.INFO).log(String.format(
                        "[PluginDiscovery] Found %d plugins", formattedPlugins.size()));
                }
            }
            
        } catch (Exception e) {
            logger.at(java.util.logging.Level.WARNING)
                .withCause(e)
                .log("[PluginDiscovery] Exception during discovery");
        }
        
        cachedPluginList = formattedPlugins;
        return cachedPluginList;
    }
    
    /**
     * Get the cached plugin list
     * @return Cached plugin list, or empty list if not yet discovered
     */
    public List<String> getCachedPluginList() {
        if (cachedPluginList == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(cachedPluginList);
    }
    
    /**
     * Formats a collection of plugins into a list of strings
     * Filters out Hytale plugins (author:name format where author == "Hytale")
     */
    private List<String> formatPlugins(Collection<? extends JavaPlugin> plugins) {
        List<String> formatted = new ArrayList<>();
        if (plugins == null) {
            return formatted;
        }
        
        for (JavaPlugin plugin : plugins) {
            try {
                String fullName = plugin.getName();
                
                // Skip Hytale plugins (format: "Hytale:PluginName")
                if (fullName != null && fullName.contains(":")) {
                    String[] parts = fullName.split(":", 2);
                    String author = parts[0];
                    
                    // Filter out Hytale plugins
                    if ("Hytale".equalsIgnoreCase(author.trim())) {
                        continue;
                    }
                    
                    // Use the plugin name without the author prefix
                    String pluginName = parts.length > 1 ? parts[1] : fullName;
                    String version = getPluginVersion(plugin);
                    String formattedPlugin = formatPluginInfo(pluginName, version);
                    
                    if (formattedPlugin != null) {
                        formatted.add(formattedPlugin);
                    } else {
                        formatted.add(pluginName);
                    }
                } else {
                    // No author prefix, use full name
                    String version = getPluginVersion(plugin);
                    String formattedPlugin = formatPluginInfo(fullName, version);
                    
                    if (formattedPlugin != null) {
                        formatted.add(formattedPlugin);
                    } else {
                        formatted.add(fullName);
                    }
                }
            } catch (Exception e) {
                // If we can't format this plugin, skip it
                logger.at(java.util.logging.Level.FINE).log("Failed to format plugin: " + e.getMessage());
            }
        }
        
        return formatted;
    }
    
    /**
     * Formats plugin information as "name (version)"
     */
    private String formatPluginInfo(String name, String version) {
        if (name == null) {
            return null;
        }
        
        String formatted = name;
        
        if (version != null && !version.isEmpty() && !version.equals("unknown")) {
            formatted = String.format("%s (%s)", name, version);
        }
        
        return formatted;
    }
    
    /**
     * Attempts to retrieve the version of a plugin
     */
    private String getPluginVersion(JavaPlugin plugin) {
        if (plugin == null) {
            return "unknown";
        }
        
        // Try 1: Get version from manifest
        try {
            java.lang.reflect.Method getManifestMethod = plugin.getClass().getMethod("getManifest");
            Object manifest = getManifestMethod.invoke(plugin);
            if (manifest != null) {
                java.lang.reflect.Method getVersionMethod = manifest.getClass().getMethod("getVersion");
                Object version = getVersionMethod.invoke(manifest);
                if (version != null && !version.toString().isEmpty()) {
                    return version.toString();
                }
            }
        } catch (Exception e) {
            // Method doesn't exist or failed
        }
        
        // Try 2: Get version from plugin directly
        try {
            java.lang.reflect.Method getVersionMethod = plugin.getClass().getMethod("getVersion");
            Object version = getVersionMethod.invoke(plugin);
            if (version != null && !version.toString().isEmpty()) {
                return version.toString();
            }
        } catch (Exception e) {
            // Method doesn't exist or failed
        }
        
        return "unknown";
    }
}
