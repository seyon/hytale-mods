# Seyon Core

Zentrale Kern-Bibliothek für alle Seyon Mods - stellt gemeinsame Services bereit.

## Übersicht

Diese Bibliothek stellt gemeinsame Funktionalität bereit, die von allen Seyon-Mods verwendet wird:

- **Plugin Discovery Service**: Zentrale Erkennung und Caching aller installierten Plugins
- **Shared Services**: Wiederverwendbare Dienste für alle Mods

## ⚠️ Wichtig

Seyon Core ist ein **reines Service-Plugin** ohne GUI. Alle Mods konfigurieren sich über ihre eigenen JSON-Dateien.

## Installation

1. Kopiere `SeyonCore-1.0.0.jar` in den `mods/` Ordner
2. Starte den Server
3. Das Plugin initialisiert automatisch alle Services

## Verwendung in eigenen Mods

### Als Build-Abhängigkeit

In `build.gradle` hinzufügen:

```gradle
dependencies {
    compileOnly project(':seyon-core')
}
```

### Im Manifest

In `manifest.json`:

```json
{
  "Dependencies": {
    "Seyon:SeyonCore": ">=1.0.0"
  }
}
```

## Features

### Plugin Discovery Service

Zentrale Erkennung aller installierten Plugins beim Server-Start:

```java
import dev.seyon.core.SeyonCorePlugin;

// In deinem Plugin:
List<String> plugins = SeyonCorePlugin.getInstance().getAllPlugins();
// Gibt Liste zurück: ["SeyonMotd (1.0.0)", "MyPlugin (2.0.0)", ...]
```

**Features:**
- Einmalige Plugin-Erkennung beim Server-Start
- Gecachte Plugin-Liste für Performance
- Automatische Filterung von Hytale Core-Plugins
- Version-Informationen aus Plugin-Manifests
- Thread-sicher und performant

### Service-Architektur

Der Core stellt zentrale Services bereit, die von allen Mods genutzt werden können:

- `PluginDiscoveryService` - Plugin-Erkennung und Caching
- Weitere Services können hinzugefügt werden

## Warum seyon-core?

- ✅ **Code-Wiederverwendung**: Gemeinsame Logik wird nur einmal implementiert
- ✅ **Performance**: Einmalige Plugin-Erkennung statt wiederholter Reflection-Calls
- ✅ **Konsistenz**: Alle Mods nutzen die gleichen bewährten Services
- ✅ **Wartbarkeit**: Bugfixes und Verbesserungen kommen allen Mods zugute
- ✅ **Thread-Safety**: Zentrale Behandlung von Threading und Caching

## Für Entwickler

### Neue Services hinzufügen

1. Service-Klasse in `dev.seyon.core.service` erstellen
2. Im `SeyonCorePlugin.setup()` initialisieren
3. Getter-Methode in `SeyonCorePlugin` hinzufügen
4. Dokumentation in README aktualisieren

### Best Practices

- Alle Services sollten thread-safe sein
- Services sollten lazy-loaded oder beim Server-Start initialisiert werden
- Verwende Caching wo möglich für Performance
- Logge wichtige Ereignisse auf INFO-Level

## Konfiguration

**Seyon Core benötigt keine Konfiguration.** Es ist ein reines Service-Plugin.

## Lizenz

MIT License - Siehe LICENSE Datei für Details.

## Autor

Christian Wielath - [seyon.de](https://seyon.de)
