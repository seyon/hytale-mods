# Hytale Mods Collection

Sammlung von Hytale Server Plugins/Mods entwickelt von Seyon.

## 📦 Projekte

| Projekt | Beschreibung | Status |
|---------|-------------|--------|
| [seyon-motd](./seyon-motd) | Configurable MOTD plugin with GUI | ✅ Funktional |
| [seyon-magic](./seyon-magic) | Arcane Arts - Umfangreiches Zaubersystem | 🚧 In Entwicklung |

## 🔧 Build & Development

### Voraussetzungen

- Java 25 JDK
- Gradle (via wrapper included)
- `HytaleServer.jar` im Ordner `dependency/`

### Multi-Projekt-Build

Alle Projekte mit einem Command bauen:

```bash
# Alle Projekte bauen
gradlew.bat build

# Nur ein spezifisches Projekt bauen
gradlew.bat :seyon-motd:build
gradlew.bat :seyon-magic:build

# Alle Tests ausführen
gradlew.bat test

# Alle Projekte cleanen
gradlew.bat clean
```

### Einzelprojekt-Build

```bash
# In ein Projekt wechseln und bauen
cd seyon-motd
..\gradlew.bat build
```

### Verfügbare Tasks

```bash
# Alle verfügbaren Tasks anzeigen
gradlew.bat tasks

# Tasks für ein spezifisches Projekt
gradlew.bat :seyon-motd:tasks
```

## 📁 Projekt-Struktur

```
hytale-mods/
├── dependency/                    # Gemeinsame Abhängigkeiten
│   └── HytaleServer.jar          # Hytale Server API
├── seyon-motd/                   # MOTD Plugin
│   ├── src/
│   ├── build.gradle
│   └── README.md
├── seyon-magic/                  # Magic System Plugin
│   ├── docs/                     # Detaillierte Dokumentation
│   ├── src/
│   ├── build.gradle
│   └── README.md
├── build.gradle                  # Root Build-Konfiguration
├── settings.gradle               # Multi-Projekt-Setup
├── gradle.properties             # Gemeinsame Properties
└── gradlew.bat                   # Gradle Wrapper (Windows)
```

## 🏗️ Architektur

### Multi-Projekt-Setup

- **Root Build**: Gemeinsame Konfiguration für alle Subprojekte
- **Subprojekte**: Projektspezifische Konfiguration und Code
- **Gemeinsame Dependencies**: Zentral verwaltet im `dependency/` Ordner

### Vorteile

- ✅ Einheitliche Build-Konfiguration
- ✅ Zentrale Dependency-Verwaltung
- ✅ Einfaches Updaten von HytaleServer.jar
- ✅ Konsistente Java/Gradle-Versionen
- ✅ Ein Command baut alle Projekte

## 📝 Neue Projekte hinzufügen

1. Neuen Ordner im Root erstellen (z.B. `seyon-xyz`)
2. In `settings.gradle` hinzufügen: `include 'seyon-xyz'`
3. `build.gradle` im neuen Projekt erstellen (siehe Beispiele)
4. Standard-Ordnerstruktur anlegen (`src/main/java`, `src/main/resources`)

## 🔗 Dependencies

- **HytaleServer.jar** - Hytale Server API (lokal, siehe `dependency/`)
- Wird zentral verwaltet und von allen Projekten genutzt

## 📄 Lizenz

Siehe LICENSE-Dateien in den jeweiligen Projekten.

## 👤 Autor

Christian Wielath - [seyon.de](https://seyon.de)
