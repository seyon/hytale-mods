package dev.seyon.leveling.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Action to EXP mapping configuration
 */
public class ActionConfig {
    private String category;
    private List<ActionMapping> actions = new ArrayList<>();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ActionMapping> getActions() {
        return actions;
    }

    public void setActions(List<ActionMapping> actions) {
        this.actions = actions;
    }

    public static class ActionMapping {
        private String action_id;
        private double exp;
        /** Optional. Multiplier for EXP (e.g. harder trees). Default 1.0. */
        private Double difficulty_factor;

        public String getActionId() {
            return action_id;
        }

        public void setActionId(String action_id) {
            this.action_id = action_id;
        }

        public double getExp() {
            return exp;
        }

        public void setExp(double exp) {
            this.exp = exp;
        }

        /** Returns difficulty_factor or 1.0 if not set. */
        public double getDifficultyFactor() {
            return difficulty_factor != null ? difficulty_factor : 1.0;
        }

        public void setDifficultyFactor(Double difficulty_factor) {
            this.difficulty_factor = difficulty_factor;
        }
    }

    /**
     * Merge from loaded: file has higher priority. Actions merged by action_id: file overwrites default;
     * file-only actions are added; default-only actions are kept.
     */
    public void mergeFrom(ActionConfig from) {
        if (from == null) return;
        if (from.category != null) this.category = from.category;
        if (from.actions == null || from.actions.isEmpty()) return;
        java.util.Set<String> fromIds = new java.util.HashSet<>();
        for (ActionMapping m : from.actions) if (m != null && m.getActionId() != null) fromIds.add(m.getActionId());
        java.util.Map<String, ActionMapping> out = new java.util.LinkedHashMap<>();
        for (ActionMapping m : from.actions) if (m != null && m.getActionId() != null) out.put(m.getActionId(), m);
        for (ActionMapping m : this.actions) {
            if (m != null && m.getActionId() != null && !fromIds.contains(m.getActionId())) out.put(m.getActionId(), m);
        }
        this.actions = new ArrayList<>(out.values());
    }
}
