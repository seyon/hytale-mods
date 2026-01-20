package dev.seyon.motd.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all translatable strings used in the plugin
 */
public class TranslationManager {
    
    private final Map<String, String> translations;
    
    public TranslationManager() {
        this.translations = new HashMap<>();
        loadDefaults();
    }
    
    /**
     * Load default English translations (only for chat/notification output)
     */
    private void loadDefaults() {
        // Chat Messages
        translations.put("chat.installed_plugins_header", "=== Installed Plugins ===");
        translations.put("chat.motd_test_header", "=== MOTD Test ===");
        translations.put("chat.motd_test_footer", "=== End Test ===");
        translations.put("chat.messages_saved", "Messages saved!");
        translations.put("chat.settings_saved", "Settings saved!");
        translations.put("chat.max_messages_reached", "Max {0} messages reached!");
        
        // Notification
        translations.put("notification.plugins_prefix", "Plugins: ");
    }
    
    /**
     * Get a translation by key
     */
    public String get(String key) {
        return translations.getOrDefault(key, key);
    }
    
    /**
     * Get a translation with placeholders replaced
     */
    public String get(String key, Object... args) {
        String value = get(key);
        for (int i = 0; i < args.length; i++) {
            value = value.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return value;
    }
    
    /**
     * Set a translation
     */
    public void set(String key, String value) {
        translations.put(key, value);
    }
    
    /**
     * Get all translations (for saving/loading)
     */
    public Map<String, String> getAll() {
        return new HashMap<>(translations);
    }
    
    /**
     * Load translations from a map
     */
    public void loadAll(Map<String, String> translations) {
        this.translations.clear();
        loadDefaults(); // Load defaults first
        this.translations.putAll(translations); // Override with custom translations
    }
    
    /**
     * Reset to defaults
     */
    public void resetToDefaults() {
        translations.clear();
        loadDefaults();
    }
}
