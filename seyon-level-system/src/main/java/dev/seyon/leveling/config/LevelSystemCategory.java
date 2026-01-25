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
    /** Item ID for the EXP gain notification icon (e.g. "Item_Material_Ingot_Iron"). Overridable in JSON. */
    private String notification_icon;
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

    public String getNotificationIcon() {
        return notification_icon;
    }

    public void setNotificationIcon(String notification_icon) {
        this.notification_icon = notification_icon;
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

    /**
     * Merge from loaded: file (non-null) overrides. Lists/maps merged by key (level, id).
     */
    public void mergeFrom(LevelSystemCategory from) {
        if (from == null) return;
        if (from.id != null) this.id = from.id;
        if (from.display_name != null) this.display_name = from.display_name;
        if (from.description != null) this.description = from.description;
        if (from.icon != null) this.icon = from.icon;
        if (from.notification_icon != null) this.notification_icon = from.notification_icon;
        if (from.exp_curve != null) this.exp_curve.mergeFrom(from.exp_curve);
        if (from.level_bonuses != null && !from.level_bonuses.isEmpty()) {
            java.util.Map<Integer, LevelBonusConfig> byLevel = new java.util.HashMap<>();
            for (LevelBonusConfig b : this.level_bonuses) byLevel.put(b.getLevel(), b);
            for (LevelBonusConfig b : from.level_bonuses) {
                LevelBonusConfig cur = byLevel.get(b.getLevel());
                if (cur != null) cur.mergeFrom(b); else byLevel.put(b.getLevel(), b);
            }
            this.level_bonuses = new ArrayList<>(byLevel.values());
            this.level_bonuses.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
        }
        if (from.skills != null && !from.skills.isEmpty()) {
            java.util.Map<String, SkillConfig> byId = new java.util.HashMap<>();
            for (SkillConfig s : this.skills) if (s.getId() != null) byId.put(s.getId(), s);
            for (SkillConfig s : from.skills) {
                SkillConfig cur = s.getId() != null ? byId.get(s.getId()) : null;
                if (cur != null) cur.mergeFrom(s); else if (s.getId() != null) byId.put(s.getId(), s);
            }
            this.skills = new ArrayList<>(byId.values());
        }
        if (from.milestones != null && !from.milestones.isEmpty()) {
            for (java.util.Map.Entry<Integer, MilestoneQuestConfig> e : from.milestones.entrySet()) {
                MilestoneQuestConfig cur = this.milestones.get(e.getKey());
                if (cur != null && e.getValue() != null) cur.mergeFrom(e.getValue());
                else if (e.getValue() != null) this.milestones.put(e.getKey(), e.getValue());
            }
        }
    }
}
