# Seyon-MOTD Plugin

A configurable MOTD (Message of the Day) plugin for Hytale servers with JSON-based configuration.

## ⚠️ Important: Configuration via JSON Files Only

**This plugin has NO in-game GUI.** All configuration is done through JSON files.

## Features

- 🎨 Configurable MOTD messages with color support (Hex codes)
- 💬 Display in chat or as notification
- 📋 Automatic plugin list display (configurable delay)
- ⏱️ 5-second delay before showing MOTD to players
- 🌍 Customizable translations for all texts
- 💾 Persistent JSON-based configuration
- 🔄 Auto-reload on server start

## Installation

1. **Required Dependency:** Install `seyon-core` plugin first
2. Copy `SeyonMotd-1.0.0.jar` to the `mods/` folder
3. Start the server - configuration file will be created automatically
4. Edit `SeyonMotd/motd-config.json` to customize settings

## Configuration

The configuration file is located at: `SeyonMotd/motd-config.json`

### Example Configuration

```json
{
  "messages": [
    { "text": "Welcome to our server!", "color": "#FFFFFF" },
    { "text": "Have fun playing!", "color": "#FFD700" },
    { "text": "Join our Discord!", "color": "#5865F2" }
  ],
  "showInChat": true,
  "showAsNotification": false,
  "showPluginListAfterSeconds": -1,
  "translations": {
    "chat.installed_plugins_header": "=== Installed Plugins ===",
    "notification.plugins_prefix": "Plugins: "
  }
}
```

### Configuration Options

#### Display Settings

- **`showInChat`** (boolean): Display MOTD in chat window
  - `true` = Show in chat
  - `false` = Don't show in chat

- **`showAsNotification`** (boolean): Display MOTD as notification popup
  - `true` = Show as notification
  - `false` = Don't show as notification

- **`showPluginListAfterSeconds`** (integer): Show plugin list after MOTD
  - `-1` = Plugin list disabled
  - `0-60` = Show plugin list X seconds after MOTD

#### Messages

Array of message objects with:
- **`text`** (string): The message content
- **`color`** (string): Hex color code (e.g., `#FFFFFF`, `#FFD700`, `#FF5733`)

**Maximum:** 10 messages

#### Translations

Customize text strings:
- `chat.installed_plugins_header` - Header for plugin list in chat
- `notification.plugins_prefix` - Prefix for plugin list in notification

### Applying Changes

1. Edit `SeyonMotd/motd-config.json`
2. Save the file
3. Restart the server (or reload plugins if supported)

**Note:** Changes require a server restart to take effect.

## How It Works

1. Player joins the server
2. **5 seconds delay** (built-in)
3. MOTD messages are displayed (chat and/or notification)
4. If configured (`showPluginListAfterSeconds ≠ -1`):
   - Wait additional X seconds
   - Display list of installed plugins (excludes Hytale core plugins)

## Plugin List Feature

The plugin list automatically:
- Discovers all installed plugins via Core module
- Filters out Hytale core plugins (prefix: "Hytale:")
- Displays plugin name and version
- Shows in chat or notification (based on settings)

## Color Codes

Use hex color codes for messages:

| Color | Hex Code | Preview |
|-------|----------|---------|
| White | `#FFFFFF` | ⬜ |
| Gold | `#FFD700` | 🟨 |
| Red | `#FF0000` | 🟥 |
| Green | `#00FF00` | 🟩 |
| Blue | `#0000FF` | 🟦 |
| Purple | `#9B59B6` | 🟪 |
| Orange | `#FF8800` | 🟧 |

## Troubleshooting

### MOTD not showing
- Check `showInChat` and `showAsNotification` are not both `false`
- Verify JSON syntax is correct
- Check server logs for errors

### Plugin list not showing
- Ensure `showPluginListAfterSeconds` is not `-1`
- Verify `seyon-core` is installed and running
- Check server logs

### Configuration not loading
- Verify JSON syntax (use a JSON validator)
- Check file location: `SeyonMotd/motd-config.json`
- Ensure proper file permissions

## Dependencies

- **seyon-core** (required) - Provides plugin discovery service

## License

MIT License - See LICENSE file

## Author

Christian Wielath - [seyon.de](https://seyon.de)
