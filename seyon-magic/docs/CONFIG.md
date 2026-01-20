# Konfigurations-System

> Alles anpassbar über Konfigurations-Dateien

## 📖 Übersicht

Das gesamte Mod ist über JSON/YAML Konfigurationsdateien erweiterbar und anpassbar. Dies ermöglicht:
- Neue Zauber ohne Code-Änderungen
- Balance-Anpassungen im laufenden Betrieb
- Server-spezifische Konfigurationen
- Community-Erweiterungen

---

## 📁 Datei-Struktur

```
arcane_arts/
├── config/
│   ├── main.json              # Haupt-Konfiguration
│   ├── items/
│   │   ├── wands.json         # Zauberstab-Definitionen
│   │   └── grimoires.json     # Grimoire-Definitionen
│   ├── affinities/
│   │   └── elements.json      # Element-Definitionen
│   ├── spells/
│   │   ├── fire.json          # Feuer-Zauber
│   │   ├── ice.json           # Eis-Zauber
│   │   ├── lightning.json     # Blitz-Zauber
│   │   └── ...                # Weitere Elemente
│   ├── modifiers/
│   │   ├── value_mods.json    # Wert-Modifikationen
│   │   ├── behavior_mods.json # Verhaltens-Mods
│   │   └── projectile_mods.json
│   └── animations/
│       ├── effects.json       # Effekt-Definitionen
│       └── sounds.json        # Sound-Definitionen
└── assets/
    └── ...                    # Grafiken, Sounds, etc.
```

---

## ⚙️ Haupt-Konfiguration

`config/main.json`

```json
{
  "mod_info": {
    "name": "Arcane Arts",
    "version": "1.0.0",
    "author": "Your Name"
  },
  "global_settings": {
    "mana_regen_rate": 5.0,
    "mana_regen_delay_after_cast": 2.0,
    "global_cooldown": 0.5,
    "max_active_spells": 10,
    "spell_collision": true,
    "friendly_fire": false
  },
  "balance_multipliers": {
    "damage": 1.0,
    "mana_cost": 1.0,
    "cooldown": 1.0,
    "point_costs": 1.0
  },
  "features": {
    "spell_crafting": true,
    "quality_system": true,
    "affinity_system": true,
    "combo_system": false
  }
}
```

---

## 🪄 Item-Konfiguration

`config/items/wands.json`

```json
{
  "item_type": "wand",
  "base_stats": {
    "cast_speed_multiplier": 1.0,
    "power_multiplier": 1.0
  },
  "qualities": {
    "common": {
      "color": "#FFFFFF",
      "mana": 50,
      "power_mult": 1.0,
      "speed_mult": 1.0,
      "max_modifiers": 2,
      "point_limit": 10,
      "affinity_slots": 1,
      "spell_slots": 1,
      "drop_weight": 100
    },
    "uncommon": {
      "color": "#00FF00",
      "mana": 75,
      "power_mult": 1.1,
      "speed_mult": 1.1,
      "max_modifiers": 3,
      "point_limit": 15,
      "affinity_slots": 1,
      "spell_slots": 2,
      "drop_weight": 50
    },
    "rare": {
      "color": "#0080FF",
      "mana": 100,
      "power_mult": 1.25,
      "speed_mult": 1.2,
      "max_modifiers": 4,
      "point_limit": 20,
      "affinity_slots": 2,
      "spell_slots": 2,
      "drop_weight": 20
    },
    "epic": {
      "color": "#9400D3",
      "mana": 150,
      "power_mult": 1.5,
      "speed_mult": 1.3,
      "max_modifiers": 5,
      "point_limit": 30,
      "affinity_slots": 2,
      "spell_slots": 3,
      "drop_weight": 5
    },
    "legendary": {
      "color": "#FFD700",
      "mana": 200,
      "power_mult": 2.0,
      "speed_mult": 1.5,
      "max_modifiers": 6,
      "point_limit": 40,
      "affinity_slots": 3,
      "spell_slots": 4,
      "drop_weight": 1
    }
  }
}
```

---

## 🔮 Affinitäts-Konfiguration

`config/affinities/elements.json`

```json
{
  "elements": {
    "fire": {
      "id": "fire",
      "name": "Feuer",
      "symbol": "🔥",
      "color": "#FF4500",
      "description": "Zerstörerische Hitze",
      "conflicts": ["ice"],
      "synergies": {
        "wind": {
          "name": "Feuersturm",
          "bonus": "area_damage",
          "value": 1.5
        },
        "earth": {
          "name": "Magma",
          "bonus": "dot_slow",
          "value": 1.3
        }
      }
    },
    "ice": {
      "id": "ice",
      "name": "Eis",
      "symbol": "❄️",
      "color": "#00BFFF",
      "description": "Eingefrorene Stille",
      "conflicts": ["fire"],
      "synergies": {
        "water": {
          "name": "Tiefkälte",
          "bonus": "freeze_duration",
          "value": 1.5
        }
      }
    }
  },
  "affinity_levels": {
    "1": { "name": "Anfänger", "bonus": 0 },
    "2": { "name": "Fortgeschritten", "bonus": 0.1 },
    "3": { "name": "Meister", "bonus": 0.25 },
    "4": { "name": "Großmeister", "bonus": 0.5 }
  }
}
```

---

## ✨ Zauber-Konfiguration

`config/spells/fire.json`

```json
{
  "element": "fire",
  "spells": {
    "fireball": {
      "id": "fireball",
      "name": "Feuerball",
      "description": "Schleudert einen brennenden Feuerball",
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
          "name": "Verbrennung",
          "damage_percent": 0.2,
          "duration": 3,
          "tick_rate": 1
        }
      ],
      "animations": {
        "cast": "fire_cast_basic",
        "travel": "fireball_projectile",
        "impact": "fire_impact_small"
      },
      "sounds": {
        "cast": "fire_charge",
        "travel": "fire_woosh_loop",
        "impact": "fire_impact"
      }
    },
    "meteor": {
      "id": "meteor",
      "name": "Meteor",
      "description": "Ruft einen Meteor vom Himmel",
      "type": "instant",
      "tier": 3,
      "point_cost": 20,
      "mana_cost": 80,
      "stats": {
        "damage": 150,
        "range": 40,
        "cast_time": 2.0,
        "cooldown": 15.0,
        "aoe_radius": 5
      },
      "effects": [
        {
          "type": "dot",
          "name": "Brennender Boden",
          "damage_percent": 0.1,
          "duration": 5,
          "aoe": true
        }
      ],
      "animations": {
        "cast": "fire_cast_channeled",
        "travel": null,
        "impact": "meteor_strike"
      }
    }
  }
}
```

---

## 🔧 Modifikations-Konfiguration

`config/modifiers/value_mods.json`

```json
{
  "category": "value",
  "modifiers": {
    "empowered": {
      "id": "empowered",
      "name": "Verstärkt",
      "description": "+50% Schaden",
      "point_cost": 4,
      "effects": [
        {
          "type": "multiply",
          "stat": "damage",
          "value": 1.5
        }
      ],
      "stackable": true,
      "max_stacks": 3,
      "animation_modifiers": {
        "glow_intensity": 1.5,
        "particle_rate": 1.5
      }
    },
    "swift": {
      "id": "swift",
      "name": "Schnellzauber",
      "description": "-25% Cast-Zeit",
      "point_cost": 3,
      "effects": [
        {
          "type": "multiply",
          "stat": "cast_time",
          "value": 0.75
        }
      ],
      "incompatible_with": ["slow_cast"]
    }
  }
}
```

`config/modifiers/behavior_mods.json`

```json
{
  "category": "behavior",
  "modifiers": {
    "explosion": {
      "id": "explosion",
      "name": "Explosion",
      "description": "Explodiert bei Einschlag",
      "point_cost": 6,
      "effects": [
        {
          "type": "on_impact",
          "behavior": "explosion",
          "radius": 3,
          "damage_falloff": {
            "enabled": true,
            "min_percent": 0.5
          }
        }
      ],
      "animation_overrides": {
        "impact": "explosion_medium"
      },
      "sound_overrides": {
        "impact": "explosion_boom"
      },
      "screen_effects": {
        "shake_intensity": 0.4,
        "shake_duration": 0.3
      }
    },
    "homing": {
      "id": "homing",
      "name": "Verfolgend",
      "description": "Folgt dem Ziel",
      "point_cost": 6,
      "effects": [
        {
          "type": "projectile_behavior",
          "behavior": "homing",
          "turn_rate": 90,
          "acquisition_range": 10
        }
      ],
      "valid_spell_types": ["projectile"],
      "incompatible_with": ["spread", "scatter"]
    }
  }
}
```

---

## 🎬 Animations-Konfiguration

`config/animations/effects.json`

```json
{
  "effects": {
    "fireball_projectile": {
      "id": "fireball_projectile",
      "layers": [
        {
          "layer": 1,
          "type": "glow",
          "color": "#FF6600",
          "intensity": 1.2,
          "pulsing": true,
          "pulse_speed": 2.0
        },
        {
          "layer": 2,
          "type": "mesh",
          "asset": "meshes/projectiles/fireball",
          "scale": [1.0, 1.0, 1.0],
          "rotation_mode": "face_velocity"
        },
        {
          "layer": 3,
          "type": "particles",
          "asset": "particles/fire/fire_trail",
          "emit_rate": 100,
          "lifetime": 0.5,
          "attach_point": "center"
        }
      ]
    },
    "explosion_medium": {
      "id": "explosion_medium",
      "duration": 1.0,
      "layers": [
        {
          "layer": 0,
          "type": "decal",
          "asset": "textures/decals/burn_mark",
          "scale": 3.0,
          "fade_duration": 10.0
        },
        {
          "layer": 2,
          "type": "effect",
          "asset": "effects/explosion_fire",
          "scale": 2.0
        },
        {
          "layer": 3,
          "type": "particles",
          "asset": "particles/fire/fire_burst",
          "count": 50,
          "spread": 360
        }
      ]
    }
  }
}
```

---

## 🔊 Sound-Konfiguration

`config/animations/sounds.json`

```json
{
  "sounds": {
    "fire_charge": {
      "asset": "sounds/cast/fire_charge.ogg",
      "volume": 0.8,
      "pitch_range": [0.95, 1.05],
      "spatial": true,
      "max_distance": 30
    },
    "fire_woosh_loop": {
      "asset": "sounds/travel/fire_woosh.ogg",
      "volume": 0.6,
      "loop": true,
      "spatial": true,
      "doppler": true
    },
    "explosion_boom": {
      "asset": "sounds/impact/explosion.ogg",
      "volume": 1.0,
      "spatial": true,
      "max_distance": 50,
      "reverb": true
    }
  }
}
```

---

## 📝 Hinzufügen neuer Inhalte

### Neuen Zauber hinzufügen

1. Öffne die entsprechende Element-Datei in `config/spells/`
2. Füge einen neuen Eintrag hinzu:

```json
{
  "my_new_spell": {
    "id": "my_new_spell",
    "name": "Mein Neuer Zauber",
    "type": "projectile",
    "tier": 1,
    "point_cost": 5,
    "mana_cost": 20,
    "stats": { ... },
    "animations": { ... }
  }
}
```

3. Erstelle ggf. neue Animation-Einträge
4. Server neustarten / Config neu laden

### Neue Modifikation hinzufügen

1. Wähle die passende Kategorie-Datei
2. Füge Modifikation hinzu mit allen Effekten
3. Definiere Inkompatibilitäten

### Neues Element hinzufügen

1. Eintrag in `elements.json`
2. Synergien/Konflikte definieren
3. Neue Zauber-Datei erstellen
4. Farb-Schema definieren

---

## 🔗 Verwandte Dokumente

- [ITEMS.md](./ITEMS.md) - Item-System Details
- [SPELLS.md](./SPELLS.md) - Zauber-System
- [MODIFIERS.md](./MODIFIERS.md) - Modifikationen
- [ANIMATIONS.md](./ANIMATIONS.md) - Animationen

