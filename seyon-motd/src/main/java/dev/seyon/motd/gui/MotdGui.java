package dev.seyon.motd.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.motd.SeyonMotdPlugin;
import dev.seyon.motd.config.MotdConfiguration;
import dev.seyon.motd.config.TranslationManager;
import dev.seyon.motd.event.MotdEventHandler;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MotdGui extends InteractiveCustomUIPage<MotdGui.GuiData> {

    private enum Tab {
        MESSAGES,
        SETTINGS,
        TRANSLATIONS
    }

    private Tab currentTab;
    private List<MessageEntry> messageEntries;
    private boolean showInChat;
    private boolean showAsNotification;
    private int showPluginListAfterSeconds;
    
    // Local state for translations (key -> value)
    private final java.util.HashMap<String, String> translationValues = new java.util.HashMap<>();

    public MotdGui(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
        this.currentTab = Tab.MESSAGES;
        
        // Load current configuration
        MotdConfiguration config = SeyonMotdPlugin.getInstance().getConfiguration();
        this.showInChat = config.isShowInChat();
        this.showAsNotification = config.isShowAsNotification();
        this.showPluginListAfterSeconds = config.getShowPluginListAfterSeconds();
        this.messageEntries = new ArrayList<>();
        
        for (MotdConfiguration.MotdMessage msg : config.getMessages()) {
            this.messageEntries.add(new MessageEntry(msg.text(), msg.color()));
        }
        
        // Load current translation values
        TranslationManager t = config.getTranslations();
        translationValues.put("chat.installed_plugins_header", t.get("chat.installed_plugins_header"));
        translationValues.put("chat.motd_test_header", t.get("chat.motd_test_header"));
        translationValues.put("chat.motd_test_footer", t.get("chat.motd_test_footer"));
        translationValues.put("chat.messages_saved", t.get("chat.messages_saved"));
        translationValues.put("chat.settings_saved", t.get("chat.settings_saved"));
        translationValues.put("chat.max_messages_reached", t.get("chat.max_messages_reached"));
        translationValues.put("notification.plugins_prefix", t.get("notification.plugins_prefix"));
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/SeyonMotd/SeyonMotd_MainPage.ui");
        
        // Tab buttons - these are always active
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MessagesTab", EventData.of("TabSwitch", "Messages"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SettingsTab", EventData.of("TabSwitch", "Settings"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TranslationsTab", EventData.of("TabSwitch", "Translations"));
        
        // Test MOTD button
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TestMotdButton", EventData.of("Button", "TestMotd"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("Button", "Back"));

        // Build tab content based on current tab
        this.buildTabContent(ref, uiCommandBuilder, uiEventBuilder, store);
    }
    
    private void buildTabContent(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        // Hide all tabs first
        uiCommandBuilder.set("#MessagesTabContent.Visible", false);
        uiCommandBuilder.set("#SettingsTabContent.Visible", false);
        uiCommandBuilder.set("#TranslationsTabContent.Visible", false);
        
        // Build and show only the current tab
        if (currentTab == Tab.MESSAGES) {
            uiCommandBuilder.set("#MessagesTabContent.Visible", true);
            buildMessagesTab(ref, uiCommandBuilder, uiEventBuilder, componentAccessor);
        } else if (currentTab == Tab.SETTINGS) {
            uiCommandBuilder.set("#SettingsTabContent.Visible", true);
            buildSettingsTab(ref, uiCommandBuilder, uiEventBuilder, componentAccessor);
        } else if (currentTab == Tab.TRANSLATIONS) {
            uiCommandBuilder.set("#TranslationsTabContent.Visible", true);
            buildTranslationsTab(ref, uiCommandBuilder, uiEventBuilder, componentAccessor);
        }
    }

    private void buildMessagesTab(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        // Add Message and Save buttons
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#AddMessageButton", EventData.of("Button", "AddMessage"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveMessagesButton", EventData.of("Button", "SaveMessages"));
        
        // Clear existing messages first!
        uiCommandBuilder.clear("#MessagesCards");
        
        // Populate message entries
        for (int i = 0; i < messageEntries.size(); i++) {
            MessageEntry entry = messageEntries.get(i);
            uiCommandBuilder.append("#MessagesCards", "Pages/SeyonMotd/SeyonMotd_MessageEntry.ui");
            
            String messageFieldPath = "#MessagesCards[" + i + "] #MessageTextField";
            String colorFieldPath = "#MessagesCards[" + i + "] #ColorTextField";
            String removeButtonPath = "#MessagesCards[" + i + "] #RemoveMessageButton";
            
            uiCommandBuilder.set(messageFieldPath + ".Value", entry.text);
            uiCommandBuilder.set(colorFieldPath + ".Value", entry.color);
            
            // Use @-prefix for keys when binding values, like in ImageImportPage
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, messageFieldPath, EventData.of("@MessageText" + i, messageFieldPath + ".Value"), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, colorFieldPath, EventData.of("@MessageColor" + i, colorFieldPath + ".Value"), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, removeButtonPath, EventData.of("RemoveMessage", String.valueOf(i)));
        }
    }

    private void buildSettingsTab(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        uiCommandBuilder.set("#ShowInChatCheckbox.Value", showInChat);
        uiCommandBuilder.set("#ShowAsNotificationCheckbox.Value", showAsNotification);
        uiCommandBuilder.set("#PluginListDelayField.Value", String.valueOf(showPluginListAfterSeconds));
        
        // For checkboxes: just send an identifier to toggle the state locally (like PluginListPage)
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#ShowInChatCheckbox", new EventData().append("Setting", "ShowInChat"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#ShowAsNotificationCheckbox", new EventData().append("Setting", "ShowAsNotification"), false);
        
        // For plugin list delay field: bind value changes
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PluginListDelayField", EventData.of("@PluginListDelay", "#PluginListDelayField.Value"), false);
        
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveSettingsButton", EventData.of("Button", "SaveSettings"));
    }

    private void buildTranslationsTab(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveTranslationsButton", EventData.of("Button", "SaveTranslations"));
        
        // Clear existing translations first!
        uiCommandBuilder.clear("#TranslationsCards");
        
        // Add translation entries (only for chat/notification texts)
        String[] keys = {
            "chat.installed_plugins_header",
            "chat.motd_test_header",
            "chat.motd_test_footer",
            "chat.messages_saved",
            "chat.settings_saved",
            "chat.max_messages_reached",
            "notification.plugins_prefix"
        };
        
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            // Use local state for translations (like messageEntries for messages)
            String value = translationValues.getOrDefault(key, "");
            
            uiCommandBuilder.append("#TranslationsCards", "Pages/SeyonMotd/SeyonMotd_TranslationEntry.ui");
            
            String keyLabelPath = "#TranslationsCards[" + i + "] #TranslationKeyLabel";
            String valueFieldPath = "#TranslationsCards[" + i + "] #TranslationValueField";
            
            uiCommandBuilder.set(keyLabelPath + ".Text", key);
            uiCommandBuilder.set(valueFieldPath + ".Value", value);
            
            // Map key to CamelCase field name with @-prefix (like ImageImportPage)
            String fieldKey = switch (key) {
                case "chat.installed_plugins_header" -> "@TranslationChatInstalledPluginsHeader";
                case "chat.motd_test_header" -> "@TranslationChatMotdTestHeader";
                case "chat.motd_test_footer" -> "@TranslationChatMotdTestFooter";
                case "chat.messages_saved" -> "@TranslationChatMessagesSaved";
                case "chat.settings_saved" -> "@TranslationChatSettingsSaved";
                case "chat.max_messages_reached" -> "@TranslationChatMaxMessagesReached";
                case "notification.plugins_prefix" -> "@TranslationNotificationPluginsPrefix";
                default -> "@Unknown";
            };
            
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, valueFieldPath, EventData.of(fieldKey, valueFieldPath + ".Value"), false);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);
        Player player = store.getComponent(ref, Player.getComponentType());
        MotdConfiguration config = SeyonMotdPlugin.getInstance().getConfiguration();
        
        // FIRST: Always update message text and colors from CODEC fields (before any return statements!)
        for (int i = 0; i < Math.min(messageEntries.size(), 10); i++) {
            String textKey = "MessageText" + i;
            String colorKey = "MessageColor" + i;
            
            if (data.messageUpdates.containsKey(textKey)) {
                String newText = data.messageUpdates.get(textKey);
                if (newText != null && !newText.isEmpty()) {
                    messageEntries.get(i).text = newText;
                }
            }
            if (data.messageUpdates.containsKey(colorKey)) {
                String newColor = data.messageUpdates.get(colorKey);
                if (newColor != null && !newColor.isEmpty()) {
                    messageEntries.get(i).color = newColor;
                }
            }
        }
        
        // FIRST: Always update translation values in local state (before any return statements!)
        for (var entry : data.translationUpdates.entrySet()) {
            if (entry.getValue() != null) {
                translationValues.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Update plugin list delay from field
        if (data.pluginListDelay != null && !data.pluginListDelay.isEmpty()) {
            try {
                showPluginListAfterSeconds = Integer.parseInt(data.pluginListDelay);
                // Clamp to reasonable range (-1 or 0-60 seconds)
                if (showPluginListAfterSeconds < -1) showPluginListAfterSeconds = -1;
                if (showPluginListAfterSeconds > 60) showPluginListAfterSeconds = 60;
            } catch (NumberFormatException e) {
                // Invalid number, keep current value
            }
        }
        
        // Handle tab switching
        if (data.tabSwitch != null) {
            currentTab = switch (data.tabSwitch) {
                case "Settings" -> Tab.SETTINGS;
                case "Translations" -> Tab.TRANSLATIONS;
                default -> Tab.MESSAGES;
            };
            
            // Rebuild UI with new tab
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildTabContent(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, false);
            return;
        }
        
        // Handle settings checkbox toggle (like PluginListPage pattern)
        if (data.setting != null) {
            switch (data.setting) {
                case "ShowInChat" -> showInChat = !showInChat;
                case "ShowAsNotification" -> showAsNotification = !showAsNotification;
            }
            this.sendUpdate();
            return;
        }
        
        // Handle button clicks
        if (data.button != null) {
            switch (data.button) {
                case "Back": {
                    Player p = store.getComponent(ref, Player.getComponentType());
                    if (p != null) {
                        p.getPageManager().setPage(ref, store, Page.None);
                    }
                    return;
                }
                case "AddMessage":
                    if (messageEntries.size() < 10) {
                        messageEntries.add(new MessageEntry("New Message", "#FFFFFF"));
                        
                        // Rebuild UI
                        UICommandBuilder commandBuilder = new UICommandBuilder();
                        UIEventBuilder eventBuilder = new UIEventBuilder();
                        this.buildTabContent(ref, commandBuilder, eventBuilder, store);
                        this.sendUpdate(commandBuilder, eventBuilder, false);
                    } else {
                        player.sendMessage(Message.raw(translationValues.getOrDefault("chat.max_messages_reached", "Maximum of 10 messages reached!")).color(Color.RED));
                        this.sendUpdate();
                    }
                    return;
                    
                case "SaveMessages":
                    config.getMessages().clear();
                    for (MessageEntry entry : messageEntries) {
                        config.getMessages().add(new MotdConfiguration.MotdMessage(entry.text, entry.color));
                    }
                    config.syncSave();
                    config.syncLoad(); // Reload to ensure persistence
                    player.sendMessage(Message.raw(translationValues.getOrDefault("chat.messages_saved", "Messages saved!")).color(Color.GREEN));
                    this.sendUpdate();
                    return;
                    
                case "SaveSettings":
                    config.setShowInChat(showInChat);
                    config.setShowAsNotification(showAsNotification);
                    config.setShowPluginListAfterSeconds(showPluginListAfterSeconds);
                    config.syncSave();
                    config.syncLoad(); // Reload to ensure persistence
                    
                    // Log saved config to verify persistence
                    SeyonMotdPlugin.getInstance().getLogger().at(java.util.logging.Level.INFO)
                        .log(String.format("MOTD Config - showInChat: %s, showAsNotification: %s, showPluginListAfterSeconds: %d", 
                            config.isShowInChat(), config.isShowAsNotification(), config.getShowPluginListAfterSeconds()));
                    
                    player.sendMessage(Message.raw(translationValues.getOrDefault("chat.settings_saved", "Settings saved!")).color(Color.GREEN));
                    this.sendUpdate();
                    return;
                    
                case "SaveTranslations":
                    // Save translations from local state (updated by ValueChanged events)
                    for (var entry : translationValues.entrySet()) {
                        config.getTranslations().set(entry.getKey(), entry.getValue());
                    }
                    config.syncSave();
                    config.syncLoad(); // Reload to ensure persistence
                    player.sendMessage(Message.raw("Translations saved!").color(Color.GREEN));
                    this.sendUpdate();
                    return;
                    
                case "TestMotd":
                    MotdConfiguration testConfig = new MotdConfiguration();
                    testConfig.setShowInChat(showInChat);
                    testConfig.setShowAsNotification(showAsNotification);
                    testConfig.setShowPluginListAfterSeconds(showPluginListAfterSeconds);
                    
                    for (MessageEntry entry : messageEntries) {
                        testConfig.getMessages().add(new MotdConfiguration.MotdMessage(entry.text, entry.color));
                    }
                    
                    // Copy local translation values to testConfig
                    for (var entry : translationValues.entrySet()) {
                        testConfig.getTranslations().set(entry.getKey(), entry.getValue());
                    }
                    
                    // Use local translationValues for header/footer
                    player.sendMessage(Message.raw(translationValues.getOrDefault("chat.motd_test_header", "=== MOTD Test ===")).color(Color.CYAN).bold(true));
                    MotdEventHandler.sendMotd(player, testConfig);
                    player.sendMessage(Message.raw(translationValues.getOrDefault("chat.motd_test_footer", "=== End Test ===")).color(Color.CYAN).bold(true));
                    this.sendUpdate();
                    return;
            }
            this.sendUpdate();
            return;
        }
        
        // Handle message removal
        if (data.removeMessage != null) {
            try {
                int index = Integer.parseInt(data.removeMessage);
                if (index >= 0 && index < messageEntries.size()) {
                    messageEntries.remove(index);
                    
                    // Rebuild UI
                    UICommandBuilder commandBuilder = new UICommandBuilder();
                    UIEventBuilder eventBuilder = new UIEventBuilder();
                    this.buildTabContent(ref, commandBuilder, eventBuilder, store);
                    this.sendUpdate(commandBuilder, eventBuilder, false);
                    return;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid index
            }
            this.sendUpdate();
            return;
        }
        
        // If we reach here, it was just a value update (message text/color or translation)
        // Send empty update to prevent loading overlay from getting stuck
        this.sendUpdate();
    }

    private static class MessageEntry {
        String text;
        String color;

        MessageEntry(String text, String color) {
            this.text = text;
            this.color = color;
        }
    }

    public static class GuiData {
        private String tabSwitch;
        private String button;
        private String removeMessage;
        private String setting; // For checkbox toggle events
        private String pluginListDelay; // For plugin list delay field
        private final java.util.HashMap<String, String> messageUpdates = new java.util.HashMap<>();
        private final java.util.HashMap<String, String> translationUpdates = new java.util.HashMap<>();

        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.<GuiData>builder(GuiData.class, GuiData::new)
                .addField(new KeyedCodec<>("TabSwitch", Codec.STRING), (data, s) -> data.tabSwitch = s, data -> data.tabSwitch)
                .addField(new KeyedCodec<>("Button", Codec.STRING), (data, s) -> data.button = s, data -> data.button)
                .addField(new KeyedCodec<>("RemoveMessage", Codec.STRING), (data, s) -> data.removeMessage = s, data -> data.removeMessage)
                .addField(new KeyedCodec<>("Setting", Codec.STRING), (data, s) -> data.setting = s, data -> data.setting)
                .addField(new KeyedCodec<>("@PluginListDelay", Codec.STRING), (data, s) -> data.pluginListDelay = s, data -> data.pluginListDelay)
                // Message fields with @-prefix (support up to 10 messages)
                .addField(new KeyedCodec<>("@MessageText0", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText0", s), data -> data.messageUpdates.get("MessageText0"))
                .addField(new KeyedCodec<>("@MessageColor0", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor0", s), data -> data.messageUpdates.get("MessageColor0"))
                .addField(new KeyedCodec<>("@MessageText1", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText1", s), data -> data.messageUpdates.get("MessageText1"))
                .addField(new KeyedCodec<>("@MessageColor1", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor1", s), data -> data.messageUpdates.get("MessageColor1"))
                .addField(new KeyedCodec<>("@MessageText2", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText2", s), data -> data.messageUpdates.get("MessageText2"))
                .addField(new KeyedCodec<>("@MessageColor2", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor2", s), data -> data.messageUpdates.get("MessageColor2"))
                .addField(new KeyedCodec<>("@MessageText3", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText3", s), data -> data.messageUpdates.get("MessageText3"))
                .addField(new KeyedCodec<>("@MessageColor3", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor3", s), data -> data.messageUpdates.get("MessageColor3"))
                .addField(new KeyedCodec<>("@MessageText4", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText4", s), data -> data.messageUpdates.get("MessageText4"))
                .addField(new KeyedCodec<>("@MessageColor4", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor4", s), data -> data.messageUpdates.get("MessageColor4"))
                .addField(new KeyedCodec<>("@MessageText5", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText5", s), data -> data.messageUpdates.get("MessageText5"))
                .addField(new KeyedCodec<>("@MessageColor5", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor5", s), data -> data.messageUpdates.get("MessageColor5"))
                .addField(new KeyedCodec<>("@MessageText6", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText6", s), data -> data.messageUpdates.get("MessageText6"))
                .addField(new KeyedCodec<>("@MessageColor6", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor6", s), data -> data.messageUpdates.get("MessageColor6"))
                .addField(new KeyedCodec<>("@MessageText7", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText7", s), data -> data.messageUpdates.get("MessageText7"))
                .addField(new KeyedCodec<>("@MessageColor7", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor7", s), data -> data.messageUpdates.get("MessageColor7"))
                .addField(new KeyedCodec<>("@MessageText8", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText8", s), data -> data.messageUpdates.get("MessageText8"))
                .addField(new KeyedCodec<>("@MessageColor8", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor8", s), data -> data.messageUpdates.get("MessageColor8"))
                .addField(new KeyedCodec<>("@MessageText9", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageText9", s), data -> data.messageUpdates.get("MessageText9"))
                .addField(new KeyedCodec<>("@MessageColor9", Codec.STRING), (data, s) -> data.messageUpdates.put("MessageColor9", s), data -> data.messageUpdates.get("MessageColor9"))
                // Translation fields with @-prefix - store in HashMap like messages
                .addField(new KeyedCodec<>("@TranslationChatInstalledPluginsHeader", Codec.STRING), (data, s) -> data.translationUpdates.put("chat.installed_plugins_header", s), data -> data.translationUpdates.get("chat.installed_plugins_header"))
                .addField(new KeyedCodec<>("@TranslationChatMotdTestHeader", Codec.STRING), (data, s) -> data.translationUpdates.put("chat.motd_test_header", s), data -> data.translationUpdates.get("chat.motd_test_header"))
                .addField(new KeyedCodec<>("@TranslationChatMotdTestFooter", Codec.STRING), (data, s) -> data.translationUpdates.put("chat.motd_test_footer", s), data -> data.translationUpdates.get("chat.motd_test_footer"))
                .addField(new KeyedCodec<>("@TranslationChatMessagesSaved", Codec.STRING), (data, s) -> data.translationUpdates.put("chat.messages_saved", s), data -> data.translationUpdates.get("chat.messages_saved"))
                .addField(new KeyedCodec<>("@TranslationChatSettingsSaved", Codec.STRING), (data, s) -> data.translationUpdates.put("chat.settings_saved", s), data -> data.translationUpdates.get("chat.settings_saved"))
                .addField(new KeyedCodec<>("@TranslationChatMaxMessagesReached", Codec.STRING), (data, s) -> data.translationUpdates.put("chat.max_messages_reached", s), data -> data.translationUpdates.get("chat.max_messages_reached"))
                .addField(new KeyedCodec<>("@TranslationNotificationPluginsPrefix", Codec.STRING), (data, s) -> data.translationUpdates.put("notification.plugins_prefix", s), data -> data.translationUpdates.get("notification.plugins_prefix"))
                .build();
    }
}
