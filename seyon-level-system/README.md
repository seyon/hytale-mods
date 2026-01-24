# Seyon Level System

A comprehensive leveling system for Hytale with categories, skills, quests, and modifiers.

## Features

- **Multiple Categories**: Mining, Woodcutting, Combat (Melee/Ranged), Exploration, Magic, and more
- **Experience System**: Gain EXP by performing actions related to each category
- **Skill Trees**: Tier-based skill trees with unique abilities for each category
- **Level Bonuses**: Automatic global modifiers (health, mana, speed, etc.) per level
- **Quest Milestones**: NPC quests at specific level intervals to unlock further progression
- **Custom GUI**: User-friendly interface to view progress, manage skills, and track quests
- **Extensible API**: Other mods can add their own categories and actions
- **Configurable**: All categories, skills, and EXP curves are fully customizable via JSON

## Installation

1. Place the `SeyonLevelSystem.jar` in your Hytale server's plugins folder
2. Start the server to generate default configuration files
3. Customize the configuration in `SeyonLevelSystem/config/`

## Configuration

See the `docs/` folder for detailed configuration guides.

## Commands

- `/leveling` - Open the leveling GUI
- `/leveling stats [player]` - View leveling statistics
- `/leveling addexp <player> <category> <amount>` - Grant experience (admin)
- `/leveling setlevel <player> <category> <level>` - Set player level (admin)
- `/leveling reload` - Reload configuration

## API for Mod Developers

```java
LevelSystemAPI api = SeyonLevelSystemPlugin.getInstance().getAPI();
api.registerCategory(myCategory);
api.registerAction("custom_action", "my_category", 25.0);
```

See `docs/API.md` for full API documentation.

## License

MIT License - See LICENSE file for details.

## Author

Christian Wielath - [seyon.de](https://seyon.de)
