# Configuration Guide

## Main Configuration

The main configuration file is located at `SeyonLevelSystem/config/main.json`.

```json
{
  "mod_info": {
    "name": "Seyon Level System",
    "version": "1.0.0",
    "author": "Christian Wielath"
  },
  "global_settings": {
    "max_level": 100,
    "exp_overflow_enabled": false,
    "skill_points_per_level": 1,
    "allow_skill_respec": true,
    "respec_cost_type": "items"
  },
  "milestone_intervals": {
    "quest_every_n_levels": 10,
    "levels_requiring_quest": [10, 20, 30, 40, 50]
  }
}
```

### Global Settings

- `max_level` - Maximum level for all categories
- `exp_overflow_enabled` - Allow EXP to overflow when level up is pending
- `skill_points_per_level` - Number of skill points granted per level
- `allow_skill_respec` - Allow players to reset skills
- `respec_cost_type` - Type of cost for skill reset (`items`, `currency`, or `free`)

### Milestone Intervals

- `quest_every_n_levels` - Require a quest every N levels (set to 0 to disable)
- `levels_requiring_quest` - Specific levels that require quests

## Category Configuration

Categories are defined in separate JSON files in `SeyonLevelSystem/config/categories/`.

### Example: Mining Category

```json
{
  "id": "mining",
  "display_name": "Minenarbeiter",
  "description": "Grabe Erze und sammle Ressourcen",
  "icon": "Server/Item/Icons/Mining.png",
  "exp_curve": {
    "type": "exponential",
    "base": 100,
    "multiplier": 1.5,
    "custom_formula": "base * pow(multiplier, level - 1)"
  },
  "level_bonuses": [
    {
      "level": 1,
      "modifiers": {
        "mining_speed": 0.01,
        "max_health": 2
      }
    }
  ],
  "skills": [
    {
      "id": "efficient_mining",
      "tier": 1,
      "name": "Effizientes Abbauen",
      "description": "10% schnellerer Erzabbau",
      "cost": 1,
      "max_points": 5,
      "effects": {
        "mining_speed_ores": 0.02
      }
    }
  ],
  "milestones": {
    "10": {
      "type": "simple_talk",
      "npc_id": "mining_master_001",
      "dialog_key": "milestone_10"
    },
    "20": {
      "type": "item_collection",
      "npc_id": "mining_master_001",
      "required_items": [
        {"item_id": "iron_ore", "amount": 50},
        {"item_id": "gold_ore", "amount": 10}
      ]
    }
  }
}
```

### EXP Curves

Three types of EXP curves are supported:

#### Linear
```json
"exp_curve": {
  "type": "linear",
  "base": 100,
  "multiplier": 1.0
}
```
Formula: `base * level`

#### Exponential
```json
"exp_curve": {
  "type": "exponential",
  "base": 100,
  "multiplier": 1.5
}
```
Formula: `base * pow(multiplier, level - 1)`

#### Custom
```json
"exp_curve": {
  "type": "custom",
  "base": 100,
  "multiplier": 1.5,
  "custom_formula": "base * level * multiplier"
}
```
Use any formula with `base`, `multiplier`, and `level` variables.

### Level Bonuses

Level bonuses are modifiers automatically applied at specific levels:

```json
"level_bonuses": [
  {
    "level": 5,
    "modifiers": {
      "max_health": 10,
      "damage": 1,
      "movement_speed": 0.01
    }
  }
]
```

### Skills

Skills are tier-based abilities that players can unlock with skill points:

```json
"skills": [
  {
    "id": "skill_id",
    "tier": 1,
    "name": "Skill Name",
    "description": "Skill description",
    "cost": 1,
    "max_points": 5,
    "effects": {
      "effect_id": 0.05
    }
  }
]
```

- `tier` - Skill tier (1-3, determines UI organization)
- `cost` - Skill points required per level
- `max_points` - Maximum number of times skill can be upgraded
- `effects` - Map of effect IDs to values (multiplied by skill level)

### Milestones

Milestones are quests required at specific levels to continue progression:

#### Simple Talk Quest
```json
"milestones": {
  "10": {
    "type": "simple_talk",
    "npc_id": "quest_giver",
    "dialog_key": "quest_dialog_10"
  }
}
```

#### Item Collection Quest
```json
"milestones": {
  "20": {
    "type": "item_collection",
    "npc_id": "quest_giver",
    "required_items": [
      {"item_id": "item_1", "amount": 10},
      {"item_id": "item_2", "amount": 5}
    ]
  }
}
```

## Action Configuration

Actions define what grants EXP and how much. They are defined in `SeyonLevelSystem/config/actions/`.

### Example: Mining Actions

```json
{
  "category": "mining",
  "actions": [
    {"action_id": "break_stone", "exp": 1.0},
    {"action_id": "break_coal_ore", "exp": 5.0},
    {"action_id": "break_iron_ore", "exp": 10.0},
    {"action_id": "break_gold_ore", "exp": 20.0},
    {"action_id": "break_diamond_ore", "exp": 50.0}
  ]
}
```

Action IDs should match the events/actions that trigger them in your mod or Hytale.

### Event-based Action IDs (built-in)

These action IDs are triggered automatically by Hytale events:

| Category        | Action ID           | Event / Trigger                          |
|-----------------|---------------------|------------------------------------------|
| mining          | `break_<blockId>`   | BreakBlockEvent (e.g. `break_stone`)     |
| woodcutting     | `break_<blockId>`   | BreakBlockEvent (e.g. `break_oak_log`)   |
| combat_melee    | `kill_enemy_melee`  | Entity death, killer used melee (EntitySource)   |
| combat_ranged   | `kill_enemy_ranged` | Entity death, killer used ranged (ProjectileSource) |
| exploration     | `discover_zone`     | DiscoverZoneEvent (zone/region discovery)|
| exploration     | `explore_steps`    | Every 100 blocks walked (ExplorationWalkExpSystem)|

Add these to your `actions/<category>.json` to enable EXP. The exploration category also grants **movement_speed** per level (level bonuses); configure in the category's `level_bonuses` (e.g. `movement_speed: 0.01` = 1% per level). Magic (Seyon Arcane Arts) uses `cast_spell_<quality>` and is registered by the Magic integration.

## Config Migrations

The plugin maintains `SeyonLevelSystem/config/migrations.json`, which records which config migrations have already been applied. **Migrations only add missing entries**; if a value exists (even if it differs from the default), it is never changed.

- **New installation:** On first start with empty `categories/` and `actions/`, all known migrations are marked as applied and none are executed. Default configs are created by the normal flow.
- **Upgrade:** If `migrations.json` is missing but you have existing configs, pending migrations run once and add only missing keys (e.g. `explore_steps` in `actions/exploration.json`). After that, they are marked applied and not run again.
- **Developer note:** To add a new migration, extend `ConfigMigrationRunner.ALL_MIGRATION_IDS` and implement the corresponding case in `runMigration()` (idempotent: only add when the entry is absent).

## Creating Custom Categories

1. Create a new JSON file in `SeyonLevelSystem/config/categories/`
2. Define the category structure (see example above)
3. Create a corresponding actions file in `SeyonLevelSystem/config/actions/`
4. Reload the configuration with `/seyon-level reload`

## Tips

- Start with lower EXP values and adjust based on testing
- Use exponential curves for combat categories, linear for passive categories
- Balance skill costs and max points to create interesting choices
- Use milestone quests to gate progression and add narrative

## Troubleshooting

If categories don't load:
- Check JSON syntax with a validator
- Ensure file names match category IDs
- Check server console for error messages
- Use `/seyon-level reload` to reload configuration
