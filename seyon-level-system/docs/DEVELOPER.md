# Seyon Level System - Developer Notes

## Project Structure

```
seyon-level-system/
├── src/main/java/dev/seyon/leveling/
│   ├── SeyonLevelSystemPlugin.java       # Main plugin entry point
│   ├── api/                               # Public API for other mods
│   │   ├── LevelSystemAPI.java           # API interface
│   │   └── LevelSystemAPIImpl.java      # API implementation
│   ├── command/                           # Command handlers
│   │   └── LevelSystemCommand.java      # Main /seyon-level command
│   ├── config/                            # Configuration classes
│   │   ├── ActionConfig.java             # Action-to-EXP mappings
│   │   ├── ExpCurveConfig.java           # EXP curve calculations
│   │   ├── LevelBonusConfig.java         # Level bonus definitions
│   │   ├── LevelSystemCategory.java     # Category configuration
│   │   ├── LevelSystemMainConfig.java   # Main config
│   │   ├── MilestoneQuestConfig.java     # Quest configurations
│   │   ├── QuestItemRequirement.java     # Quest item requirements
│   │   └── SkillConfig.java              # Skill definitions
│   ├── event/                             # Event handlers
│   │   └── LevelSystemEventHandler.java  # PlayerReady and other events
│   ├── gui/                               # GUI classes (placeholders)
│   │   ├── LevelSystemMainGui.java      # Main Level System GUI
│   │   ├── QuestDialogGui.java           # Quest dialog GUI
│   │   └── SkillTreeGui.java             # Skill tree GUI
│   ├── integration/                       # External mod integrations
│   │   ├── ItemTooltipExtension.java     # Item tooltip extension
│   │   └── MagicIntegration.java        # Seyon Arcane Arts integration
│   ├── model/                             # Data models
│   │   ├── CategoryProgress.java         # Per-category progress
│   │   └── PlayerLevelSystemData.java   # Player data model
│   └── service/                           # Business logic services
│       ├── ActionRegistryService.java    # Action registration
│       ├── CategoryService.java          # Category management
│       ├── DefaultConfigCreator.java     # Default config generator
│       ├── ExperienceService.java        # EXP and Level System logic
│       ├── LevelSystemConfigService.java # Config loading/saving
│       ├── LevelSystemDataService.java   # Player data persistence
│       ├── ModifierService.java          # Modifier calculations
│       ├── QuestService.java             # Quest management
│       └── SkillService.java             # Skill management
├── src/main/resources/
│   ├── manifest.json                      # Plugin manifest
│   └── Common/UI/Custom/Pages/SeyonLevelSystem/  # UI files
└── docs/
    ├── API.md                             # API documentation
    └── CONFIG.md                          # Configuration guide
```

## Implementation Status

### ✅ Complete
- Project setup and structure
- All data models (PlayerLevelSystemData, CategoryProgress, etc.)
- Configuration system (loading/saving JSON)
- All core services:
  - LevelSystemConfigService
  - CategoryService
  - LevelSystemDataService
  - ActionRegistryService
  - ExperienceService
  - SkillService
  - ModifierService
  - QuestService
- Public API (LevelSystemAPI + LevelSystemAPIImpl)
- Command system (/seyon-level with subcommands)
- Event handler (PlayerReady)
- Default category configs (Mining, Woodcutting, Combat x2, Exploration)
- Seyon Arcane Arts integration (auto-registration of magic category)
- Documentation (API.md, CONFIG.md, README.md)

### 🚧 TODO / Incomplete
- **GUI System**: Placeholder classes created, but need full implementation
  - LevelSystemMainGui - needs UI file and InteractiveCustomUIPage implementation
  - SkillTreeGui - needs UI file and InteractiveCustomUIPage implementation
  - QuestDialogGui - needs UI file and InteractiveCustomUIPage implementation
  - UI files (.ui) in resources/Common/UI/Custom/Pages/SeyonLevelSystem/
  
- **Event Handlers**: Placeholder only
  - BlockBreakEvent → grant mining/woodcutting EXP
  - EntityKillEvent → grant combat EXP
  - ItemCraftEvent → grant crafting EXP
  - ExploreEvent → grant exploration EXP
  - These require knowledge of actual Hytale event types
  
- **Item Tooltip Extension**: Placeholder only
  - Needs ItemHoverEvent or similar
  - Dynamic tooltip modification to show bonuses
  
- **Modifier Application**: Placeholder in ModifierService
  - Needs integration with Hytale's attribute system
  - Apply calculated modifiers to player attributes (HP, damage, speed, etc.)
  
- **Player Lookup**: Commands have TODOs for player lookup by name
  - Needed for admin commands (addexp, setlevel, resetskills)

## Key Design Decisions

1. **File-based Persistence**: Player data stored as JSON files in `SeyonLevelSystem/playerdata/`
   - Simple, reliable, human-readable
   - Could be upgraded to database in future

2. **Service-Oriented Architecture**: Business logic separated into focused services
   - Easy to test, maintain, and extend
   - Clear separation of concerns

3. **Config-Driven**: Everything configurable via JSON
   - Categories, skills, EXP curves, actions all in configs
   - No code changes needed for balance adjustments

4. **Extensible API**: Public API for other mods
   - Register categories, actions, grant EXP
   - Query player data, check skills
   - Well-documented in docs/API.md

5. **EXP Curve System**: Three curve types (linear, exponential, custom formula)
   - Flexible enough for different progression styles
   - Custom formula supports basic math expressions

6. **Tier-Based Skills**: Skills organized in tiers
   - Creates interesting progression
   - UI can be organized by tier

7. **Quest Milestones**: Two quest types (talk, item collection)
   - Gates progression at key levels
   - Adds narrative opportunity
   - Configurable per category

## Testing Notes

Since Hytale server is not fully released yet, testing requires:
1. Valid HytaleServer.jar in `../dependency/`
2. Gradle build: `./gradlew build`
3. JAR output in `release/` folder
4. Test on actual Hytale server

## Future Enhancements

Potential additions for v2.0:
- GUI implementation
- More event handlers for EXP sources
- Database persistence option
- Skill cooldowns/active abilities
- Party/group EXP sharing
- Leaderboards
- Prestige system (reset at max level for bonuses)
- Custom achievement system integration
- More quest types (kill X enemies, reach Y location)

## Notes for Continuation

If continuing this implementation:
1. **Priority 1**: GUI system - most visible feature
2. **Priority 2**: Event handlers - core gameplay loop
3. **Priority 3**: Modifier application - makes leveling meaningful
4. **Priority 4**: Player lookup - needed for admin commands

For GUI implementation, refer to:
- `seyon-motd/src/main/java/dev/seyon/motd/gui/MotdGui.java`
- `seyon-motd/src/main/resources/Common/UI/Custom/Pages/SeyonMotd/`

These provide a complete example of:
- InteractiveCustomUIPage usage
- UI file structure
- Event binding
- Data codec
- Tab switching
- Dynamic content

## Contact

Christian Wielath - https://seyon.de
