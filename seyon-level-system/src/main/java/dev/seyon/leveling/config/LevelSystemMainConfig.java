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
    }
}
