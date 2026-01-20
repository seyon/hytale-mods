# Zauber-System

> Grundzauber, Zauber-Typen und das Crafting-System

## 📖 Übersicht

Das Zauber-System basiert auf **Grundzaubern**, die durch **Modifikationen** angepasst werden können. Jeder Zauber hat Basis-Kosten und kann durch das Item-Punktelimit begrenzt werden.

---

## 🎯 Zauber-Kategorien

### Nach Wirkungsweise

| Kategorie | Beschreibung | Beispiele |
|-----------|--------------|-----------|
| **Projektil** | Fliegt zum Ziel | Feuerball, Eislanze, Arkangeschoss |
| **Strahl** | Sofortige Linie | Feuerstrahl, Blitz, Lichtstrahl |
| **Fläche** | Wirkt in einem Bereich | Erdbeben, Nova, Regen |
| **Selbst** | Wirkt auf den Zauberer | Schild, Heilung, Buff |
| **Beschwörung** | Erschafft Entitäten | Elementar, Diener, Barriere |
| **Instantan** | Sofort am Ziel | Blitzschlag, Teleport |

### Nach Element (Auszug)

Siehe [AFFINITIES.md](./AFFINITIES.md) für vollständige Element-Liste.

---

## 🔥 Grundzauber-Katalog

### Feuer-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Feuerfunke | Projektil | 3 | 1 | Kleiner, schneller Feuerball |
| Feuerball | Projektil | 5 | 1 | Standard Feuergeschoss |
| Feuerstrahl | Strahl | 8 | 2 | Kontinuierlicher Feuerstrahl |
| Flammenwand | Fläche | 10 | 2 | Wand aus Flammen |
| Meteor | Instantan | 20 | 3 | Meteor fällt vom Himmel |
| Feuersturm | Fläche | 25 | 3 | Regen aus Feuerbällen |
| Sonneneruption | Fläche | 40 | 4 | Massive Feuerexplosion |

### Eis-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Eissplitter | Projektil | 3 | 1 | Kleiner Eissplitter |
| Eislanze | Projektil | 6 | 1 | Durchdringende Eislanze |
| Froststrahl | Strahl | 8 | 2 | Einfrierender Strahl |
| Eiswand | Beschwörung | 10 | 2 | Solide Eisbarriere |
| Schneesturm | Fläche | 15 | 3 | Flächendeckende Kälte |
| Absolute Null | Fläche | 35 | 4 | Alles einfrieren |

### Blitz-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Funken | Strahl | 2 | 1 | Schwacher Elektroschock |
| Blitz | Instantan | 7 | 1 | Blitz von oben |
| Kettenblitz | Strahl | 12 | 2 | Springt zwischen Zielen |
| Blitzsphäre | Projektil | 10 | 2 | Langsame, verfolgende Kugel |
| Donnerschlag | Fläche | 18 | 3 | AoE Blitz + Betäubung |
| Blitzsturm | Fläche | 30 | 4 | Viele Blitze vom Himmel |

### Erde-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Steinwurf | Projektil | 3 | 1 | Schleudert einen Stein |
| Erddorn | Instantan | 5 | 1 | Dorn aus dem Boden |
| Steinschild | Selbst | 8 | 2 | Defensiver Steinschild |
| Erdbeben | Fläche | 15 | 2 | Erschüttert den Boden |
| Steinprisma | Beschwörung | 12 | 3 | Fängt Ziel ein |
| Tektonik | Fläche | 35 | 4 | Massives Erdbeben |

### Wasser-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Wasserstrahl | Strahl | 3 | 1 | Wasserdruck-Strahl |
| Wasserkugel | Projektil | 5 | 1 | Explodierende Wasserkugel |
| Heilquelle | Fläche | 10 | 2 | Heilender Bereich |
| Wasserpeitsche | Strahl | 8 | 2 | Schlagende Wasserpeitsche |
| Flutwelle | Fläche | 20 | 3 | Welle die alles mitreißt |
| Tsunami | Fläche | 40 | 4 | Gigantische Welle |

### Wind-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Windstoß | Instantan | 2 | 1 | Schubst Ziel zurück |
| Luftklinge | Projektil | 5 | 1 | Schneidender Wind |
| Windschild | Selbst | 6 | 2 | Projektile werden abgelenkt |
| Tornado | Beschwörung | 15 | 2 | Wirbelwind |
| Sturm | Fläche | 20 | 3 | Massiver Windsturm |
| Vakuum | Fläche | 30 | 4 | Saugt alles ein |

### Licht-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Lichtblitz | Instantan | 3 | 1 | Blendet Ziel |
| Heilstrahl | Strahl | 6 | 1 | Heilt Verbündete |
| Segen | Selbst | 8 | 2 | Temporärer Buff |
| Lichtbarriere | Beschwörung | 12 | 2 | Schützende Barriere |
| Sonnenstrahl | Strahl | 15 | 3 | Mächtiger Lichtstrahl |
| Göttliches Urteil | Instantan | 35 | 4 | Vernichtender Lichtschlag |

### Dunkelheits-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Schattenbolzen | Projektil | 4 | 1 | Dunkles Geschoss |
| Lebensentzug | Strahl | 7 | 1 | Stiehlt Leben |
| Fluch | Instantan | 8 | 2 | Schwächt Ziel |
| Schattenmantel | Selbst | 10 | 2 | Teilweise Unsichtbarkeit |
| Void-Zone | Fläche | 18 | 3 | Schadenzone |
| Schwarzes Loch | Fläche | 40 | 4 | Zieht alles ein |

### Gift-Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Giftpfeil | Projektil | 4 | 1 | Vergiftendes Geschoss |
| Giftwolke | Fläche | 8 | 1 | Giftiger Nebel |
| Infektion | Instantan | 10 | 2 | Springt auf nahe Ziele |
| Säurestrahl | Strahl | 12 | 2 | Ätzender Strahl |
| Seuche | Fläche | 20 | 3 | Großflächige Infektion |
| Todesschwarm | Beschwörung | 30 | 4 | Giftige Insekten |

### Arkane Zauber

| Zauber | Typ | Basis-Kosten | Stufe | Beschreibung |
|--------|-----|--------------|-------|--------------|
| Arkangeschoss | Projektil | 3 | 1 | Verfolgendes Geschoss |
| Teleport | Selbst | 8 | 1 | Kurze Teleportation |
| Manastoß | Strahl | 10 | 2 | Entzieht Mana |
| Arkane Barriere | Beschwörung | 12 | 2 | Magisches Schild |
| Magie bannen | Instantan | 15 | 3 | Entfernt Magie-Effekte |
| Dimensionsriss | Fläche | 35 | 4 | Instabiler Riss |

---

## ⚙️ Zauber-Eigenschaften

Jeder Grundzauber hat folgende Basis-Eigenschaften:

```
┌────────────────────────────────────────────┐
│ Grundzauber: Feuerball                     │
├────────────────────────────────────────────┤
│ Element:        Feuer                      │
│ Typ:            Projektil                  │
│ Basis-Kosten:   5 Punkte                   │
│ Stufe:          1                          │
│ Mana-Kosten:    15                         │
├────────────────────────────────────────────┤
│ Basis-Werte:                               │
│   - Schaden:           25                  │
│   - Reichweite:        30m                 │
│   - Geschwindigkeit:   20m/s               │
│   - Cast-Zeit:         0.5s                │
│   - Cooldown:          1.0s                │
│   - Größe:             0.5m                │
├────────────────────────────────────────────┤
│ Effekte:                                   │
│   - Verbrennung: 5 Schaden/s für 3s        │
├────────────────────────────────────────────┤
│ Animationen:                               │
│   - Cast:     fire_cast_basic              │
│   - Travel:   fireball_projectile          │
│   - Impact:   fire_impact_small            │
└────────────────────────────────────────────┘
```

---

## 🔨 Zauber-Crafting Prozess

### Schritt 1: Grundzauber wählen
- Muss zur Item-Affinität passen
- Verbraucht Basis-Punkte

### Schritt 2: Modifikationen hinzufügen
- Begrenzt durch Max-Modifikationen des Items
- Begrenzt durch verbleibende Punkte
- Siehe [MODIFIERS.md](./MODIFIERS.md)

### Schritt 3: Zauber binden
- Zauber wird an Item gebunden
- Kann später ersetzt werden
- Verbrauchte Ressourcen gehen verloren

### Beispiel-Crafting

```
Item: Epischer Feuer-Zauberstab
├── Punktelimit: 30
├── Max Modifikationen: 5
└── Affinität: Feuer (Stufe 3)

Grundzauber: Feuerball
└── Kosten: 5 Punkte
    Verbleibend: 25 Punkte

+ Modifikation: Verstärkt (+50% Schaden)
└── Kosten: 4 Punkte
    Verbleibend: 21 Punkte

+ Modifikation: Explosion (AoE am Ziel)
└── Kosten: 6 Punkte
    Verbleibend: 15 Punkte

+ Modifikation: Größer (+100% Größe)
└── Kosten: 3 Punkte
    Verbleibend: 12 Punkte

+ Modifikation: Dreifach (3 Projektile)
└── Kosten: 10 Punkte
    Verbleibend: 2 Punkte

═══════════════════════════════════════
Ergebnis: Verstärkter Explosiver Großer Dreifach-Feuerball
├── Gesamtkosten: 28/30 Punkte
├── Modifikationen: 4/5
└── Effekt: 3 große Feuerbälle mit +50% Schaden 
            die bei Einschlag explodieren
```

---

## 📊 Zauber-Slots

Items haben begrenzte Zauber-Slots:

| Item-Typ | Qualität | Zauber-Slots |
|----------|----------|--------------|
| Zauberstab | Gewöhnlich | 1 |
| Zauberstab | Ungewöhnlich | 2 |
| Zauberstab | Selten | 2 |
| Zauberstab | Episch | 3 |
| Zauberstab | Legendär | 4 |
| Grimoire | Gewöhnlich | 2 |
| Grimoire | Ungewöhnlich | 3 |
| Grimoire | Selten | 4 |
| Grimoire | Episch | 5 |
| Grimoire | Legendär | 6 |

---

## 💾 Konfigurations-Struktur (Beispiel)

```json
{
  "base_spells": {
    "fireball": {
      "id": "fireball",
      "name": "Feuerball",
      "element": "fire",
      "type": "projectile",
      "tier": 1,
      "point_cost": 5,
      "mana_cost": 15,
      "stats": {
        "damage": 25,
        "range": 30,
        "speed": 20,
        "cast_time": 0.5,
        "cooldown": 1.0,
        "size": 0.5
      },
      "effects": [
        {
          "type": "dot",
          "damage": 5,
          "duration": 3,
          "name": "burning"
        }
      ],
      "animations": {
        "cast": "fire_cast_basic",
        "travel": "fireball_projectile",
        "impact": "fire_impact_small"
      }
    }
  }
}
```

---

## 🔗 Verwandte Dokumente

- [MODIFIERS.md](./MODIFIERS.md) - Alle verfügbaren Modifikationen
- [ANIMATIONS.md](./ANIMATIONS.md) - Animationssystem für Zauber
- [AFFINITIES.md](./AFFINITIES.md) - Element-Anforderungen
- [CONFIG.md](./CONFIG.md) - Vollständige Konfigurationsstruktur

