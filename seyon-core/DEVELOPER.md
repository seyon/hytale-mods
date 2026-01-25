# Seyon Core - Developer Guide

## Zweck

`seyon-core` ist die zentrale Kern-Bibliothek für alle Seyon-Mods. Sie enthält das einheitliche Settings-System und gemeinsame Utilities.

## Settings-System

### Settings Provider erstellen

Jeder Mod sollte einen `ISettingsProvider` implementieren:

```java
package dev.seyon.yourmod.settings;

import dev.seyon.core.settings.ISettingsProvider;
import dev.seyon.core.settings.SettingsCategory;

public class YourModSettingsProvider implements ISettingsProvider {
    
    @Override
    public String getModId() {
        return "seyon-yourmod";
    }
    
    @Override
    public String getModName() {
        return "Your Mod";
    }
    
    @Override
    public String getModVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getModDescription() {
        return "Description for Info tab";
    }
    
    @Override
    public List<SettingsCategory> getCategories() {
        List<SettingsCategory> categories = new ArrayList<>();
        categories.add(new SettingsCategory("config", "Configuration", "Mod settings", 10));
        return categories;
    }
    
    @Override
    public void buildCategoryUI(String categoryId, String contentPath, 
                                 UICommandBuilder builder, UIEventBuilder eventBuilder,
                                 Ref<EntityStore> ref, Store<EntityStore> store) {
        // Build UI for your category
    }
    
    @Override
    public boolean handleCategoryEvent(String categoryId, String eventType, 
                                        Object eventData, Player player,
                                        Ref<EntityStore> ref, Store<EntityStore> store) {
        // Handle events, return true to rebuild UI
        return false;
    }
}
```

### Provider registrieren

Im Plugin-Setup:

```java
import dev.seyon.core.settings.SettingsRegistry;

@Override
protected void setup() {
    super.setup();
    
    // Register settings provider
    SettingsRegistry.getInstance().registerProvider(new YourModSettingsProvider(this));
}
```

## Neue Utils hinzufügen

### Wann sollte etwas in seyon-core?

✅ **JA - Gehört in seyon-core:**
- Code wird in **2+ Mods** verwendet
- Settings-System Komponenten
- Thread-Safety-kritische Operationen (z.B. ECS-Zugriffe)
- Allgemeine Helper (z.B. String-Formatting, Color-Constants)
- Gemeinsame Patterns (z.B. Data-Persistence-Helper)

❌ **NEIN - Bleibt im Mod:**
- Mod-spezifische Business-Logik
- Domain-spezifische Models
- Nur einmal verwendeter Code

### Beispiel: Neue Utility hinzufügen

```java
package dev.seyon.core;

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
import dev.seyon.core.PlayerUtils;

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

seyon-core hat **keine** Runtime-Dependencies außer der Hytale-API:

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

## Lizenz

MIT License - Kann frei in allen Seyon-Mods verwendet werden.

