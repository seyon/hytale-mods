package dev.seyon.motd.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotdConfiguration extends BlockingDiskFile {

    private List<MotdMessage> messages;
    private boolean showInChat;
    private boolean showAsNotification;
    private int showPluginListAfterSeconds;
    private TranslationManager translations;

    public MotdConfiguration() {
        super(Path.of("SeyonMotd/config.json"));
        this.messages = new ArrayList<>();
        // Default messages
        this.messages.add(new MotdMessage("Welcome to our server!", "#FFFFFF"));
        this.messages.add(new MotdMessage("Have fun playing!", "#FFD700"));
        this.showInChat = true;
        this.showAsNotification = false;
        this.showPluginListAfterSeconds = -1; // Default: -1 (disabled)
        this.translations = new TranslationManager();
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        var element = JsonParser.parseReader(bufferedReader);
        
        // Check if the parsed element is null or not a JSON object
        if (element == null || !element.isJsonObject()) {
            // File is empty or invalid, keep default values
            return;
        }
        
        var object = element.getAsJsonObject();
        
        this.showInChat = object.get("showInChat").getAsBoolean();
        this.showAsNotification = object.get("showAsNotification").getAsBoolean();
        
        // Migration: convert old boolean showPluginList to new int showPluginListAfterSeconds
        if (object.has("showPluginListAfterSeconds")) {
            this.showPluginListAfterSeconds = object.get("showPluginListAfterSeconds").getAsInt();
        } else if (object.has("showPluginList")) {
            // Migrate from old boolean format: true = 3 seconds, false = -1 (disabled)
            boolean oldValue = object.get("showPluginList").getAsBoolean();
            this.showPluginListAfterSeconds = oldValue ? 3 : -1;
        } else {
            this.showPluginListAfterSeconds = -1; // Default: disabled
        }
        
        this.messages.clear();
        JsonArray messagesArray = object.getAsJsonArray("messages");
        for (int i = 0; i < messagesArray.size(); i++) {
            JsonObject msgObj = messagesArray.get(i).getAsJsonObject();
            String text = msgObj.get("text").getAsString();
            String color = msgObj.get("color").getAsString();
            this.messages.add(new MotdMessage(text, color));
        }
        
        // Load translations
        if (object.has("translations")) {
            JsonObject translationsObj = object.getAsJsonObject("translations");
            Map<String, String> translationsMap = new HashMap<>();
            for (String key : translationsObj.keySet()) {
                translationsMap.put(key, translationsObj.get(key).getAsString());
            }
            this.translations.loadAll(translationsMap);
        }
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("showInChat", this.showInChat);
        object.addProperty("showAsNotification", this.showAsNotification);
        object.addProperty("showPluginListAfterSeconds", this.showPluginListAfterSeconds);
        
        JsonArray messagesArray = new JsonArray();
        for (MotdMessage message : this.messages) {
            JsonObject msgObj = new JsonObject();
            msgObj.addProperty("text", message.text());
            msgObj.addProperty("color", message.color());
            messagesArray.add(msgObj);
        }
        object.add("messages", messagesArray);
        
        // Save translations
        JsonObject translationsObj = new JsonObject();
        for (Map.Entry<String, String> entry : this.translations.getAll().entrySet()) {
            translationsObj.addProperty(entry.getKey(), entry.getValue());
        }
        object.add("translations", translationsObj);
        
        bufferedWriter.write(object.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        write(bufferedWriter);
    }

    public List<MotdMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<MotdMessage> messages) {
        this.messages = messages;
    }

    public boolean isShowInChat() {
        return showInChat;
    }

    public void setShowInChat(boolean showInChat) {
        this.showInChat = showInChat;
    }

    public boolean isShowAsNotification() {
        return showAsNotification;
    }

    public void setShowAsNotification(boolean showAsNotification) {
        this.showAsNotification = showAsNotification;
    }

    public int getShowPluginListAfterSeconds() {
        return showPluginListAfterSeconds;
    }

    public void setShowPluginListAfterSeconds(int showPluginListAfterSeconds) {
        this.showPluginListAfterSeconds = showPluginListAfterSeconds;
    }
    
    /**
     * Convenience method to check if plugin list should be shown
     */
    public boolean shouldShowPluginList() {
        return showPluginListAfterSeconds != -1;
    }

    public TranslationManager getTranslations() {
        return translations;
    }

    /**
     * Convert hex color string to Color object
     */
    public static Color hexToColor(String hex) {
        try {
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16)
            );
        } catch (Exception e) {
            return Color.WHITE; // fallback to white
        }
    }

    /**
     * Record representing a single MOTD message
     */
    public record MotdMessage(String text, String color) {
    }
}
