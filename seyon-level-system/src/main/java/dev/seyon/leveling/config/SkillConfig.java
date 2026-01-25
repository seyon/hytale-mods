package dev.seyon.leveling.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Skill configuration
 */
public class SkillConfig {
    private String id;
    private int tier = 1;
    private String name;
    private String description;
    private int cost = 1;
    private int max_points = 1;
    private Map<String, Double> effects = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getMaxPoints() {
        return max_points;
    }

    public void setMaxPoints(int max_points) {
        this.max_points = max_points;
    }

    public Map<String, Double> getEffects() {
        return effects;
    }

    public void setEffects(Map<String, Double> effects) {
        this.effects = effects;
    }

    /** Merge from loaded: non-null from wins. */
    public void mergeFrom(SkillConfig from) {
        if (from == null) return;
        if (from.id != null) this.id = from.id;
        this.tier = from.tier;
        if (from.name != null) this.name = from.name;
        if (from.description != null) this.description = from.description;
        this.cost = from.cost;
        this.max_points = from.max_points;
        if (from.effects != null && !from.effects.isEmpty()) this.effects.putAll(from.effects);
    }
}
