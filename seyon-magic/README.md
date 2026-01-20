# Arcane Arts - Hytale Magic System

Ein modulares, konfigurierbares Zaubersystem-Plugin für Hytale Server.

## 🚀 Projekt-Status

- ✅ Grundstruktur eingerichtet
- ✅ Gradle-Build-System konfiguriert
- ✅ Plugin-Entry und Minimal-Logik implementiert
- ✅ Service-Stubs für zukünftige Features vorbereitet
- ⏳ Item-System (Zauberstäbe, Grimoires) - TODO
- ⏳ Zauber-System (Grundzauber, Modifikationen) - TODO
- ⏳ Affinitäts-System - TODO
- ⏳ Qualitäts-System - TODO

## 📁 Projekt-Struktur

```
seyon-magic/
├── src/main/
│   ├── java/dev/seyon/magic/
│   │   ├── SeyonMagicPlugin.java          # Haupt-Plugin-Klasse
│   │   ├── command/
│   │   │   └── SeyonMagicCommand.java     # Test-Command
│   │   ├── event/
│   │   │   └── MagicEventHandler.java     # Event-Handler
│   │   └── service/
│   │       ├── MagicConfigService.java    # Konfigurations-Service (stub)
│   │       ├── MagicItemService.java      # Item-Service (stub)
│   │       └── SpellService.java          # Zauber-Service (stub)
│   └── resources/
│       └── manifest.json                   # Plugin-Manifest
├── build.gradle                            # Gradle-Build-Konfiguration
├── settings.gradle                         # Gradle-Settings
└── gradle.properties                       # Gradle-Properties

```

## 🔧 Build & Installation

### Voraussetzungen

- Java 25 JDK
- Gradle (via wrapper included)
- `HytaleServer.jar` im Ordner `../dependency/`

### Build

```bash
cd seyon-magic
gradlew.bat build
```

Das kompilierte Plugin (JAR) wird in `build/libs/` erstellt.

### Installation

1. Kompiliertes JAR aus `build/libs/` nehmen
2. In den `mods/` Ordner deines Hytale Servers kopieren
3. Server starten

## 🎮 Verwendung

### Commands

- `/seyon-magic` - Test-Command (OP-Permission erforderlich)
  - Zeigt Status und Version des Magic-Systems an

### Events

- **PlayerReadyEvent**: Sendet Willkommensnachricht an Spieler nach 3 Sekunden

## 🏗️ Architektur

Das Plugin folgt einer Service-orientierten Architektur, um God-Classes zu vermeiden:

- **SeyonMagicPlugin**: Main Plugin Entry Point, Service-Management
- **MagicConfigService**: Verwaltung von Konfigurationsdateien
- **MagicItemService**: Verwaltung von magischen Items (Zauberstäbe, Grimoires)
- **SpellService**: Verwaltung von Zaubern und Modifikationen

## 📖 Weiterführende Dokumentation

Siehe `docs/` Ordner für detaillierte Informationen:

- [README.md](docs/README.md) - Übersicht und Feature-Liste
- [ITEMS.md](docs/ITEMS.md) - Magische Items & Qualitäten
- [SPELLS.md](docs/SPELLS.md) - Zauber-System
- [MODIFIERS.md](docs/MODIFIERS.md) - Modifikations-System
- [AFFINITIES.md](docs/AFFINITIES.md) - Element-Affinitäten
- [ANIMATIONS.md](docs/ANIMATIONS.md) - Effekte & Animationen
- [CONFIG.md](docs/CONFIG.md) - Konfiguration

## 🔗 Abhängigkeiten

- **HytaleServer.jar** - Hytale Server API (lokal, siehe `../dependency/`)

## 📝 Lizenz

Siehe [LICENSE](LICENSE) Datei.

## 👤 Autor

Christian Wielath - [seyon.de](https://seyon.de)
