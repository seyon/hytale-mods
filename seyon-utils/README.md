# Seyon Utils

Zentrale Utility-Bibliothek für alle Seyon Mods.

## Übersicht

Diese Bibliothek stellt gemeinsame Funktionalität bereit, die von mehreren Seyon-Mods verwendet wird:

- **PlayerUtils**: Sichere Player-UUID-Abfrage mit Thread-Safety-Behandlung
- **Weitere Utilities folgen**

## Verwendung

Als Abhängigkeit in `build.gradle` hinzufügen:

```gradle
dependencies {
    implementation project(':seyon-utils')
}
```

## Features

### PlayerUtils

Sichere UUID-Abfrage für Spieler unter Berücksichtigung des Component-Systems:

```java
import dev.seyon.utils.PlayerUtils;

// UUID sicher abrufen (Component System)
UUID uuid = PlayerUtils.getPlayerUUID(player);
```

**Wichtig**: In asynchronen Kontexten (Commands) sollte `player.getUuid()` direkt verwendet werden aufgrund von Threading-Limitierungen.

## Warum seyon-utils?

- **Code-Wiederverwendung**: Gemeinsame Logik wird nur einmal implementiert
- **Konsistenz**: Alle Mods nutzen die gleichen bewährten Patterns
- **Wartbarkeit**: Bugfixes und Verbesserungen kommen allen Mods zugute
- **Thread-Safety**: Zentrale Behandlung von Hytale's Component-System

## Zukünftige Utilities

Geplante Erweiterungen:
- ConfigHelper für JSON-basierte Konfiguration
- MessageBuilder für konsistente Chat-Nachrichten
- DataPersistence-Helper für UUID-basierte Datenspeicherung
- Weitere nach Bedarf

## Lizenz

MIT License - Siehe LICENSE Datei für Details.
