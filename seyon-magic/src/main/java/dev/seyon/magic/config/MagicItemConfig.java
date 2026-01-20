package dev.seyon.magic.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for magic items (wands.json / grimoires.json)
 */
public class MagicItemConfig {
    
    private String item_type = "wand";
    private BaseStats base_stats = new BaseStats();
    private Map<String, ItemQualityConfig> qualities = new HashMap<>();

    public MagicItemConfig() {
        // Initialize default qualities
        initializeDefaultQualities();
    }

    private void initializeDefaultQualities() {
        // Common
        ItemQualityConfig common = new ItemQualityConfig();
        common.setColor("#FFFFFF");
        common.setMana(50);
        common.setPowerMult(1.0);
        common.setSpeedMult(1.0);
        common.setMaxModifiers(2);
        common.setPointLimit(10);
        common.setAffinitySlots(1);
        common.setSpellSlots(1);
        common.setDropWeight(100);
        qualities.put("common", common);

        // Uncommon
        ItemQualityConfig uncommon = new ItemQualityConfig();
        uncommon.setColor("#00FF00");
        uncommon.setMana(75);
        uncommon.setPowerMult(1.1);
        uncommon.setSpeedMult(1.1);
        uncommon.setMaxModifiers(3);
        uncommon.setPointLimit(15);
        uncommon.setAffinitySlots(1);
        uncommon.setSpellSlots(2);
        uncommon.setDropWeight(50);
        qualities.put("uncommon", uncommon);

        // Rare
        ItemQualityConfig rare = new ItemQualityConfig();
        rare.setColor("#0080FF");
        rare.setMana(100);
        rare.setPowerMult(1.25);
        rare.setSpeedMult(1.2);
        rare.setMaxModifiers(4);
        rare.setPointLimit(20);
        rare.setAffinitySlots(2);
        rare.setSpellSlots(2);
        rare.setDropWeight(20);
        qualities.put("rare", rare);

        // Epic
        ItemQualityConfig epic = new ItemQualityConfig();
        epic.setColor("#9400D3");
        epic.setMana(150);
        epic.setPowerMult(1.5);
        epic.setSpeedMult(1.3);
        epic.setMaxModifiers(5);
        epic.setPointLimit(30);
        epic.setAffinitySlots(2);
        epic.setSpellSlots(3);
        epic.setDropWeight(5);
        qualities.put("epic", epic);

        // Legendary
        ItemQualityConfig legendary = new ItemQualityConfig();
        legendary.setColor("#FFD700");
        legendary.setMana(200);
        legendary.setPowerMult(2.0);
        legendary.setSpeedMult(1.5);
        legendary.setMaxModifiers(6);
        legendary.setPointLimit(40);
        legendary.setAffinitySlots(3);
        legendary.setSpellSlots(4);
        legendary.setDropWeight(1);
        qualities.put("legendary", legendary);
    }

    public String getItemType() {
        return item_type;
    }

    public BaseStats getBaseStats() {
        return base_stats;
    }

    public Map<String, ItemQualityConfig> getQualities() {
        return qualities;
    }

    public static class BaseStats {
        private double cast_speed_multiplier = 1.0;
        private double power_multiplier = 1.0;

        public double getCastSpeedMultiplier() {
            return cast_speed_multiplier;
        }

        public double getPowerMultiplier() {
            return power_multiplier;
        }
    }
}
