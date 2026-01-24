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
    }
}
