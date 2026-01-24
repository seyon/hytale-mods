# Seyon Utils - Developer Guide

## Zweck

`seyon-utils` ist eine zentrale Utility-Bibliothek für alle Seyon-Mods. Sie enthält gemeinsame Logik, die von mehreren Mods genutzt wird.

## Neue Utils hinzufügen

### Wann sollte etwas in seyon-utils?

✅ **JA - Gehört in seyon-utils:**
- Code wird in **2+ Mods** verwendet
- Thread-Safety-kritische Operationen (z.B. ECS-Zugriffe)
- Allgemeine Helper (z.B. String-Formatting, Color-Constants)
- Gemeinsame Patterns (z.B. Data-Persistence-Helper)

❌ **NEIN - Bleibt im Mod:**
- Mod-spezifische Business-Logik
- Domain-spezifische Models
- Nur einmal verwendeter Code

### Beispiel: Neue Utility hinzufügen

```java
package dev.seyon.utils;

/**
 * Message formatting utilities
 */
public class MessageUtils {
    
    private static final Color PREFIX_COLOR = Color.ORANGE;
    
    /**
     * Create a formatted prefix message
     */
    public static Message createPrefix(String modName) {
        return Message.raw("[" + modName + "] ")
            .color(PREFIX_COLOR)
            .bold(true);
    }
}
```

## Verfügbare Utilities

### PlayerUtils

```java
import dev.seyon.utils.PlayerUtils;

// Get UUID via Component System (requires World-Thread!)
UUID uuid = PlayerUtils.getPlayerUUID(player);
```

**Thread-Safety Hinweis:**
- `PlayerUtils.getPlayerUUID()` benötigt World-Thread Zugriff
- In Async-Commands: `player.getUuid()` verwenden (deprecated aber notwendig)
- In Events: Besser `player.getUuid()` verwenden (Threading-Probleme vermeiden)

## Best Practices

1. **Dokumentation**: Jede Utility-Methode braucht JavaDoc
2. **Thread-Safety**: Immer dokumentieren ob World-Thread benötigt wird
3. **Null-Safety**: `@Nullable` und `@Nonnull` Annotations verwenden
4. **Testing**: Bei Threading-kritischem Code Hinweise geben
5. **Backwards-Compatibility**: Keine Breaking-Changes ohne Major-Version

## Dependencies

seyon-utils hat **keine** Runtime-Dependencies außer der Hytale-API:

```gradle
dependencies {
    compileOnly 'com.hypixel.hytale:hytale-api:2.0.0'
    compileOnly 'org.checkerframework:checker-qual:3.48.2'
}
```

## Versionierung

- **Major** (1.0.0 → 2.0.0): Breaking Changes
- **Minor** (1.0.0 → 1.1.0): Neue Features (backwards-compatible)
- **Patch** (1.0.0 → 1.0.1): Bugfixes

## Zukünftige Utilities (Ideen)

- **ConfigHelper**: JSON-basierte Konfiguration
- **MessageBuilder**: Fluent API für Chat-Nachrichten
- **ColorConstants**: Vordefinierte Farben für Konsistenz
- **DataPersistenceHelper**: UUID-basierte Datenspeicherung
- **SchedulerUtils**: Sichere async/sync Scheduler-Wrapper
- **InventoryHelper**: Gemeinsame Inventory-Operationen

## Lizenz

MIT License - Kann frei in allen Seyon-Mods verwendet werden.
