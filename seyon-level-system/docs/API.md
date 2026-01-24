# API Documentation

## Overview

The Seyon Level System API allows other mods to integrate with the Level System by:
- Registering custom categories
- Registering actions that grant experience
- Querying player levels and skills
- Accessing player modifiers

## Getting Started

### 1. Add Dependency

In your `manifest.json`:

```json
{
  "Dependencies": {
    "Seyon:SeyonLevelSystem": "*"
  }
}
```

### 2. Get API Instance

```java
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.api.LevelSystemAPI;

public class MyPlugin extends JavaPlugin {
    private LevelSystemAPI levelSystemAPI;
    
    @Override
    protected void setup() {
        // Get the API
        this.levelSystemAPI = SeyonLevelSystemPlugin.getInstance().getAPI();
    }
}
```

## API Methods

### Register a Custom Category

```java
import dev.seyon.leveling.config.*;

LevelSystemCategory myCategory = new LevelSystemCategory();
myCategory.setId("my_category");
myCategory.setDisplayName("My Category");
myCategory.setDescription("Custom category description");

// Configure EXP curve
ExpCurveConfig expCurve = new ExpCurveConfig();
expCurve.setType("exponential");
expCurve.setBase(100.0);
expCurve.setMultiplier(1.5);
myCategory.setExpCurve(expCurve);

// Add skills
SkillConfig skill = new SkillConfig();
skill.setId("my_skill");
skill.setName("My Skill");
skill.setDescription("Custom skill description");
skill.setCost(1);
skill.setMaxPoints(5);
skill.getEffects().put("custom_effect", 0.1);
myCategory.getSkills().add(skill);

// Register the category
levelSystemAPI.registerCategory(myCategory);
```

### Register Actions

```java
// Register an action that grants EXP
levelSystemAPI.registerAction("my_custom_action", "my_category", 25.0);
```

### Grant Experience

```java
// Grant experience to a player
UUID playerId = player.getUuid();
levelSystemAPI.grantExperience(playerId, "mining", 100.0);
```

### Query Player Data

```java
// Get player's level in a category
int level = levelSystemAPI.getPlayerLevel(playerId, "mining");

// Check if player has a skill
boolean hasSkill = levelSystemAPI.hasSkill(playerId, "mining", "efficient_mining");

// Get a modifier value
double healthBonus = levelSystemAPI.getModifierValue(playerId, "max_health");
```

### Check Category Existence

```java
if (levelSystemAPI.hasCategory("mining")) {
    // Category exists
}
```

## Example: Custom Magic Category

```java
public class MagicIntegration {
    
    public static void registerMagicCategory(LevelSystemAPI api) {
        LevelSystemCategory magic = new LevelSystemCategory();
        magic.setId("magic");
        magic.setDisplayName("Magie");
        magic.setDescription("Meistere die arkanen Künste");
        
        // EXP Curve
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(150.0);
        expCurve.setMultiplier(1.2);
        magic.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("max_mana", 5.0);
            bonus.getModifiers().put("spell_power", 1.0);
            magic.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("mana_efficiency");
        skill1.setTier(1);
        skill1.setName("Mana-Effizienz");
        skill1.setDescription("Reduziert Manakosten um 5% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("mana_cost_reduction", 0.05);
        magic.getSkills().add(skill1);
        
        // Register
        api.registerCategory(magic);
        
        // Register actions
        api.registerAction("cast_fireball", "magic", 30.0);
        api.registerAction("cast_heal", "magic", 20.0);
        api.registerAction("cast_teleport", "magic", 50.0);
    }
}
```

## Events Integration

To grant experience based on events, listen to Hytale events and call the API:

```java
@EventHandler
public void onSpellCast(SpellCastEvent event) {
    Player player = event.getPlayer();
    String spellId = event.getSpell().getId();
    
    // Grant experience based on spell
    levelingAPI.grantExperience(player.getUuid(), "magic", 25.0);
}
```

## Best Practices

1. **Category IDs**: Use descriptive, lowercase IDs with underscores (e.g., `my_mod_combat`)
2. **Action IDs**: Use descriptive action IDs that clearly indicate what triggers them
3. **EXP Values**: Balance EXP rewards based on difficulty and frequency of actions
4. **Modifiers**: Use consistent modifier IDs across mods for compatibility
5. **Skills**: Design skills that are interesting but not overpowered

## Common Modifier IDs

Use these standard modifier IDs for compatibility:

- `max_health` - Maximum health
- `max_mana` - Maximum mana
- `max_stamina` - Maximum stamina
- `damage` - Generic damage bonus
- `melee_damage` - Melee damage bonus
- `ranged_damage` - Ranged damage bonus
- `spell_power` - Spell/magic damage bonus
- `movement_speed` - Movement speed multiplier
- `mining_speed` - Mining speed multiplier
- `woodcutting_speed` - Woodcutting speed multiplier

## Support

For questions or issues, please visit our GitHub repository or Discord server.
