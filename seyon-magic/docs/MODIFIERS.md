# Modifikations-System

> Zauber individuell anpassen und verstärken

## 📖 Übersicht

Modifikationen erlauben es, Grundzauber anzupassen und zu verstärken. Jede Modifikation hat **Punktkosten**, die vom Item-Punktelimit abgezogen werden.

---

## 🏷️ Modifikations-Kategorien

### 1. Wert-Modifikationen
Verändern numerische Werte des Zaubers

### 2. Verhaltens-Modifikationen  
Fügen neue Mechaniken hinzu

### 3. Trigger-Modifikationen
Bestimmen wann/wie Effekte ausgelöst werden

### 4. Projektil-Modifikationen
Speziell für Projektil-Zauber

### 5. Flächen-Modifikationen
Speziell für Flächen-Zauber

---

## 📊 Wert-Modifikationen

### Schaden

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Leicht verstärkt | +25% Schaden | 2 |
| Verstärkt | +50% Schaden | 4 |
| Stark verstärkt | +100% Schaden | 8 |
| Maximiert | +200% Schaden | 15 |
| Abgeschwächt | -25% Schaden | -1 |
| Schwach | -50% Schaden | -2 |

### Geschwindigkeit (Projektile)

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Beschleunigt | +50% Geschwindigkeit | 2 |
| Rasant | +100% Geschwindigkeit | 4 |
| Verlangsamt | -25% Geschwindigkeit | -1 |
| Kriechend | -50% Geschwindigkeit | -2 |

### Größe

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Vergrößert | +50% Größe | 2 |
| Riesig | +100% Größe | 4 |
| Massiv | +200% Größe | 8 |
| Kompakt | -25% Größe | -1 |
| Winzig | -50% Größe | -2 |

### Reichweite

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Erweiterte Reichweite | +50% Reichweite | 2 |
| Fernkampf | +100% Reichweite | 4 |
| Scharfschütze | +200% Reichweite | 7 |
| Nahkampf | -50% Reichweite | -2 |

### Cast-Zeit

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Schnellzauber | -25% Cast-Zeit | 3 |
| Sofortzauber | -50% Cast-Zeit | 6 |
| Langsamer Zauber | +50% Cast-Zeit | -2 |

### Cooldown

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Reduzierter Cooldown | -25% Cooldown | 3 |
| Minimaler Cooldown | -50% Cooldown | 7 |
| Verlängerter Cooldown | +50% Cooldown | -2 |

### Mana-Kosten

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Effizient | -25% Mana-Kosten | 2 |
| Sparsam | -50% Mana-Kosten | 5 |
| Verschwenderisch | +50% Mana-Kosten | -2 |

---

## 🎮 Verhaltens-Modifikationen

### Explosiv-Effekte

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Explosion | Explodiert bei Einschlag (3m) | 6 |
| Große Explosion | Explodiert bei Einschlag (5m) | 10 |
| Massive Explosion | Explodiert bei Einschlag (8m) | 16 |

### Flächen-Effekte

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Flächenschaden | 50% Schaden im 2m Radius | 4 |
| Erweiterter Flächenschaden | 75% Schaden im 4m Radius | 8 |
| Vernichtung | 100% Schaden im 6m Radius | 14 |

### Status-Effekte

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Brennend | DoT: 10% Schaden/s für 3s | 3 |
| Einfrierend | Slow: 30% für 2s | 3 |
| Betäubend | Stun: 0.5s | 5 |
| Vergiftend | DoT: 5% Schaden/s für 5s | 3 |
| Durchdringend | Ignoriert 25% Rüstung | 4 |
| Rüstungsbruch | Ignoriert 50% Rüstung | 8 |
| Heilungsreduktion | -50% Heilung für 5s | 4 |
| Schwächend | -20% Schaden für 5s | 4 |
| Verwundbar | +25% erlittener Schaden für 5s | 5 |

### Ketten-Effekte

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Kettenreaktion | Springt zu 2 weiteren Zielen | 6 |
| Ansteckend | Effekte springen über | 8 |
| Spaltung | Teilt sich bei Einschlag | 10 |

---

## ⏱️ Trigger-Modifikationen

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Verzögert (1s) | Löst nach 1 Sekunde aus | 1 |
| Verzögert (2s) | Löst nach 2 Sekunden aus | 2 |
| Verzögert (5s) | Löst nach 5 Sekunden aus | 3 |
| Bei Kontakt | Löst bei Berührung aus | 0 |
| Bei Niederlage | Löst aus wenn Ziel besiegt | 4 |
| Bei kritischem Treffer | Löst bei Crit aus | 5 |
| Annäherung | Löst aus wenn Feind nahe | 4 |
| Landmine | Bleibt liegen bis Kontakt | 5 |

---

## 🎯 Projektil-Modifikationen

### Mehrfach-Projektile

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Doppelt | 2 Projektile | 5 |
| Dreifach | 3 Projektile | 10 |
| Salve | 5 Projektile | 18 |
| Hagel | 10 Projektile (verteilt) | 30 |

### Streuung

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Leichte Streuung | 10° Streuung | 1 |
| Mittlere Streuung | 25° Streuung | 2 |
| Starke Streuung | 45° Streuung | 3 |
| Fächer | 90° Fächer | 4 |
| Kreis | 360° Ring | 8 |

### Flugbahn

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Verfolgend | Folgt dem Ziel | 6 |
| Stark verfolgend | Aggressive Verfolgung | 10 |
| Bumerang | Kehrt zurück | 4 |
| Ricochet | Prallt ab (2x) | 5 |
| Durchbohrend | Durchdringt Gegner | 6 |
| Spirale | Spiralförmige Flugbahn | 2 |
| Wellenförmig | Sinusförmige Bewegung | 2 |

### Projektil-Extras

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Nachglühen | Hinterlässt Schadensspur | 4 |
| Gravitationsfeld | Zieht Gegner leicht an | 5 |
| Schrapnell | Splitter bei Einschlag | 6 |

---

## 🌐 Flächen-Modifikationen

| Modifikation | Effekt | Punktkosten |
|--------------|--------|-------------|
| Größerer Radius | +50% Flächenradius | 4 |
| Massiver Radius | +100% Flächenradius | 8 |
| Längere Dauer | +50% Effekt-Dauer | 3 |
| Persistierend | +100% Effekt-Dauer | 6 |
| Wandernd | Fläche bewegt sich langsam | 5 |
| Pulsierend | Effekt wiederholt sich | 7 |

---

## 🔄 Kombinations-Regeln

### Stapelbare Modifikationen
Diese können mehrfach angewendet werden:
- Schaden-Modifikationen (bis +300% max)
- Größe-Modifikationen (bis +400% max)
- Verzögerungen (addieren sich)

### Nicht-stapelbare Modifikationen
Nur eine pro Zauber:
- Mehrfach-Projektile (höchste zählt)
- Flugbahn-Änderungen (letzte zählt)
- Streuungs-Typen (nicht kombinierbar)

### Inkompatible Kombinationen
Können nicht zusammen verwendet werden:
- Verfolgend + Streuung
- Landmine + Verfolgend  
- Sofortzauber + Verzögert
- Kriechend + Rasant

---

## 🧮 Kosten-Berechnung

### Formel
```
Gesamtkosten = Grundzauber-Kosten + Σ(Modifikations-Kosten)
```

### Beispiel-Berechnung
```
Grundzauber: Feuerball (5 Punkte)
+ Verstärkt (+50% Schaden): 4 Punkte
+ Explosion (3m): 6 Punkte
+ Dreifach: 10 Punkte
+ Verfolgend: 6 Punkte
────────────────────────────
Gesamt: 31 Punkte

→ Benötigt Item mit mind. 31 Punktelimit
→ Benötigt Item mit mind. 4 Mod-Slots
```

---

## ⚡ Modifikations-Synergien

Manche Modifikationen verstärken sich gegenseitig:

| Kombination | Synergie-Effekt |
|-------------|-----------------|
| Explosion + Brennend | Flammen breiten sich aus |
| Einfrierend + Schaden | +25% Schaden gegen Gefrorene |
| Mehrfach + Streuung | Bessere Abdeckung |
| Verfolgend + Betäubend | Garantierter Treffer → Stun |
| Durchbohrend + DoT | DoT auf alle getroffenen |

---

## 💾 Konfigurations-Struktur (Beispiel)

```json
{
  "modifiers": {
    "damage": {
      "empowered": {
        "id": "empowered",
        "name": "Verstärkt",
        "category": "value",
        "point_cost": 4,
        "effects": [
          {
            "type": "multiply_stat",
            "stat": "damage",
            "value": 1.5
          }
        ],
        "stackable": true,
        "max_stacks": 3,
        "incompatible_with": []
      }
    },
    "projectile": {
      "triple": {
        "id": "triple",
        "name": "Dreifach",
        "category": "projectile",
        "point_cost": 10,
        "effects": [
          {
            "type": "projectile_count",
            "value": 3
          }
        ],
        "stackable": false,
        "incompatible_with": ["double", "salvo", "hail"]
      }
    },
    "behavior": {
      "explosion": {
        "id": "explosion",
        "name": "Explosion",
        "category": "behavior",
        "point_cost": 6,
        "effects": [
          {
            "type": "on_impact",
            "effect": "explosion",
            "radius": 3,
            "damage_falloff": true
          }
        ],
        "animations": {
          "impact_override": "explosion_medium"
        }
      }
    }
  }
}
```

---

## 🎨 UI-Darstellung (Konzept)

```
┌─────────────────────────────────────────────────────┐
│ ZAUBER-CRAFTING                                     │
├─────────────────────────────────────────────────────┤
│ Grundzauber: [Feuerball ▼]          Kosten: 5      │
├─────────────────────────────────────────────────────┤
│ Modifikationen:                                     │
│ ┌─────────────────┐ ┌─────────────────┐            │
│ │ [Verstärkt    ] │ │ [+]             │            │
│ │ +50% Schaden    │ │                 │            │
│ │ Kosten: 4       │ │                 │            │
│ └─────────────────┘ └─────────────────┘            │
│                                                     │
│ Verfügbare Modifikationen:                         │
│ ┌────────────┐ ┌────────────┐ ┌────────────┐      │
│ │ Explosion  │ │ Dreifach   │ │ Verfolgend │      │
│ │    6 Pkt   │ │   10 Pkt   │ │    6 Pkt   │      │
│ └────────────┘ └────────────┘ └────────────┘      │
├─────────────────────────────────────────────────────┤
│ Punkte: [████████░░░░░░░] 9/30                     │
│ Slots:  [██░░░] 1/5                                │
├─────────────────────────────────────────────────────┤
│        [Vorschau]              [Erstellen]          │
└─────────────────────────────────────────────────────┘
```

---

## 🔗 Verwandte Dokumente

- [SPELLS.md](./SPELLS.md) - Grundzauber die modifiziert werden
- [ITEMS.md](./ITEMS.md) - Punkte- und Slot-Limits
- [ANIMATIONS.md](./ANIMATIONS.md) - Visuelle Effekte der Modifikationen
- [CONFIG.md](./CONFIG.md) - Alle Konfigurationsoptionen

