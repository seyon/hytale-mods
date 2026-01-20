# Seyon-MOTD Plugin

A configurable MOTD (Message of the Day) plugin for Hytale servers with GUI management.

## Features

- 🎨 Configurable MOTD messages with color support (Hex codes)
- 💬 Display in chat or as notification
- 📋 Automatic plugin list display (configurable delay)
- ⏱️ 5-second delay before showing MOTD to players
- 🖥️ User-friendly GUI for management
- 🌍 Customizable translations for all UI texts
- 🔒 OP permissions required for configuration
- 💾 Persistent JSON-based configuration

## Installation

1. Copy the JAR `SeyonMotd-1.0.0.jar`
2. Place it in the `mods/` folder of your Hytale server
3. Start the server

## Usage

### Command

```
/seyon-motd
```
**Requires:** OP permissions

### GUI

The GUI has three tabs:

#### Messages Tab
- List of all MOTD messages
- **Text:** The message text
- **Color:** Hex color code (e.g. `#FFFFFF`, `#FFD700`, `#FF5733`)
- **ADD:** Add a new message (max 10)
- **REMOVE:** Remove a message
- **SAVE:** Save all message changes

#### Settings Tab
- **Show in Chat:** Display MOTD in chat on join
- **Show as Notification:** Display MOTD as notification on join
- **Show Plugin List after MOTD:** Delay in seconds (-1 = disabled, 0-60 seconds)
- **SAVE SETTINGS:** Save all setting changes

#### Translations Tab
- Customize all chat and notification texts
- Edit header/footer messages
- Localize plugin messages
- **SAVE:** Save translation changes

### Test Feature

Use the **TEST MOTD** button in the sidebar to preview your MOTD configuration without rejoining.

## Configuration File

The configuration is stored in `SeyonMotd/config.json`:

```json
{
  "messages": [
    { "text": "Welcome to our server!", "color": "#FFFFFF" },
    { "text": "Have fun playing!", "color": "#FFD700" }
  ],
  "showInChat": true,
  "showAsNotification": false,
  "showPluginListAfterSeconds": -1,
  "translations": {
    "chat.installed_plugins_header": "=== Installed Plugins ===",
    "chat.motd_test_header": "=== MOTD Test ===",
    "chat.motd_test_footer": "=== End Test ===",
    "chat.messages_saved": "Messages saved!",
    "chat.settings_saved": "Settings saved!",
    "chat.max_messages_reached": "Maximum of 10 messages reached!",
    "notification.plugins_prefix": "Plugins: "
  }
}
```

### Configuration Options

- **showInChat** (boolean): Display MOTD in chat
- **showAsNotification** (boolean): Display MOTD as notification popup
- **showPluginListAfterSeconds** (int): 
  - `-1`: Plugin list disabled
  - `0-60`: Show plugin list X seconds after MOTD
- **messages** (array): List of MOTD messages with text and color
- **translations** (object): Customizable text translations

## How It Works

1. Player joins the server
2. **5 seconds delay** before MOTD is sent
3. MOTD messages are displayed (chat and/or notification)
4. If configured (`showPluginListAfterSeconds ≠ -1`):
   - Wait additional X seconds
   - Display list of installed plugins (excludes Hytale core plugins)

## Features in Detail

### Plugin Discovery

The plugin automatically discovers all installed plugins using reflection:
- Accesses `pluginInit.classLoader.pluginManager.getPlugins()`
- Filters out Hytale core plugins (prefix: "Hytale:")
- Displays plugin name and version (from manifest)
- Clean logging for debugging

### Delayed Messaging

- **MOTD Delay:** 5-second fixed delay after player join
- **Plugin List Delay:** Configurable 0-60 seconds after MOTD
- Uses `ScheduledExecutorService` for thread-safe scheduling
- Executes on world thread using `CompletableFuture`

### Translation System

All user-facing texts are customizable:
- Chat messages (headers, confirmations)
- Notification texts
- Plugin list prefix
- Persistent storage in `config.json`
