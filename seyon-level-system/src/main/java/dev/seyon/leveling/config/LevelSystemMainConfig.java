package dev.seyon.leveling.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Main configuration for Level System (main.json)
 */
public class LevelSystemMainConfig {
    
    private ModInfo mod_info = new ModInfo();
    private GlobalSettings global_settings = new GlobalSettings();
    private MilestoneIntervals milestone_intervals = new MilestoneIntervals();

    public ModInfo getModInfo() {
        return mod_info;
    }

    public void setModInfo(ModInfo mod_info) {
        this.mod_info = mod_info;
    }

    public GlobalSettings getGlobalSettings() {
        return global_settings;
    }

    public void setGlobalSettings(GlobalSettings global_settings) {
        this.global_settings = global_settings;
    }

    public MilestoneIntervals getMilestoneIntervals() {
        return milestone_intervals;
    }

    public void setMilestoneIntervals(MilestoneIntervals milestone_intervals) {
        this.milestone_intervals = milestone_intervals;
    }

    /**
     * Merge from loaded config: file values (non-null) override this. Used when merging file over Java defaults.
     */
    public void mergeFrom(LevelSystemMainConfig from) {
        if (from == null) return;
        if (from.mod_info != null) mod_info.mergeFrom(from.mod_info);
        if (from.global_settings != null) global_settings.mergeFrom(from.global_settings);
        if (from.milestone_intervals != null) milestone_intervals.mergeFrom(from.milestone_intervals);
    }

    public static class ModInfo {
        private String name = "Seyon Level System";
        private String version = "1.0.0";
        private String author = "Christian Wielath";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void mergeFrom(ModInfo from) {
            if (from == null) return;
            if (from.name != null) this.name = from.name;
            if (from.version != null) this.version = from.version;
            if (from.author != null) this.author = from.author;
        }
    }

    public static class GlobalSettings {
        private int max_level = 100;
        private boolean exp_overflow_enabled = false;
        private int skill_points_per_level = 1;
        private boolean allow_skill_respec = true;
        private String respec_cost_type = "items";

        public int getMaxLevel() {
            return max_level;
        }

        public void setMaxLevel(int max_level) {
            this.max_level = max_level;
        }

        public boolean isExpOverflowEnabled() {
            return exp_overflow_enabled;
        }

        public void setExpOverflowEnabled(boolean exp_overflow_enabled) {
            this.exp_overflow_enabled = exp_overflow_enabled;
        }

        public int getSkillPointsPerLevel() {
            return skill_points_per_level;
        }

        public void setSkillPointsPerLevel(int skill_points_per_level) {
            this.skill_points_per_level = skill_points_per_level;
        }

        public boolean isAllowSkillRespec() {
            return allow_skill_respec;
        }

        public void setAllowSkillRespec(boolean allow_skill_respec) {
            this.allow_skill_respec = allow_skill_respec;
        }

        public String getRespecCostType() {
            return respec_cost_type;
        }

        public void setRespecCostType(String respec_cost_type) {
            this.respec_cost_type = respec_cost_type;
        }

        public void mergeFrom(GlobalSettings from) {
            if (from == null) return;
            this.max_level = from.max_level;
            this.exp_overflow_enabled = from.exp_overflow_enabled;
            this.skill_points_per_level = from.skill_points_per_level;
            this.allow_skill_respec = from.allow_skill_respec;
            if (from.respec_cost_type != null) this.respec_cost_type = from.respec_cost_type;
        }
    }

    public static class MilestoneIntervals {
        private int quest_every_n_levels = 10;
        private List<Integer> levels_requiring_quest = new ArrayList<>(List.of(10, 20, 30, 40, 50));

        public int getQuestEveryNLevels() {
            return quest_every_n_levels;
        }

        public void setQuestEveryNLevels(int quest_every_n_levels) {
            this.quest_every_n_levels = quest_every_n_levels;
        }

        public List<Integer> getLevelsRequiringQuest() {
            return levels_requiring_quest;
        }

        public void setLevelsRequiringQuest(List<Integer> levels_requiring_quest) {
            this.levels_requiring_quest = levels_requiring_quest;
        }

        public void mergeFrom(MilestoneIntervals from) {
            if (from == null) return;
            this.quest_every_n_levels = from.quest_every_n_levels;
            if (from.levels_requiring_quest != null && !from.levels_requiring_quest.isEmpty()) {
                this.levels_requiring_quest = new ArrayList<>(from.levels_requiring_quest);
            }
        }
    }
}
