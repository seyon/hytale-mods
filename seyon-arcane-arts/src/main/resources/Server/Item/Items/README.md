# Magic Items - Custom Item Definitions

Diese Ordnerstruktur enthält alle Custom Item Definitionen für das Seyon Magic System.

## Aktuelle Items

### Wands (Zauberstäbe)
- `Magic_Wand_Common.json` - Common Magic Wand
- `Magic_Wand_Uncommon.json` - Uncommon Magic Wand
- `Magic_Wand_Rare.json` - Rare Magic Wand
- `Magic_Wand_Epic.json` - Epic Magic Wand
- `Magic_Wand_Legendary.json` - Legendary Magic Wand

### Grimoires (Zauberbücher)
- `Magic_Grimoire_Common.json` - Common Grimoire
- `Magic_Grimoire_Uncommon.json` - Uncommon Grimoire
- `Magic_Grimoire_Rare.json` - Rare Grimoire
- `Magic_Grimoire_Epic.json` - Epic Grimoire
- `Magic_Grimoire_Legendary.json` - Legendary Grimoire

## Aktueller Status

**Placeholder-Phase:** Alle Items verwenden aktuell Vanilla-Assets als Platzhalter:
- **Wands** nutzen `Common_Stick` Model/Texture/Icon
- **Grimoires** nutzen `Common_Book` Model/Texture/Icon

## Nächste Schritte: Custom Assets erstellen

Um eigene Models, Texturen und Icons zu verwenden, müssen folgende Assets erstellt werden:

### 1. Models erstellen
Erstelle `.blockymodel` Dateien in:
```
resources/Common/Items/Magic_Wand_[Quality]/
resources/Common/Items/Magic_Grimoire_[Quality]/
```

Empfohlene Tools:
- Hytale Model Maker
- Blockbench (mit Hytale Export)

### 2. Texturen erstellen
Erstelle `.png` Texturen für die Models:
```
resources/Common/Items/Magic_Wand_[Quality]/texture.png
resources/Common/Items/Magic_Grimoire_[Quality]/texture.png
```

Empfohlene Tools:
- Aseprite
- GIMP
- Photoshop

### 3. Icons erstellen
Erstelle Inventar-Icons (32x32 oder 64x64 empfohlen):
```
resources/Common/Icons/ItemsGenerated/magic_wand_[quality]_icon.png
resources/Common/Icons/ItemsGenerated/magic_grimoire_[quality]_icon.png
```

### 4. JSON-Definitionen updaten

Wenn Assets fertig sind, update die Pfade in den `.json` Dateien:

```json
{
  "Icon": "Icons/ItemsGenerated/magic_wand_legendary_icon.png",
  "Model": "Items/Magic_Wand_Legendary/model.blockymodel",
  "Texture": "Items/Magic_Wand_Legendary/texture.png"
}
```

## Beispiel-Ordnerstruktur (Ziel)

```
resources/
├── Server/
│   └── Item/
│       └── Items/
│           ├── Magic_Wand_Common.json
│           ├── Magic_Wand_Legendary.json
│           └── ...
├── Common/
│   ├── Items/
│   │   ├── Magic_Wand_Common/
│   │   │   ├── model.blockymodel
│   │   │   └── texture.png
│   │   ├── Magic_Wand_Legendary/
│   │   │   ├── model.blockymodel
│   │   │   └── texture.png
│   │   └── ...
│   └── Icons/
│       └── ItemsGenerated/
│           ├── magic_wand_common_icon.png
│           ├── magic_wand_legendary_icon.png
│           └── ...
```

## Hinweise

- **Quality-basierte Unterschiede:** Jede Quality sollte visuell unterscheidbar sein (z.B. durch Farben, Effekte, Details)
- **Konsistenz:** Halte einen einheitlichen Stil für alle Wand/Grimoire Variants
- **Performance:** Halte Texture-Größen angemessen (max 512x512 für Items empfohlen)
- **Namenskonvention:** Verwende `snake_case` für Asset-Dateien und `PascalCase` für Item-IDs
