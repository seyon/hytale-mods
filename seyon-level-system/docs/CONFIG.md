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
  "display_name": "Miner",
  "description": "Mine ores and gather resources",
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
      "name": "Efficient Mining",
      "description": "10% faster ore mining",
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
    {"action_id": "break_Stone", "exp": 1.0, "difficulty_factor": 1.0},
    {"action_id": "break_Ore_Iron_Stone", "exp": 2.0, "difficulty_factor": 1.0},
    {"action_id": "break_Ore_Gold_Stone", "exp": 6.0, "difficulty_factor": 1.2},
    {"action_id": "break_Ore_Cobalt_Stone", "exp": 18.0, "difficulty_factor": 1.7},
    {"action_id": "break_Ore_Adamantite_Volcanic", "exp": 40.0, "difficulty_factor": 2.2}
  ]
}
```

### Optional: difficulty_factor (Woodcutting and Mining)

For `break_<blockId>` actions you can set `difficulty_factor` (default 1.0). **total = exp × difficulty_factor** per block (one BreakBlockEvent = one block).

- **Woodcutting:** One block = one event. Formula: `exp × difficulty_factor` per log. Example: `{"action_id": "break_Wood_Crystal_Trunk", "exp": 5.0, "difficulty_factor": 2.0}` — each block gives 10 EXP. (Column counting was removed: it is not reliable whether blocks above will fall; a future Hytale event would be needed for that.)
- **Mining:** One block = one event. Formula: `exp × difficulty_factor` per block. The factor reflects Hytale ore rarity and hardness. Default config uses [Blocks.Ores](https://www.hytaleitemids.com/api/items?page=1&limit=500&category=Blocks.Ores); exp and factor are tuned by rarity: Common (Iron, Copper) < Gold < Silver/Thorium < Cobalt < Adamantite. Mithril and Onyxium are not in Survival but included for future use.

Block IDs for logs: e.g. `Wood_Oak_Trunk`, `Wood_Crystal_Trunk` ([hytaleitemids.com](https://www.hytaleitemids.com/api/items?search=log)). Ore IDs: e.g. `Ore_Iron_Stone`, `Ore_Adamantite_Volcanic`, `Rock_Gem_Emerald`.

### Farming Harvest (harvest_&lt;blockId&gt;) and overrides

All blocks matching `Plant_Crop_*_Block` (from [hytaleitemids.com Plant_Crop_](https://www.hytaleitemids.com/api/items?search=Plant_Crop_&quality=Common)) grant Farming EXP when harvested (UseBlockEvent.Post). The default config includes:

- **14 crops in fixed order** (slightly more EXP each; farming is easy and done in bulk): Wheat (3.0), Lettuce (3.2), Carrot (3.4), Corn (3.6), Cauliflower (3.8), Turnip (4.0), Aubergine (4.2), Pumpkin (4.4), Tomato (4.6), Chilli (4.8), Cotton (5.0), Rice (5.2), Onion (5.4), Potato (5.6).
- **All other** `Plant_Crop_*_Block` from the API (Health/Mana/Stamina crops, mushrooms, berries, etc.) with default **4.0** EXP.

**Override config:** `SeyonLevelSystem/config/farming_harvest.json` lets you override EXP (and add extra block IDs):

```json
{
  "overrides": {
    "Plant_Crop_Wheat_Block": 4.0,
    "Plant_Crop_Apple_Block": 3.5
  }
}
```

Keys are **block IDs** (without `harvest_`). Values override the EXP for `harvest_<blockId>`. If a block ID is not in `actions/farming.json`, it is **added** with the given EXP. Reload with `/seyon-level reload`.

Action IDs should match the events/actions that trigger them in your mod or Hytale.

### Event-based Action IDs (built-in)

These action IDs are triggered automatically by Hytale events:

| Category        | Action ID            | Event / Trigger                          |
|-----------------|----------------------|------------------------------------------|
| mining          | `break_<blockId>`    | BreakBlockEvent (e.g. `break_stone`)     |
| woodcutting     | `break_<blockId>`    | BreakBlockEvent; `difficulty_factor` (e.g. `break_Wood_Oak_Trunk`)                 |
| combat_melee    | `kill_enemy_melee`   | Entity death, killer used melee (EntitySource)   |
| combat_ranged   | `kill_enemy_ranged`  | Entity death, killer used ranged (ProjectileSource) |
| exploration     | `discover_zone`      | DiscoverZoneEvent (zone/region discovery)|
| exploration     | `explore_steps`      | Every 100 blocks walked (ExplorationWalkExpSystem)|
| exploration     | `discover_instance`  | DiscoverInstanceEvent.Display (instance discovery; requires instances builtin)|
| farming         | `harvest_<blockId>`  | UseBlockEvent.Post (harvest crop; fallback `harvest_crop`)|
| farming         | `harvest_animal`     | *(no event yet)*                         |
| farming         | `place_<blockId>`   | PlaceBlockEvent (from ItemStack.getBlockKey())|
| farming         | `place_<itemId>`     | PlaceBlockEvent (from ItemStack.getItemId() when blockKey not set)|
| crafting        | `craft_item`        | CraftRecipeEvent.Post (exp × quantity)   |
| crafting        | `craft_<recipeId>`   | CraftRecipeEvent.Post (per-recipe, exp × quantity)|

Add these to your `actions/<category>.json` to enable EXP. The exploration category also grants **movement_speed** per level (level bonuses); configure in the category's `level_bonuses` (e.g. `movement_speed: 0.01` = 1% per level). Magic (Seyon Arcane Arts) uses `cast_spell_<quality>` and is registered by the Magic integration.

## Java Defaults and Merge

All configuration is defined as **Java objects** in `DefaultConfigProvider`. On each load:

1. **Java defaults** are taken from `DefaultConfigProvider` (main, categories, actions, farming_harvest overrides).
2. **Existing config files** are read. **Files have higher priority**: non-null values from files override the Java defaults (merge).
3. The **merged result** is written back to the config directory. So config files are always brought up to date with any new keys from Java; user changes in files are preserved.

- **New installation:** No files exist; Java defaults are used and then saved. You get full `main.json`, `categories/*.json`, `actions/*.json`, and `farming_harvest.json`.
- **Upgrade:** New keys from Java (e.g. new actions, new categories) are merged in; your existing file values override. After load, the merged state is saved.
- **Custom categories/actions:** JSON files in `categories/` or `actions/` whose id/category is not in the Java defaults are loaded and saved as-is (no Java merge for them).

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
