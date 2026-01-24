# Animationen & Effekte

> Visuelle Darstellung von Zaubern

## 📖 Übersicht

Das Animationssystem besteht aus drei Hauptphasen:
1. **Cast-Phase** - Animation beim Zaubern
2. **Travel-Phase** - Animation während der Bewegung (Projektile)
3. **Impact-Phase** - Animation bei Auslösung/Einschlag

Jede Phase ist separat konfigurierbar und kann mehrere gleichzeitige Effekte haben.

---

## 🎬 Animations-Phasen

### 1. Cast-Animation
Wird beim Spieler angezeigt während der Zauber gewirkt wird.

```
┌─────────────────────────────────────────┐
│ Cast-Animation                          │
├─────────────────────────────────────────┤
│ • Spieler-Animation (Arm-Bewegung)      │
│ • Partikel-Effekte um Spieler/Item      │
│ • Sound-Effekte                         │
│ • Optional: Ladebalken/Kreis            │
│ • Optional: Magischer Kreis unter Füßen │
└─────────────────────────────────────────┘
```

### 2. Travel-Animation
Wird für das Projektil auf dem Weg zum Ziel angezeigt.

```
┌─────────────────────────────────────────┐
│ Travel-Animation                        │
├─────────────────────────────────────────┤
│ • Projektil-Mesh/Sprite                 │
│ • Partikel-Trail (Schweif)              │
│ • Leuchteffekte                         │
│ • Sound-Loop                            │
│ • Optional: Nachglühen/Spur             │
└─────────────────────────────────────────┘
```

### 3. Impact-Animation
Wird am Zielort/bei Einschlag angezeigt.

```
┌─────────────────────────────────────────┐
│ Impact-Animation                        │
├─────────────────────────────────────────┤
│ • Explosions-/Einschlags-Effekt         │
│ • Partikel-Burst                        │
│ • Screen-Shake (optional)               │
│ • Sound-Effekt                          │
│ • Optional: Verbleibende Effekte (DoT)  │
│ • Optional: Decals (Brandflecken etc.)  │
└─────────────────────────────────────────┘
```

---

## 🔥 Element-spezifische Animationen

### Feuer
```yaml
cast:
  particles: fire_embers_rising
  glow: orange_intense
  sound: fire_whoosh
  
travel:
  mesh: fireball_sphere
  trail: fire_trail
  glow: orange_pulsing
  sound: fire_burning_loop
  
impact:
  effect: fire_explosion
  particles: fire_burst
  decal: burn_mark
  sound: fire_impact
  screen_shake: small
```

### Eis
```yaml
cast:
  particles: frost_crystals
  glow: cyan_cold
  sound: ice_forming
  
travel:
  mesh: ice_shard
  trail: frost_trail
  glow: cyan_shimmer
  sound: ice_whistle
  
impact:
  effect: ice_shatter
  particles: ice_fragments
  decal: frost_patch
  sound: ice_break
  freeze_effect: true
```

### Blitz
```yaml
cast:
  particles: electric_sparks
  glow: yellow_flash
  sound: electric_charge
  
travel:
  # Oft leer für Blitze - sie sind instant!
  mesh: null
  trail: null
  
impact:
  effect: lightning_strike
  particles: electric_discharge
  decal: scorch_mark
  sound: thunder_crack
  screen_shake: medium
  flash: white_brief
```

### Wasser
```yaml
cast:
  particles: water_drops
  glow: blue_flowing
  sound: water_swirl
  
travel:
  mesh: water_orb
  trail: water_droplets
  glow: blue_transparent
  sound: water_woosh
  
impact:
  effect: water_splash
  particles: water_spray
  decal: wet_spot
  sound: water_splash
```

### Erde
```yaml
cast:
  particles: dust_rising
  glow: brown_earthy
  sound: rock_rumble
  
travel:
  mesh: rock_chunk
  trail: dust_trail
  glow: none
  sound: rock_woosh
  
impact:
  effect: rock_shatter
  particles: debris_chunks
  decal: crack_pattern
  sound: rock_impact
  screen_shake: medium
```

### Arkane
```yaml
cast:
  particles: arcane_runes
  glow: purple_mystical
  sound: magic_hum
  magic_circle: true
  
travel:
  mesh: arcane_orb
  trail: rune_trail
  glow: purple_pulsing
  sound: magic_pulse
  
impact:
  effect: arcane_burst
  particles: rune_explosion
  decal: magic_sigil
  sound: magic_release
```

---

## 📐 Animations-Layer System

Mehrere Effekte können gleichzeitig abgespielt werden:

```
Layer-Stack (von unten nach oben):
┌───────────────────────────────────┐
│ Layer 4: Overlay-Effekte          │  ← Screen-Flash, UI
│ Layer 3: Partikel-Effekte         │  ← Funken, Rauch
│ Layer 2: Haupt-Effekt             │  ← Feuerball, Explosion
│ Layer 1: Basis-Effekte            │  ← Glühen, Trails
│ Layer 0: Decals/Ground            │  ← Bodentexturen
└───────────────────────────────────┘
```

### Beispiel: Verstärkter Explosiver Feuerball

```yaml
travel_layers:
  - layer: 1
    type: glow
    color: "#FF4500"
    intensity: 1.5  # Verstärkt
    
  - layer: 2
    type: mesh
    asset: fireball_large  # Vergrößert
    scale: 2.0
    
  - layer: 3
    type: particles
    asset: fire_trail_intense
    rate: 200  # Mehr Partikel
    
impact_layers:
  - layer: 0
    type: decal
    asset: burn_mark_large
    
  - layer: 2
    type: effect
    asset: fire_explosion_large
    scale: 2.5  # Explosion modifier
    
  - layer: 3
    type: particles
    asset: explosion_debris
    count: 50
    
  - layer: 4
    type: screen_shake
    intensity: 0.4
    duration: 0.3
```

---

## 🔄 Modifikations-Animations-Mapping

Modifikationen fügen zusätzliche visuelle Effekte hinzu:

| Modifikation | Zusätzliche Effekte |
|--------------|---------------------|
| Verstärkt | Intensiveres Glühen, +50% Partikel |
| Explosion | Explosions-Layer bei Impact |
| Dreifach | 3x Travel-Animation |
| Verfolgend | Leuchtendes Zielmarker |
| Brennend | Zusätzlicher DoT-Partikeleffekt |
| Einfrierend | Eis-Kristall Overlay auf Ziel |
| Kettenreaktion | Verbindungs-Blitz zwischen Zielen |
| Durchbohrend | Durchdring-Effekt + Mehrfach-Impact |

---

## ⚡ Effekt-Kombinierung

### Additive Effekte
```yaml
# Basis-Feuerball Glow
base_glow:
  color: "#FF4500"
  intensity: 1.0

# + Verstärkt Modifier
enhanced_glow:
  intensity_add: 0.5  # → 1.5 total

# + Explosion Modifier  
explosive_glow:
  intensity_add: 0.3  # → 1.8 total
  pulse: true
```

### Ersetzende Effekte
```yaml
# Basis-Impact
base_impact: fire_impact_small

# Mit Explosion Modifier → Ersetzt
impact_override: fire_explosion_medium

# Mit Große Explosion → Ersetzt erneut
impact_override: fire_explosion_large
```

---

## 🎵 Sound-System

Jede Phase hat eigene Sound-Slots:

```yaml
sounds:
  cast:
    - sound: fire_charge
      volume: 0.8
      delay: 0.0
    - sound: magic_hum
      volume: 0.3
      delay: 0.1
      loop_until_cast_end: true
      
  travel:
    - sound: fireball_woosh
      volume: 0.6
      loop: true
      pitch_variation: 0.1
      
  impact:
    - sound: explosion_boom
      volume: 1.0
    - sound: fire_crackle
      volume: 0.5
      delay: 0.2
      duration: 2.0
```

---

## 🎨 Farb-Schemas nach Element

```yaml
color_schemes:
  fire:
    primary: "#FF4500"    # Orange-Rot
    secondary: "#FFD700"  # Gold
    accent: "#FF0000"     # Rot
    glow: "#FF6600"       # Helles Orange
    
  ice:
    primary: "#00BFFF"    # Cyan
    secondary: "#FFFFFF"  # Weiß
    accent: "#87CEEB"     # Hellblau
    glow: "#ADD8E6"       # Eisblau
    
  lightning:
    primary: "#FFFF00"    # Gelb
    secondary: "#FFFFFF"  # Weiß
    accent: "#00FFFF"     # Elektrisch Blau
    glow: "#FFFFA0"       # Blitz-Gelb
    
  earth:
    primary: "#8B4513"    # Braun
    secondary: "#D2691E"  # Erde
    accent: "#556B2F"     # Dunkelgrün
    glow: "#9B7653"       # Sandig
    
  water:
    primary: "#1E90FF"    # Blau
    secondary: "#00CED1"  # Türkis
    accent: "#FFFFFF"     # Weiß (Schaum)
    glow: "#87CEFA"       # Wasserblau
    
  arcane:
    primary: "#9400D3"    # Dunkel-Violett
    secondary: "#DA70D6"  # Orchidee
    accent: "#FFD700"     # Gold-Runen
    glow: "#BA55D3"       # Magisch-Lila
```

---

## 📦 Asset-Struktur

```
assets/
├── meshes/
│   ├── projectiles/
│   │   ├── fireball_small.fbx
│   │   ├── fireball_medium.fbx
│   │   ├── fireball_large.fbx
│   │   ├── ice_shard.fbx
│   │   └── ...
│   └── effects/
│       ├── explosion_fire.fbx
│       └── ...
│
├── particles/
│   ├── fire/
│   │   ├── fire_trail.particle
│   │   ├── fire_burst.particle
│   │   └── fire_embers.particle
│   ├── ice/
│   └── ...
│
├── textures/
│   ├── decals/
│   │   ├── burn_mark.png
│   │   ├── frost_patch.png
│   │   └── ...
│   └── glows/
│
├── sounds/
│   ├── cast/
│   ├── travel/
│   └── impact/
│
└── animations/
    ├── player/
    │   ├── cast_wand.anim
    │   └── cast_grimoire.anim
    └── ...
```

---

## 💾 Konfigurations-Struktur (Beispiel)

```json
{
  "spell_animations": {
    "fireball": {
      "cast": {
        "player_animation": "cast_wand_fire",
        "duration": 0.5,
        "layers": [
          {
            "type": "particles",
            "asset": "fire_embers_rising",
            "attach_to": "player_hand"
          },
          {
            "type": "glow",
            "color": "#FF4500",
            "intensity": 1.0,
            "attach_to": "player_hand"
          }
        ],
        "sounds": [
          {
            "asset": "fire_charge",
            "volume": 0.8
          }
        ]
      },
      "travel": {
        "duration": "until_impact",
        "layers": [
          {
            "type": "mesh",
            "asset": "fireball_medium",
            "scale": 1.0,
            "rotation": "face_direction"
          },
          {
            "type": "trail",
            "asset": "fire_trail",
            "length": 2.0,
            "fade": true
          },
          {
            "type": "glow",
            "color": "#FF6600",
            "intensity": 1.2
          }
        ],
        "sounds": [
          {
            "asset": "fire_woosh",
            "loop": true,
            "volume": 0.6
          }
        ]
      },
      "impact": {
        "duration": 1.0,
        "layers": [
          {
            "type": "effect",
            "asset": "fire_impact",
            "scale": 1.0
          },
          {
            "type": "particles",
            "asset": "fire_burst",
            "count": 30
          },
          {
            "type": "decal",
            "asset": "burn_mark",
            "duration": 10.0,
            "fade_out": 2.0
          }
        ],
        "sounds": [
          {
            "asset": "fire_impact",
            "volume": 1.0
          }
        ],
        "screen_effects": {
          "shake": {
            "intensity": 0.1,
            "duration": 0.2
          }
        }
      }
    }
  },
  "modifier_animation_overrides": {
    "explosion": {
      "impact": {
        "layers_add": [
          {
            "type": "effect",
            "asset": "explosion_medium",
            "scale": 1.5
          }
        ],
        "screen_effects": {
          "shake": {
            "intensity": 0.4,
            "duration": 0.3
          }
        }
      }
    },
    "enhanced": {
      "all_phases": {
        "glow_intensity_multiply": 1.5,
        "particle_rate_multiply": 1.5
      }
    }
  }
}
```

---

## 🔗 Verwandte Dokumente

- [SPELLS.md](./SPELLS.md) - Welche Zauber welche Animationen nutzen
- [MODIFIERS.md](./MODIFIERS.md) - Wie Modifikationen Animationen ändern
- [CONFIG.md](./CONFIG.md) - Vollständige Konfigurations-Dokumentation

