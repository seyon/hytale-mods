# Hytale Modding - AI Agent Guide

This file collects important insights and best practices for Hytale mod development. It is intended to be continuously expanded as new insights are gained.

## ⚠️ IMPORTANT: Build Process

**GRADLE BUILD IS NEVER EXECUTED AUTOMATICALLY!**

- **NEVER** automatically execute `gradle build`, `./gradlew build`, or similar build commands.
- The user **ALWAYS** performs the build process **MANUALLY**.
- After making code changes, inform the user that they can start the build manually.
- Only `gradle compileJava` is acceptable for checking syntax errors (if explicitly requested).

## Player UUID - The Correct Pattern

### ❌ Outdated (Deprecated)
```java
UUID uuid = player.getUuid(); // Deprecated, marked for removal
```

### ✅ Modern (Component System)
```java
// Use helper method (see PlayerUtils.java)
UUID uuid = PlayerUtils.getPlayerUUID(player);

// Or manually:
Ref<EntityStore> ref = player.getReference();
Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
UUID uuid = uuidComponent.getUuid();
```

**Important:** Hytale is migrating from legacy methods to a component-based system. Always use the component system!

**BUT:** The component system only works in the World thread! In async commands, the deprecated `getUuid()` must be used.

### PlayerUtils Helper Class
A `PlayerUtils` class has been created with the method `getPlayerUUID(Player player)`, which retrieves the UUID via the component system. This class should be used for all UUID accesses.

### When to Use Which Method?

```java
// ✅ In Event Handlers (World Thread)
UUID uuid = PlayerUtils.getPlayerUUID(player);

// ✅ In Async Commands (Outside World Thread) 
UUID uuid = player.getUuid(); // deprecated but necessary

// ✅ In Synchronous Commands (World Thread)
UUID uuid = PlayerUtils.getPlayerUUID(player);
```

## Colors for Messages

### ❌ Not Available
```java
Color.GOLD // does not exist in java.awt.Color
```

### ✅ Available Alternatives
```java
Color.ORANGE  // Use instead of GOLD
Color.YELLOW
Color.GREEN
Color.RED
Color.CYAN
Color.GRAY
```

## Entity Component System (ECS)

Hytale uses an Entity Component System for all game objects:

- **Entities** do not have direct properties, but **Components**.
- Components are retrieved via `ComponentType` and `ComponentAccessor`.
- Example Components:
  - `UUIDComponent` - Unique ID of an entity.
  - `TransformComponent` - Position and rotation.
  - `Player` - Player-specific data.
  - `PlayerRef` - Reference to a player.

### Typical Pattern
```java
// Retrieve component
Ref<EntityStore> ref = entity.getReference();
Store<EntityStore> store = world.getEntityStore().getStore();
SomeComponent component = store.getComponent(ref, SomeComponent.getComponentType());
```

## Command System

### Command Arguments
```java
// Required Argument
RequiredArg<String> arg = this.withRequiredArg("name", "description", ArgTypes.STRING);
String value = arg.get(context);

// Optional Argument  
OptionalArg<Integer> arg = this.withOptionalArg("name", "description", ArgTypes.INTEGER);
Integer value = arg.get(context); // can be null
```

### Available ArgTypes
- `ArgTypes.STRING`
- `ArgTypes.INTEGER`
- `ArgTypes.DOUBLE`
- `ArgTypes.BOOLEAN`
- More in the Hytale API documentation.

## Inventory System

```java
Inventory inventory = player.getInventory();
ItemContainer storageContainer = inventory.getStorage();

// Add/remove items
storageContainer.addItemStack(item);
storageContainer.removeItemStack(item);
```

See: https://hytalemodding.dev/en/docs/guides/plugin/inventory-management

## Data Persistence

### Saving Player Data
Hytale does not have a built-in PlayerStorage system for custom data. Recommended approach:

1. **JSON-based persistence** using Gson.
2. **UUID-based files**: `playerdata/{uuid}.json`
3. **In-memory cache** for active players.
4. **Save on**: Disconnect, server shutdown, periodically.

```java
// Example structure
SeyonLeveling/
  playerdata/
    {uuid}.json
  config/
    main.json
    categories/
      mining.json
    actions/
      mining_actions.json
```

## Event System

### Registering Events
```java
// In Plugin setup()
this.getEventRegistry().registerGlobal(
    PlayerReadyEvent.class, 
    event -> handler.onPlayerReady(event)
);
```

### Important Events
- `PlayerReadyEvent` - Player is fully loaded.
- `BlockBreakEvent` - Block is being mined (not yet available in Beta).
- `EntityKillEvent` - Entity is being killed (not yet available in Beta).

**Note:** Many events are not yet available in the current Hytale Beta!

## Plugin Structure

### Best Practices
- **Service Layer Pattern**: Offload logic to services (not in commands/events).
- **Dependency Injection**: Inject services via constructor.
- **Configuration**: JSON-based with Gson.
- **Logging**: Use HytaleLogger.

### Typical Folder Structure
```
seyon-example/
  src/main/
    java/dev/seyon/example/
      command/          # Commands
      config/           # Config classes
      event/            # Event handlers
      gui/              # GUI classes
      integration/      # Integration with other mods
      model/            # Data models
      service/          # Business logic
      SeyonExamplePlugin.java
    resources/
      manifest.json     # Plugin metadata
      Server/
        Item/Items/     # Custom items
      Common/
        UI/Custom/      # Custom UI
  build.gradle
  gradle.properties

seyon-utils/            # ⭐ Central utils for all mods
  src/main/
    java/dev/seyon/utils/
      PlayerUtils.java
```

## Multi-Project Setup

### settings.gradle
```gradle
include 'seyon-utils'       // Shared utilities
include 'seyon-magic'
include 'seyon-motd'
include 'seyon-leveling'
```

### Seyon Utils - Central Utility Library

**seyon-utils** is a shared module for all Seyon mods:

```gradle
// Include as dependency in other mods
dependencies {
    implementation project(':seyon-utils')
}
```

**Available Utils:**
- `PlayerUtils.getPlayerUUID(player)` - Safe UUID retrieval via component system.

**When to use seyon-utils:**
- Shared logic required in multiple mods.
- Thread-safety critical operations (e.g., UUID retrieval).
- Reusable helper classes.

### Dependencies Between Projects
```json
// In manifest.json - IMPORTANT: Format must be "Group:Name"!
"OptionalDependencies": {
    "Seyon:SeyonMagic": "*"  // ✅ Correct: Group:Name
}

// ❌ WRONG - Server won't start:
"OptionalDependencies": {
    "SeyonMagic": "*"  // Error: "String does not match <group>:<name>"
}
```

**Checking Integration:**
```java
// Check integration
try {
    Class.forName("dev.seyon.magic.SeyonMagicPlugin");
    // Implement integration
} catch (ClassNotFoundException e) {
    // Other mod not available
}
```

## Known Limitations (As of Hytale Beta)

- No `Color.GOLD` in `java.awt.Color`.
- Many game events not yet available (BlockBreak, EntityKill, etc.).
- No built-in attribute system for modifiers.
- GUI system still under development.
- No Item Tooltip Extension API.

## ❗ Common Errors and Solutions

### Server won't start: "String does not match <group>:<name>"

**Problem:** Dependencies in `manifest.json` have the wrong format.

```json
// ❌ WRONG
"OptionalDependencies": {
    "SeyonMagic": "*"
}

// ✅ CORRECT
"OptionalDependencies": {
    "Seyon:SeyonMagic": "*"
}
```

**Solution:** Always use the `"Group:Name"` format!

### IllegalStateException: Assert not in thread at PlayerUtils.getPlayerUUID()

**Problem:** `PlayerUtils.getPlayerUUID()` accesses the component system, which only works in the World thread.

**Solution:** 
- In event handlers: use `player.getUuid()` (deprecated, but safer).
- In async commands: use `player.getUuid()` (deprecated, but necessary).

```java
// ✅ In Events - Just use the deprecated method
UUID uuid = player.getUuid(); // deprecated but safe

// ✅ In Async Commands - Also deprecated method
UUID uuid = player.getUuid(); // deprecated but necessary

// ⚠️ Use PlayerUtils.getPlayerUUID() only in synchronous World thread code!
```

**Important:** `PlayerUtils.getPlayerUUID()` can cause threading issues in events. It's better to use the deprecated `getUuid()`.

### Connection Issues with ScheduledExecutorService in Events

**Problem:** Using a scheduler in the `PlayerReady` event can cause connection problems.

```java
// ❌ PROBLEMATIC - Can lead to CompletionException
scheduler.schedule(() -> {
    player.sendMessage(...);
}, 3, TimeUnit.SECONDS);
```

**Solution:** Send messages directly; do not use a scheduler.

```java
// ✅ SAFE - Send directly
player.sendMessage(...);
```

### Color.GOLD not found

**Problem:** `Color.GOLD` does not exist in `java.awt.Color`.

**Solution:** Use `Color.ORANGE` (see "Colors for Messages" section).

## Debugging Tips

### Logging
```java
HytaleLogger logger = plugin.getLogger();
logger.at(Level.INFO).log("Message");
logger.at(Level.WARNING).log("Warning");
logger.at(Level.SEVERE).withCause(exception).log("Error");
```

### Async Operations
Commands can be asynchronous:
```java
public class MyCommand extends AbstractAsyncCommand {
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext context) {
        // Async logic here
        return CompletableFuture.completedFuture(null);
    }
}
```

## 📚 Resources & Documentation

**IMPORTANT for LLMs:** Before working on a task, read through the relevant documentation! The following resources contain detailed information on all Hytale modding topics.

### API References (Lists)

These pages contain complete lists of all available elements:

- **Events**: https://hytalemodding.dev/en/docs/server/events
  - All available events (PlayerReadyEvent, BlockBreakEvent, etc.)
  - Event hierarchy (IEvent, IAsyncEvent, EcsEvent)
  
- **Sounds**: https://hytalemodding.dev/en/docs/server/sounds
  - All sound IDs that can be used in `SoundEvent.getAssetMap()`
  - Categories: SFX, Music, Ambient
  
- **Entities**: https://hytalemodding.dev/en/docs/server/entities
  - All spawnable entity IDs
  - NPCs, Mobs, Projectiles, etc.

### Detailed Guides & Tutorials

**⚠️ ALWAYS read these guides before working on the corresponding task!**

#### Fundamentals
- **Logging**: https://hytalemodding.dev/en/docs/guides/plugin/logging
  - Using HytaleLogger, log levels, template arguments
  
- **Commands**: https://hytalemodding.dev/en/docs/guides/plugin/creating-commands
  - AbstractAsyncCommand, AbstractPlayerCommand, AbstractTargetPlayerCommand
  - Command arguments (Required, Optional, Default, Flag)
  - Argument validators, permissions, command variants
  
- **Events**: https://hytalemodding.dev/en/docs/guides/plugin/creating-events
  - Event registration, event handlers
  - ECS events vs. regular events

#### Communication & UI
- **Sounds**: https://hytalemodding.dev/en/docs/guides/plugin/playing-sounds
  - Sound indices, TransformComponent, SoundUtil
  - Playing 3D sounds
  
- **Chat Formatting**: https://hytalemodding.dev/en/docs/guides/plugin/chat-formatting
  - PlayerChatEvent, Message.join(), Color
  - TinyMessage for rich text (gradients, hex colors, links)
  
- **Custom UI**: https://hytalemodding.dev/en/docs/guides/plugin/ui
  - .ui files, CustomUIHud, InteractiveCustomUIPage
  - UI elements, event binding, dynamic updates
  - HyUI documentation: https://github.com/Elliesaur/HyUI/blob/main/docs/getting-started.md
  
- **Title Holograms**: https://hytalemodding.dev/en/docs/guides/plugin/text-hologram#bonus-section---creating-title-holograms-with-code
  - Floating text via entity nameplate
  - Programmatic creation of holograms

#### Player Interaction
- **Inventory**: https://hytalemodding.dev/en/docs/guides/plugin/inventory-management
  - Creating ItemStack, using ItemContainer
  - Hotbar, Storage, Armor, Backpack
  
- **Player Input**: https://hytalemodding.dev/en/docs/guides/plugin/player-input-guide
  - SyncInteractionChains packet
  - InteractionTypes (Primary, Secondary, Use)
  
- **Player Stats**: https://hytalemodding.dev/en/docs/guides/plugin/player-stats
  - EntityStatMap, DefaultEntityStatTypes
  - Manipulating health, stamina, mana
  
- **Player Death**: https://hytalemodding.dev/en/docs/guides/plugin/player-death-event
  - OnDeathSystem, DeathComponent
  - Death info and damage tracking

#### World & Entities
- **Custom Blocks**: https://hytalemodding.dev/en/docs/guides/plugin/creating-block
  - Creating block JSON, textures, BlockType
  - Enabling asset pack
  
- **Spawning Entities**: https://hytalemodding.dev/en/docs/guides/plugin/spawning-entities
  - Creating Holder<EntityStore>
  - Adding components (Transform, Model, BoundingBox)
  - world.execute() pattern
  
- **Spawning NPCs**: https://hytalemodding.dev/en/docs/guides/plugin/spawning-npcs
  - NPCPlugin.spawnNPC() helper
  - Configuring NPC inventory
  - Simpler than manual entity spawning

#### Administration
- **Permissions**: https://hytalemodding.dev/en/docs/guides/plugin/permission-management
  - Using PermissionsModule
  - Managing user/group permissions
  - Permission checks

### How you should use these resources:

1. **Before every task**: Identify which guides are relevant.
2. **Read the guides**: Use WebFetch (or your internal knowledge) to read the relevant pages.
3. **Understand the patterns**: Pay attention to code examples and best practices.
4. **Implement**: Use the patterns from the guides.
5. **Troubleshooting**: Consult the guides when problems arise.

**Example Workflow:**
```
User: "Create a command that spawns NPCs"

LLM should:
1. Consult: https://hytalemodding.dev/en/docs/guides/plugin/creating-commands
2. Consult: https://hytalemodding.dev/en/docs/guides/plugin/spawning-npcs
3. Read and understand guides
4. Implement code based on the patterns
```

---

## 📝 Extension Notes

**This file should be continuously updated with:**
- New API insights
- Frequent error patterns and their solutions
- Real-world best practices
- Changes in the Hytale API
- Workarounds for Beta limitations

**With every important insight:** Expand this file so that future LLM interactions can benefit!
