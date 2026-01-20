package dev.seyon.magic.config;

/**
 * Configuration for a single item quality tier
 */
public class ItemQualityConfig {
    
    private String color = "#FFFFFF";
    private int mana = 50;
    private double power_mult = 1.0;
    private double speed_mult = 1.0;
    private int max_modifiers = 2;
    private int point_limit = 10;
    private int affinity_slots = 1;
    private int spell_slots = 1;
    private int drop_weight = 100;

    public String getColor() {
        return color;
    }

    public int getMana() {
        return mana;
    }

    public double getPowerMult() {
        return power_mult;
    }

    public double getSpeedMult() {
        return speed_mult;
    }

    public int getMaxModifiers() {
        return max_modifiers;
    }

    public int getPointLimit() {
        return point_limit;
    }

    public int getAffinitySlots() {
        return affinity_slots;
    }

    public int getSpellSlots() {
        return spell_slots;
    }

    public int getDropWeight() {
        return drop_weight;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setPowerMult(double power_mult) {
        this.power_mult = power_mult;
    }

    public void setSpeedMult(double speed_mult) {
        this.speed_mult = speed_mult;
    }

    public void setMaxModifiers(int max_modifiers) {
        this.max_modifiers = max_modifiers;
    }

    public void setPointLimit(int point_limit) {
        this.point_limit = point_limit;
    }

    public void setAffinitySlots(int affinity_slots) {
        this.affinity_slots = affinity_slots;
    }

    public void setSpellSlots(int spell_slots) {
        this.spell_slots = spell_slots;
    }

    public void setDropWeight(int drop_weight) {
        this.drop_weight = drop_weight;
    }
}
