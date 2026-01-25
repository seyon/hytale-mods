package dev.seyon.leveling.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Level bonus configuration
 */
public class LevelBonusConfig {
    private int level;
    private Map<String, Double> modifiers = new HashMap<>();

    public LevelBonusConfig() {
    }

    public LevelBonusConfig(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<String, Double> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Map<String, Double> modifiers) {
        this.modifiers = modifiers;
    }

    /** Merge from loaded: from.modifiers override this. */
    public void mergeFrom(LevelBonusConfig from) {
        if (from == null) return;
        this.level = from.level;
        if (from.modifiers != null && !from.modifiers.isEmpty()) this.modifiers.putAll(from.modifiers);
    }
}
