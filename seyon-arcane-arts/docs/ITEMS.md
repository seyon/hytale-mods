# Magische Gegenstände

> Zauberstäbe & Grimoires

## 📜 Übersicht

Es gibt zwei Hauptkategorien magischer Gegenstände:

| Typ | Verwendung | Stärke |
|-----|-----------|--------|
| **Zauberstab** | Schnelle, einfache Zauber | Geschwindigkeit |
| **Grimoire** | Komplexe, mächtige Zauber | Macht & Spezialeffekte |

---

## 🪄 Zauberstäbe (Wands)

### Eigenschaften
- Schnelle Zaubergeschwindigkeit
- Geringere Zauberkraft
- Weniger Modifikations-Slots
- Niedrigeres Punktelimit
- Ideal für Kampf und schnelle Reaktionen

### Qualitätsstufen

| Qualität | Mana | Zauberkraft | Geschwindigkeit | Max Mods | Punktelimit |
|----------|------|-------------|-----------------|----------|-------------|
| **Gewöhnlich** | 50 | 1.0x | 1.0x | 2 | 10 |
| **Ungewöhnlich** | 75 | 1.1x | 1.1x | 3 | 15 |
| **Selten** | 100 | 1.25x | 1.2x | 4 | 20 |
| **Episch** | 150 | 1.5x | 1.3x | 5 | 30 |
| **Legendär** | 200 | 2.0x | 1.5x | 6 | 40 |

### Affinitäts-Slots

| Qualität | Affinitäts-Slots |
|----------|------------------|
| Gewöhnlich | 1 |
| Ungewöhnlich | 1 |
| Selten | 2 |
| Episch | 2 |
| Legendär | 3 |

---

## 📚 Grimoires (Bücher)

### Eigenschaften
- Langsamere Zaubergeschwindigkeit
- Höhere Zauberkraft
- Mehr Modifikations-Slots
- Höheres Punktelimit
- Ideal für mächtige Zauber und Spezialeffekte
- Können komplexere Zauber aufnehmen

### Qualitätsstufen

| Qualität | Mana | Zauberkraft | Geschwindigkeit | Max Mods | Punktelimit |
|----------|------|-------------|-----------------|----------|-------------|
| **Gewöhnlich** | 100 | 1.2x | 0.7x | 4 | 20 |
| **Ungewöhnlich** | 150 | 1.4x | 0.75x | 5 | 30 |
| **Selten** | 200 | 1.6x | 0.8x | 6 | 45 |
| **Episch** | 300 | 2.0x | 0.85x | 8 | 60 |
| **Legendär** | 400 | 2.5x | 0.9x | 10 | 80 |

### Affinitäts-Slots

| Qualität | Affinitäts-Slots |
|----------|------------------|
| Gewöhnlich | 1 |
| Ungewöhnlich | 2 |
| Selten | 2 |
| Episch | 3 |
| Legendär | 4 |

---

## ⭐ Qualitätssystem

### Faktoren die Qualität beeinflussen

1. **Materialien beim Craften**
   - Seltene Materialien = höhere Chance auf bessere Qualität
   - Bestimmte Kombinationen garantieren Mindestqualität

2. **Crafting-Skill** (optional)
   - Höherer Skill = höhere Qualitäts-Chance
   - Perfektes Timing bei Mini-Games

3. **Magische Verstärkung**
   - Items können nachträglich verbessert werden
   - Begrenzte Anzahl an Upgrades

### Basiswerte erklärt

#### Mana
- Verfügbare magische Energie
- Zauber verbrauchen Mana basierend auf Komplexität
- Regeneriert sich über Zeit

#### Zauberkraft (Multiplier)
- Basis-Schaden/Effektstärke wird multipliziert
- Beeinflusst alle Aspekte eines Zaubers
- Stapelt mit Modifikationen

#### Zaubergeschwindigkeit (Multiplier)
- Beeinflusst Cast-Zeit
- 1.0x = Normal, >1.0x = Schneller, <1.0x = Langsamer
- Beeinflusst auch Cooldowns

---

## 🎨 Visuelle Unterscheidung

### Zauberstäbe
```
Qualität        Aussehen
─────────────────────────────────────
Gewöhnlich      Einfaches Holz, keine Verzierungen
Ungewöhnlich    Holz mit leichtem Schimmer
Selten          Verziertes Holz, sichtbare Runen
Episch          Kristallverzierungen, leuchtende Runen
Legendär        Einzigartiges Design, Partikeleffekte
```

### Grimoires
```
Qualität        Aussehen
─────────────────────────────────────
Gewöhnlich      Abgenutztes Leder, einfacher Einband
Ungewöhnlich    Gepflegtes Leder, einfache Gravuren
Selten          Verzierter Einband, metallene Ecken
Episch          Leuchtende Symbole, magische Aura
Legendär        Schwebendes Buch, Partikeleffekte
```

---

## 💾 Konfigurations-Struktur (Beispiel)

```json
{
  "item_types": {
    "wand": {
      "base_cast_speed": 1.0,
      "base_power": 1.0,
      "qualities": {
        "common": {
          "mana": 50,
          "power_multiplier": 1.0,
          "speed_multiplier": 1.0,
          "max_modifiers": 2,
          "point_limit": 10,
          "affinity_slots": 1
        }
        // ... weitere Qualitäten
      }
    },
    "grimoire": {
      "base_cast_speed": 0.7,
      "base_power": 1.2,
      "qualities": {
        // ... analog zu wand
      }
    }
  }
}
```

---

## 🔗 Verwandte Dokumente

- [AFFINITIES.md](./AFFINITIES.md) - Welche Elemente Items haben können
- [SPELLS.md](./SPELLS.md) - Welche Zauber mit Items gewirkt werden
- [MODIFIERS.md](./MODIFIERS.md) - Modifikations-Limits der Items

