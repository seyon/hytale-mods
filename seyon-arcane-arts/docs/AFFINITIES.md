# Elementar-Affinitäten

> Das Affinitätssystem bestimmt, welche Zauber mit einem Item gewirkt werden können

## 🔮 Übersicht

Jedes magische Item hat **Affinitäts-Slots**, die mit Elementen gefüllt werden können. Ein Zauber kann nur gewirkt werden, wenn das Item die passende Affinität besitzt.

---

## 🌟 Elemente

### Primäre Elemente

| Element | Symbol | Beschreibung | Typische Effekte |
|---------|--------|--------------|------------------|
| **Feuer** | 🔥 | Zerstörerische Hitze | DoT, Flächenschaden, Verbrennung |
| **Wasser** | 💧 | Fließende Kontrolle | Verlangsamung, Heilung, Druck |
| **Erde** | 🌍 | Solide Verteidigung | Schilde, Betäubung, Terrain |
| **Wind** | 💨 | Schnelle Bewegung | Knockback, Geschwindigkeit, Reichweite |
| **Blitz** | ⚡ | Rohe Energie | Hoher Schaden, Ketteneffekte, Betäubung |
| **Eis** | ❄️ | Eingefrorene Stille | Freeze, Slow, Shatter |

### Sekundäre Elemente

| Element | Symbol | Beschreibung | Typische Effekte |
|---------|--------|--------------|------------------|
| **Licht** | ✨ | Heilige Kraft | Heilung, Buffs, Blendung |
| **Dunkelheit** | 🌑 | Verborgene Macht | Debuffs, Drain, Unsichtbarkeit |
| **Gift** | ☠️ | Schleichender Tod | DoT, Schwächung, Infektion |
| **Arkane** | 🔮 | Pure Magie | Verstärkung, Meta-Effekte, Teleportation |

---

## 🔄 Element-Beziehungen

### Synergien (Bonus-Effekte bei Kombination)

```
Feuer + Wind    → Feuersturm (verstärkter Flächenschaden)
Wasser + Eis    → Tiefkälte (verlängerter Freeze)
Blitz + Wasser  → Kettenblitz (bessere Ausbreitung)
Erde + Feuer    → Magma (DoT + Slow)
Dunkel + Gift   → Verfall (verstärkter DoT)
Licht + Arkane  → Reinheit (verstärkte Heilung)
```

### Konflikte (Einschränkungen)

```
Feuer ↔ Eis       → Können nicht kombiniert werden
Licht ↔ Dunkel    → Können nicht kombiniert werden
Gift ↔ Licht      → Reduzierte Effektivität
```

---

## 📊 Affinitäts-Stufen

Affinitäten können verschiedene Stärken haben:

| Stufe | Name | Effekt |
|-------|------|--------|
| 1 | Anfänger | Kann Grundzauber wirken |
| 2 | Fortgeschritten | +10% Effektivität, mittlere Zauber |
| 3 | Meister | +25% Effektivität, mächtige Zauber |
| 4 | Großmeister | +50% Effektivität, legendäre Zauber |

### Stufung durch

1. **Item-Qualität** - Höhere Qualität = höhere mögliche Stufe
2. **Verzauberungen** - Können Affinitäts-Stufe erhöhen
3. **Seltene Materialien** - Beim Craften verwendete Essenz

---

## 🎯 Zauber-Anforderungen

### Einfache Zauber (Stufe 1 benötigt)
- Feuerball
- Wasserpeitsche
- Steinschild
- Windstoß

### Mittlere Zauber (Stufe 2 benötigt)
- Flammenwand
- Blitzschlag
- Erdbeben
- Eislanze

### Mächtige Zauber (Stufe 3 benötigt)
- Meteor
- Tsunami
- Blitzsturm
- Absolute Null

### Legendäre Zauber (Stufe 4 benötigt)
- Sonneneruption
- Dimensionsriss
- Elementarsturm
- Zeitstillstand

---

## 🔧 Multi-Element Zauber

Manche Zauber benötigen mehrere Affinitäten:

| Zauber | Benötigte Affinitäten |
|--------|----------------------|
| Dampfexplosion | Feuer (1) + Wasser (1) |
| Schwarzes Feuer | Feuer (2) + Dunkelheit (2) |
| Heiliger Blitz | Blitz (2) + Licht (2) |
| Elementar-Nova | 4 beliebige Elemente (je 1) |

---

## 💎 Affinitäts-Quellen

### Materialien für Affinitäten

| Element | Materialien |
|---------|-------------|
| 🔥 Feuer | Feueressenz, Phönixfeder, Lavakern |
| 💧 Wasser | Wasseressenz, Meerjungfrauenschuppe, Tiefseekristall |
| 🌍 Erde | Erdessenz, Titanerz, Uralter Stein |
| 💨 Wind | Windessenz, Sturmfeder, Wolkenfaden |
| ⚡ Blitz | Blitzessenz, Sturmkern, Elektrum |
| ❄️ Eis | Eisessenz, Ewiges Eis, Frostkern |
| ✨ Licht | Lichtessenz, Sonnensplitter, Heiliger Staub |
| 🌑 Dunkelheit | Schattenessenz, Voidkristall, Mondträne |
| ☠️ Gift | Giftessenz, Vipernzahn, Verderbter Saft |
| 🔮 Arkane | Arkane Essenz, Manakristall, Ätherfaden |

---

## 💾 Konfigurations-Struktur (Beispiel)

```json
{
  "affinities": {
    "fire": {
      "id": "fire",
      "symbol": "🔥",
      "color": "#FF4500",
      "conflicts_with": ["ice"],
      "synergizes_with": ["wind", "earth"],
      "synergy_effects": {
        "wind": {
          "bonus_type": "area_damage",
          "bonus_value": 1.5
        }
      }
    }
  },
  "affinity_levels": {
    "novice": { "level": 1, "effectiveness_bonus": 0 },
    "adept": { "level": 2, "effectiveness_bonus": 0.1 },
    "master": { "level": 3, "effectiveness_bonus": 0.25 },
    "grandmaster": { "level": 4, "effectiveness_bonus": 0.5 }
  }
}
```

---

## 🔗 Verwandte Dokumente

- [ITEMS.md](./ITEMS.md) - Wie viele Affinitäts-Slots Items haben
- [SPELLS.md](./SPELLS.md) - Welche Affinitäten Zauber benötigen
- [CONFIG.md](./CONFIG.md) - Vollständige Konfigurationsoptionen

