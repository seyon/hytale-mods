package dev.seyon.magic.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Main configuration for the magic system (main.json)
 */
public class MagicMainConfig {
    
    private ModInfo mod_info = new ModInfo();
    private GlobalSettings global_settings = new GlobalSettings();
    private BalanceMultipliers balance_multipliers = new BalanceMultipliers();
    private Features features = new Features();

    public ModInfo getModInfo() {
        return mod_info;
    }

    public GlobalSettings getGlobalSettings() {
        return global_settings;
    }

    public BalanceMultipliers getBalanceMultipliers() {
        return balance_multipliers;
    }

    public Features getFeatures() {
        return features;
    }

    public static class ModInfo {
        private String name = "Arcane Arts";
        private String version = "1.0.0";
        private String author = "Christian Wielath";

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getAuthor() {
            return author;
        }
    }

    public static class GlobalSettings {
        private double mana_regen_rate = 5.0;
        private double mana_regen_delay_after_cast = 2.0;
        private double global_cooldown = 0.5;
        private int max_active_spells = 10;
        private boolean spell_collision = true;
        private boolean friendly_fire = false;

        public double getManaRegenRate() {
            return mana_regen_rate;
        }

        public double getManaRegenDelayAfterCast() {
            return mana_regen_delay_after_cast;
        }

        public double getGlobalCooldown() {
            return global_cooldown;
        }

        public int getMaxActiveSpells() {
            return max_active_spells;
        }

        public boolean isSpellCollision() {
            return spell_collision;
        }

        public boolean isFriendlyFire() {
            return friendly_fire;
        }
    }

    public static class BalanceMultipliers {
        private double damage = 1.0;
        private double mana_cost = 1.0;
        private double cooldown = 1.0;
        private double point_costs = 1.0;

        public double getDamage() {
            return damage;
        }

        public double getManaCost() {
            return mana_cost;
        }

        public double getCooldown() {
            return cooldown;
        }

        public double getPointCosts() {
            return point_costs;
        }
    }

    public static class Features {
        private boolean spell_crafting = true;
        private boolean quality_system = true;
        private boolean affinity_system = true;
        private boolean combo_system = false;

        public boolean isSpellCrafting() {
            return spell_crafting;
        }

        public boolean isQualitySystem() {
            return quality_system;
        }

        public boolean isAffinitySystem() {
            return affinity_system;
        }

        public boolean isComboSystem() {
            return combo_system;
        }
    }
}
