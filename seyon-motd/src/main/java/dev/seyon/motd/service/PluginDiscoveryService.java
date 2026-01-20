package dev.seyon.motd.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.seyon.motd.SeyonMotdPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service for discovering and retrieving loaded plugins from the Hytale server.
 * Uses direct reflection access to pluginInit.classLoader.pluginManager.
 */
public class PluginDiscoveryService {
    
    private final HytaleLogger logger;
    private final JavaPluginInit pluginInit;
    
    public PluginDiscoveryService(HytaleLogger logger, JavaPluginInit pluginInit) {
        this.logger = logger;
        this.pluginInit = pluginInit;
    }
    
    /**
     * Discovers all loaded plugins via direct classLoader.pluginManager access.
     * Path: pluginInit → classLoader (field) → pluginManager (field) → getPlugins() (method)
     * @return List of formatted plugin strings
     */
    public List<String> discoverAllPlugins() {
        logger.at(java.util.logging.Level.INFO).log("[PluginDiscovery] Starting plugin discovery via classLoader.pluginManager...");
        
        List<String> formattedPlugins = new ArrayList<>();
        
        if (pluginInit == null) {
            logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] pluginInit is null, using fallback");
            return getFallbackPluginList();
        }
        
        try {
            // Access classLoader field from pluginInit
            java.lang.reflect.Field classLoaderField = pluginInit.getClass().getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            Object classLoader = classLoaderField.get(pluginInit);
            
            if (classLoader == null) {
                logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] classLoader is null, using fallback");
                return getFallbackPluginList();
            }
            
            logger.at(java.util.logging.Level.INFO).log("[PluginDiscovery] Found classLoader: " + classLoader.getClass().getName());
            
            // Access pluginManager field from classLoader
            java.lang.reflect.Field pluginManagerField = classLoader.getClass().getDeclaredField("pluginManager");
            pluginManagerField.setAccessible(true);
            Object pluginManager = pluginManagerField.get(classLoader);
            
            if (pluginManager == null) {
                logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] pluginManager is null, using fallback");
                return getFallbackPluginList();
            }
            
            logger.at(java.util.logging.Level.INFO).log("[PluginDiscovery] Found pluginManager: " + pluginManager.getClass().getName());
            
            // Call getPlugins() method on pluginManager
            java.lang.reflect.Method getPluginsMethod = pluginManager.getClass().getMethod("getPlugins");
            Object result = getPluginsMethod.invoke(pluginManager);
            
            if (result instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<? extends JavaPlugin> plugins = (Collection<? extends JavaPlugin>) result;
                
                if (plugins != null && !plugins.isEmpty()) {
                    formattedPlugins = formatPlugins(plugins);
                    logger.at(java.util.logging.Level.INFO).log(String.format(
                        "[PluginDiscovery] SUCCESS: Found %d plugins via pluginInit.classLoader.pluginManager.getPlugins()", 
                        formattedPlugins.size()));
                    return formattedPlugins;
                }
            }
            
            logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] getPlugins() returned empty collection, using fallback");
            return getFallbackPluginList();
            
        } catch (Exception e) {
            logger.at(java.util.logging.Level.WARNING).log("[PluginDiscovery] Exception during discovery: " + e.getMessage());
            return getFallbackPluginList();
        }
    }
    
    /**
     * Returns a fallback plugin list containing at least this plugin
     */
    private List<String> getFallbackPluginList() {
        List<String> plugins = new ArrayList<>();
        JavaPlugin self = SeyonMotdPlugin.getInstance();
        if (self != null) {
            String formatted = formatPluginInfo(self.getName(), getPluginVersion(self));
            plugins.add(formatted != null ? formatted : "SeyonMotd");
        } else {
            plugins.add("SeyonMotd");
        }
        logger.at(java.util.logging.Level.INFO).log("[PluginDiscovery] Using fallback, returning " + plugins.size() + " plugin(s)");
        return plugins;
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
     * Formats plugin information as "name (version)" or "name (version) [UPDATE AVAILABLE]"
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
        
        // Try 3: Get version from descriptor
        try {
            java.lang.reflect.Method getDescriptorMethod = plugin.getClass().getMethod("getDescriptor");
            Object descriptor = getDescriptorMethod.invoke(plugin);
            if (descriptor != null) {
                java.lang.reflect.Method getVersionMethod = descriptor.getClass().getMethod("getVersion");
                Object version = getVersionMethod.invoke(descriptor);
                if (version != null && !version.toString().isEmpty()) {
                    return version.toString();
                }
            }
        } catch (Exception e) {
            // Method doesn't exist or failed
        }
        
        return "unknown";
    }
    
    /**
     * Attempts to retrieve the GitHub repository URL from the plugin's manifest
     * Returns null if no GitHub URL is found
     */
    private String getGitHubUrlFromManifest(JavaPlugin plugin) {
        if (plugin == null) {
            return null;
        }
        
        try {
            java.lang.reflect.Method getManifestMethod = plugin.getClass().getMethod("getManifest");
            Object manifest = getManifestMethod.invoke(plugin);
            
            if (manifest != null) {
                // Try common field names for repository URL
                String[] possibleFields = {"url", "repository", "repositoryUrl", "sourceUrl", "homepage", "website"};
                
                for (String fieldName : possibleFields) {
                    try {
                        // Try as method first (getUrl, getRepository, etc.)
                        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        java.lang.reflect.Method urlMethod = manifest.getClass().getMethod(methodName);
                        Object urlObj = urlMethod.invoke(manifest);
                        
                        if (urlObj != null) {
                            String url = urlObj.toString();
                            if (url.contains("github.com")) {
                                logger.at(java.util.logging.Level.FINE).log(String.format(
                                    "[PluginDiscovery] Found GitHub URL for %s: %s", plugin.getName(), url));
                                return url;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        // Try as field instead
                        try {
                            java.lang.reflect.Field urlField = manifest.getClass().getDeclaredField(fieldName);
                            urlField.setAccessible(true);
                            Object urlObj = urlField.get(manifest);
                            
                            if (urlObj != null) {
                                String url = urlObj.toString();
                                if (url.contains("github.com")) {
                                    logger.at(java.util.logging.Level.FINE).log(String.format(
                                        "[PluginDiscovery] Found GitHub URL for %s: %s", plugin.getName(), url));
                                    return url;
                                }
                            }
                        } catch (Exception e2) {
                            // Field doesn't exist, continue
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Manifest not available or error occurred
        }
        
        return null;
    }
    
    /**
     * Fetches the latest release tag from a GitHub repository
     * Returns null if no release is found or an error occurs
     */
    private String getLatestGitHubRelease(String githubUrl) {
        if (githubUrl == null || !githubUrl.contains("github.com")) {
            return null;
        }
        
        try {
            // Parse GitHub URL to get owner and repo
            // Format: https://github.com/owner/repo or https://github.com/owner/repo.git
            String cleanUrl = githubUrl.replace(".git", "").trim();
            String[] parts = cleanUrl.split("/");
            
            if (parts.length < 2) {
                return null;
            }
            
            String owner = parts[parts.length - 2];
            String repo = parts[parts.length - 1];
            
            // GitHub API endpoint for latest release
            String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);
            
            logger.at(java.util.logging.Level.FINE).log("[PluginDiscovery] Checking for updates: " + apiUrl);
            
            // Make HTTP request to GitHub API
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                // Parse JSON response to get tag_name
                String jsonResponse = response.toString();
                int tagNameIndex = jsonResponse.indexOf("\"tag_name\"");
                
                if (tagNameIndex != -1) {
                    int startQuote = jsonResponse.indexOf("\"", tagNameIndex + 11);
                    int endQuote = jsonResponse.indexOf("\"", startQuote + 1);
                    
                    if (startQuote != -1 && endQuote != -1) {
                        String tagName = jsonResponse.substring(startQuote + 1, endQuote);
                        logger.at(java.util.logging.Level.FINE).log("[PluginDiscovery] Latest release: " + tagName);
                        return tagName;
                    }
                }
            }
            
            conn.disconnect();
        } catch (Exception e) {
            logger.at(java.util.logging.Level.FINE).log("[PluginDiscovery] Error checking GitHub release: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Checks if a new version is available compared to the current version
     * Returns true if latestVersion is newer than currentVersion
     */
    private boolean isNewerVersion(String currentVersion, String latestVersion) {
        if (currentVersion == null || latestVersion == null) {
            return false;
        }
        
        // Remove 'v' prefix if present
        currentVersion = currentVersion.replaceFirst("^v", "");
        latestVersion = latestVersion.replaceFirst("^v", "");
        
        // Simple string comparison for now (works for semantic versioning)
        return !currentVersion.equals(latestVersion) && latestVersion.compareTo(currentVersion) > 0;
    }
}
