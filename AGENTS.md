# Hytale Modding - AI Agent Guide

Diese Datei sammelt wichtige Erkenntnisse und Best Practices für die Hytale Mod-Entwicklung. Sie soll kontinuierlich erweitert werden, wenn neue Erkenntnisse gewonnen werden.

## ⚠️ WICHTIG: Build-Prozess

**GRADLE BUILD WIRD NIE AUTOMATISCH AUSGEFÜHRT!**

- **NIEMALS** `gradle build`, `./gradlew build` oder ähnliche Build-Befehle automatisch ausführen
- Der Benutzer führt den Build-Prozess **IMMER MANUELL** aus
- Nach Code-Änderungen dem Benutzer mitteilen, dass er den Build manuell starten kann
- Nur `gradle compileJava` ist akzeptabel, um Syntax-Fehler zu prüfen (wenn explizit gewünscht)

## Player UUID - Das richtige Pattern

### ❌ Veraltet (Deprecated)
```java
UUID uuid = player.getUuid(); // Deprecated, marked for removal
```

### ✅ Modern (Component-System)
```java
// Hilfsmethode verwenden (siehe PlayerUtils.java)
UUID uuid = PlayerUtils.getPlayerUUID(player);

// Oder manuell:
Ref<EntityStore> ref = player.getReference();
Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
UUID uuid = uuidComponent.getUuid();
```

**Wichtig:** Hytale migriert von Legacy-Methoden zum Component-basierten System. Immer das Component-System verwenden!

**ABER:** Component-System funktioniert nur im World-Thread! In Async-Commands muss deprecated `getUuid()` verwendet werden.

### PlayerUtils Helper-Klasse
Es wurde eine `PlayerUtils` Klasse erstellt mit der Methode `getPlayerUUID(Player player)`, die die UUID über das Component-System holt. Diese Klasse sollte für alle UUID-Zugriffe verwendet werden.

### Wann welche Methode?

```java
// ✅ In Event-Handlern (World-Thread)
UUID uuid = PlayerUtils.getPlayerUUID(player);

// ✅ In Async-Commands (Outside World-Thread) 
UUID uuid = player.getUuid(); // deprecated aber notwendig

// ✅ In synchronen Commands (World-Thread)
UUID uuid = PlayerUtils.getPlayerUUID(player);
```

## Farben für Messages

### ❌ Nicht verfügbar
```java
Color.GOLD // existiert nicht in java.awt.Color
```

### ✅ Verfügbare Alternativen
```java
Color.ORANGE  // Verwende statt GOLD
Color.YELLOW
Color.GREEN
Color.RED
Color.CYAN
Color.GRAY
```

## Entity Component System (ECS)

Hytale verwendet ein Entity-Component-System für alle Spielobjekte:

- **Entities** haben keine direkten Properties, sondern **Components**
- Components werden über `ComponentType` und `ComponentAccessor` abgerufen
- Beispiel-Components:
  - `UUIDComponent` - Eindeutige ID einer Entity
  - `TransformComponent` - Position und Rotation
  - `Player` - Player-spezifische Daten
  - `PlayerRef` - Referenz zu einem Player

### Typisches Pattern
```java
// Component abrufen
Ref<EntityStore> ref = entity.getReference();
Store<EntityStore> store = world.getEntityStore().getStore();
SomeComponent component = store.getComponent(ref, SomeComponent.getComponentType());
```

## Command System

### Command-Argumente
```java
// Required Argument
RequiredArg<String> arg = this.withRequiredArg("name", "description", ArgTypes.STRING);
String value = arg.get(context);

// Optional Argument  
OptionalArg<Integer> arg = this.withOptionalArg("name", "description", ArgTypes.INTEGER);
Integer value = arg.get(context); // kann null sein
```

### Verfügbare ArgTypes
- `ArgTypes.STRING`
- `ArgTypes.INTEGER`
- `ArgTypes.DOUBLE`
- `ArgTypes.BOOLEAN`
- Weitere in der Hytale API Dokumentation

## Inventory System

```java
Inventory inventory = player.getInventory();
ItemContainer storageContainer = inventory.getStorage();

// Items hinzufügen/entfernen
storageContainer.addItemStack(item);
storageContainer.removeItemStack(item);
```

Siehe: https://hytalemodding.dev/en/docs/guides/plugin/inventory-management

## Datenpersistenz

### Player-Daten speichern
Hytale hat kein eingebautes PlayerStorage-System für Custom-Daten. Empfohlener Ansatz:

1. **JSON-basierte Persistenz** mit Gson
2. **UUID-basierte Dateien**: `playerdata/{uuid}.json`
3. **In-Memory Cache** für aktive Spieler
4. **Speichern bei**: Disconnect, Server-Shutdown, periodisch

```java
// Beispiel-Struktur
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

### Events registrieren
```java
// Im Plugin setup()
this.getEventRegistry().registerGlobal(
    PlayerReadyEvent.class, 
    event -> handler.onPlayerReady(event)
);
```

### Wichtige Events
- `PlayerReadyEvent` - Spieler ist vollständig geladen
- `BlockBreakEvent` - Block wird abgebaut (noch nicht in Beta verfügbar)
- `EntityKillEvent` - Entity wird getötet (noch nicht in Beta verfügbar)

**Hinweis:** Viele Events sind in der aktuellen Hytale Beta noch nicht verfügbar!

## Plugin-Struktur

### Best Practices
- **Service-Layer Pattern**: Logik in Services auslagern (nicht in Commands/Events)
- **Dependency Injection**: Services via Constructor injizieren
- **Konfiguration**: JSON-basiert mit Gson
- **Logging**: HytaleLogger verwenden

### Typische Ordnerstruktur
```
seyon-example/
  src/main/
    java/dev/seyon/example/
      command/          # Commands
      config/           # Config-Klassen
      event/            # Event-Handler
      gui/              # GUI-Klassen
      integration/      # Integration mit anderen Mods
      model/            # Datenmodelle
      service/          # Business-Logik
      SeyonExamplePlugin.java
    resources/
      manifest.json     # Plugin-Metadata
      Server/
        Item/Items/     # Custom Items
      Common/
        UI/Custom/      # Custom UI
  build.gradle
  gradle.properties

seyon-utils/            # ⭐ Zentrale Utils für alle Mods
  src/main/
    java/dev/seyon/utils/
      PlayerUtils.java
```

## Multi-Projekt Setup

### settings.gradle
```gradle
include 'seyon-utils'       // Shared utilities
include 'seyon-magic'
include 'seyon-motd'
include 'seyon-leveling'
```

### Seyon Utils - Zentrale Utility-Bibliothek

**seyon-utils** ist ein gemeinsames Modul für alle Seyon-Mods:

```gradle
// In anderen Mods als Dependency einbinden
dependencies {
    implementation project(':seyon-utils')
}
```

**Verfügbare Utils:**
- `PlayerUtils.getPlayerUUID(player)` - Sichere UUID-Abfrage via Component-System

**Wann seyon-utils verwenden:**
- Gemeinsame Logik, die in mehreren Mods benötigt wird
- Thread-Safety-kritische Operationen (z.B. UUID-Abfrage)
- Wiederverwendbare Helper-Klassen

### Abhängigkeiten zwischen Projekten
```json
// In manifest.json - WICHTIG: Format muss "Group:Name" sein!
"OptionalDependencies": {
    "Seyon:SeyonMagic": "*"  // ✅ Korrekt: Group:Name
}

// ❌ FALSCH - Server startet nicht:
"OptionalDependencies": {
    "SeyonMagic": "*"  // Fehler: "String does not match <group>:<name>"
}
```

**Integration prüfen:**
```java
// Integration prüfen
try {
    Class.forName("dev.seyon.magic.SeyonMagicPlugin");
    // Integration implementieren
} catch (ClassNotFoundException e) {
    // Anderer Mod nicht verfügbar
}
```

## Bekannte Einschränkungen (Stand: Hytale Beta)

- Keine `Color.GOLD` in `java.awt.Color`
- Viele Game-Events noch nicht verfügbar (BlockBreak, EntityKill, etc.)
- Kein eingebautes Attribute-System für Modifiers
- GUI-System noch in Entwicklung
- Keine Item-Tooltip-Extension API

## ❗ Häufige Fehler und Lösungen

### Server startet nicht: "String does not match <group>:<name>"

**Problem:** Dependencies in `manifest.json` haben falsches Format

```json
// ❌ FALSCH
"OptionalDependencies": {
    "SeyonMagic": "*"
}

// ✅ RICHTIG
"OptionalDependencies": {
    "Seyon:SeyonMagic": "*"
}
```

**Lösung:** Immer das Format `"Group:Name"` verwenden!

### IllegalStateException: Assert not in thread bei PlayerUtils.getPlayerUUID()

**Problem:** `PlayerUtils.getPlayerUUID()` greift auf Component-System zu, welches nur im World-Thread funktioniert

**Lösung:** 
- In Event-Handlern: `player.getUuid()` verwenden (deprecated, aber sicherer)
- In Async-Commands: `player.getUuid()` verwenden (deprecated, aber notwendig)

```java
// ✅ In Events - Einfach deprecated Methode verwenden
UUID uuid = player.getUuid(); // deprecated but safe

// ✅ In Async Commands - Auch deprecated Methode
UUID uuid = player.getUuid(); // deprecated but necessary

// ⚠️ PlayerUtils.getPlayerUUID() nur in synchronem World-Thread Code verwenden!
```

**Wichtig:** `PlayerUtils.getPlayerUUID()` kann Threading-Probleme in Events verursachen. Besser die deprecated `getUuid()` verwenden.

### Connection Issues mit ScheduledExecutorService in Events

**Problem:** Scheduler in PlayerReady Event kann Connection-Probleme verursachen

```java
// ❌ PROBLEMATISCH - Kann zu CompletionException führen
scheduler.schedule(() -> {
    player.sendMessage(...);
}, 3, TimeUnit.SECONDS);
```

**Lösung:** Nachrichten direkt senden, keinen Scheduler verwenden

```java
// ✅ SICHER - Direkt senden
player.sendMessage(...);
```

### Color.GOLD nicht gefunden

**Problem:** `Color.GOLD` existiert nicht in `java.awt.Color`

**Lösung:** `Color.ORANGE` verwenden (siehe Abschnitt "Farben für Messages")

## Debugging-Tipps

### Logging
```java
HytaleLogger logger = plugin.getLogger();
logger.at(Level.INFO).log("Message");
logger.at(Level.WARNING).log("Warning");
logger.at(Level.SEVERE).withCause(exception).log("Error");
```

### Async-Operationen
Commands können asynchron sein:
```java
public class MyCommand extends AbstractAsyncCommand {
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext context) {
        // Async logic here
        return CompletableFuture.completedFuture(null);
    }
}
```

## 📚 Ressourcen & Dokumentation

**WICHTIG für LLMs:** Bevor du eine Aufgabe bearbeitest, lies die relevanten Dokumentationen durch! Die folgenden Ressourcen enthalten detaillierte Informationen zu allen Hytale Modding-Themen.

### API-Referenzen (Listen)

Diese Seiten enthalten vollständige Listen aller verfügbaren Elemente:

- **Events**: https://hytalemodding.dev/en/docs/server/events
  - Alle verfügbaren Events (PlayerReadyEvent, BlockBreakEvent, etc.)
  - Event-Hierarchie (IEvent, IAsyncEvent, EcsEvent)
  
- **Sounds**: https://hytalemodding.dev/en/docs/server/sounds
  - Alle Sound-IDs die in `SoundEvent.getAssetMap()` verwendet werden können
  - Kategorien: SFX, Music, Ambient
  
- **Entities**: https://hytalemodding.dev/en/docs/server/entities
  - Alle spawnable Entity-IDs
  - NPCs, Mobs, Projectiles, etc.

### Detaillierte Guides & Tutorials

**⚠️ Diese Guides IMMER lesen bevor du die entsprechende Aufgabe bearbeitest!**

#### Grundlagen
- **Logging**: https://hytalemodding.dev/en/docs/guides/plugin/logging
  - HytaleLogger verwenden, Log-Levels, Template-Argumente
  
- **Commands**: https://hytalemodding.dev/en/docs/guides/plugin/creating-commands
  - AbstractAsyncCommand, AbstractPlayerCommand, AbstractTargetPlayerCommand
  - Command-Argumente (Required, Optional, Default, Flag)
  - Argument-Validatoren, Permissions, Command-Variants
  
- **Events**: https://hytalemodding.dev/en/docs/guides/plugin/creating-events
  - Event-Registrierung, Event-Handler
  - ECS Events vs. normale Events

#### Kommunikation & UI
- **Sounds**: https://hytalemodding.dev/en/docs/guides/plugin/playing-sounds
  - Sound-Indizes, TransformComponent, SoundUtil
  - 3D-Sounds abspielen
  
- **Chat Formatting**: https://hytalemodding.dev/en/docs/guides/plugin/chat-formatting
  - PlayerChatEvent, Message.join(), Color
  - TinyMessage für Rich-Text (Gradients, Hex-Colors, Links)
  
- **Custom UI**: https://hytalemodding.dev/en/docs/guides/plugin/ui
  - .ui Files, CustomUIHud, InteractiveCustomUIPage
  - UI-Elemente, Event-Binding, Dynamisches Update
  
- **Title Holograms**: https://hytalemodding.dev/en/docs/guides/plugin/text-hologram#bonus-section---creating-title-holograms-with-code
  - Floating Text via Entity-Nameplate
  - Programmtisches Erstellen von Holograms

#### Spieler-Interaktion
- **Inventory**: https://hytalemodding.dev/en/docs/guides/plugin/inventory-management
  - ItemStack erstellen, ItemContainer verwenden
  - Hotbar, Storage, Armor, Backpack
  
- **Player Input**: https://hytalemodding.dev/en/docs/guides/plugin/player-input-guide
  - SyncInteractionChains Packet
  - InteractionTypes (Primary, Secondary, Use)
  
- **Player Stats**: https://hytalemodding.dev/en/docs/guides/plugin/player-stats
  - EntityStatMap, DefaultEntityStatTypes
  - Health, Stamina, Mana manipulieren
  
- **Player Death**: https://hytalemodding.dev/en/docs/guides/plugin/player-death-event
  - OnDeathSystem, DeathComponent
  - Death-Info und Damage-Tracking

#### Welt & Entities
- **Custom Blocks**: https://hytalemodding.dev/en/docs/guides/plugin/creating-block
  - Block-JSON erstellen, Textures, BlockType
  - Asset-Pack aktivieren
  
- **Spawning Entities**: https://hytalemodding.dev/en/docs/guides/plugin/spawning-entities
  - Holder<EntityStore> erstellen
  - Components hinzufügen (Transform, Model, BoundingBox)
  - world.execute() Pattern
  
- **Spawning NPCs**: https://hytalemodding.dev/en/docs/guides/plugin/spawning-npcs
  - NPCPlugin.spawnNPC() Helper
  - NPC-Inventory konfigurieren
  - Einfacher als manuelles Entity-Spawning

#### Administration
- **Permissions**: https://hytalemodding.dev/en/docs/guides/plugin/permission-management
  - PermissionsModule verwenden
  - User/Group Permissions verwalten
  - Permission-Checks

### Wie du diese Ressourcen nutzen sollst:

1. **Vor jeder Aufgabe**: Identifiziere welche Guides relevant sind
2. **Lies die Guides**: Nutze WebFetch um die relevanten Seiten zu lesen
3. **Verstehe die Patterns**: Achte auf Code-Beispiele und Best Practices
4. **Implementiere**: Verwende die Patterns aus den Guides
5. **Fehlersuche**: Konsultiere die Guides bei Problemen

**Beispiel-Workflow:**
```
User: "Erstelle einen Command der NPCs spawnt"

LLM sollte:
1. WebFetch: https://hytalemodding.dev/en/docs/guides/plugin/creating-commands
2. WebFetch: https://hytalemodding.dev/en/docs/guides/plugin/spawning-npcs
3. Guides lesen und verstehen
4. Code implementieren basierend auf den Patterns
```

---

## 📝 Erweiterungshinweise

**Diese Datei sollte kontinuierlich aktualisiert werden mit:**
- Neuen API-Erkenntnissen
- Häufigen Fehlermustern und deren Lösungen  
- Best Practices aus der Praxis
- Änderungen in der Hytale API
- Workarounds für Beta-Limitierungen

**Bei jeder wichtigen Erkenntnis:** Diese Datei erweitern, damit zukünftige LLM-Interaktionen davon profitieren können!
