package dev.seyon.leveling.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Override config for farming harvest EXP. Located at SeyonLevelSystem/config/farming_harvest.json.
 * Keys are block IDs (e.g. Plant_Crop_Wheat_Block); values override the default EXP for harvest_&lt;blockId&gt;.
 * Entries not present in actions/farming.json will be added with the given EXP.
 */
public class FarmingHarvestOverrideConfig {

    private Map<String, Double> overrides = new HashMap<>();

    public Map<String, Double> getOverrides() {
        return overrides;
    }

    public void setOverrides(Map<String, Double> overrides) {
        this.overrides = overrides != null ? overrides : new HashMap<>();
    }

    /** Merge from loaded: from.overrides override this. */
    public void mergeFrom(FarmingHarvestOverrideConfig from) {
        if (from == null) return;
        if (from.overrides != null && !from.overrides.isEmpty()) this.overrides.putAll(from.overrides);
    }
}
