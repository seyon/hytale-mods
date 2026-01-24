package dev.seyon.leveling.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Category configuration (loaded from categories/*.json)
 */
public class LevelSystemCategory {
    private String id;
    private String display_name;
    private String description;
    private String icon;
    private ExpCurveConfig exp_curve = new ExpCurveConfig();
    private List<LevelBonusConfig> level_bonuses = new ArrayList<>();
    private List<SkillConfig> skills = new ArrayList<>();
    private Map<Integer, MilestoneQuestConfig> milestones = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return display_name;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ExpCurveConfig getExpCurve() {
        return exp_curve;
    }

    public void setExpCurve(ExpCurveConfig exp_curve) {
        this.exp_curve = exp_curve;
    }

    public List<LevelBonusConfig> getLevelBonuses() {
        return level_bonuses;
    }

    public void setLevelBonuses(List<LevelBonusConfig> level_bonuses) {
        this.level_bonuses = level_bonuses;
    }

    public List<SkillConfig> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillConfig> skills) {
        this.skills = skills;
    }

    public Map<Integer, MilestoneQuestConfig> getMilestones() {
        return milestones;
    }

    public void setMilestones(Map<Integer, MilestoneQuestConfig> milestones) {
        this.milestones = milestones;
    }

    /**
     * Get level bonuses for a specific level
     */
    public LevelBonusConfig getLevelBonus(int level) {
        for (LevelBonusConfig bonus : level_bonuses) {
            if (bonus.getLevel() == level) {
                return bonus;
            }
        }
        return null;
    }

    /**
     * Get milestone quest for a specific level
     */
    public MilestoneQuestConfig getMilestoneQuest(int level) {
        return milestones.get(level);
    }

    /**
     * Check if level requires a quest
     */
    public boolean hasQuestAtLevel(int level) {
        return milestones.containsKey(level);
    }

    /**
     * Get skill by ID
     */
    public SkillConfig getSkill(String skillId) {
        for (SkillConfig skill : skills) {
            if (skill.getId().equals(skillId)) {
                return skill;
            }
        }
        return null;
    }
}
