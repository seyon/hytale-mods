package dev.seyon.leveling.model;

import java.util.*;

/**
 * Player-specific Level System data
 * This is stored per player and persisted
 */
public class PlayerLevelSystemData {
    private UUID playerId;
    private Map<String, CategoryProgress> categoryProgress;
    private Map<String, Integer> availableSkillPoints;
    private Map<String, Map<String, Integer>> activeSkills; // categoryId -> (skillId -> points)
    private Map<String, Set<Integer>> completedQuests; // categoryId -> Set of completed quest levels

    public PlayerLevelSystemData() {
        this.categoryProgress = new HashMap<>();
        this.availableSkillPoints = new HashMap<>();
        this.activeSkills = new HashMap<>();
        this.completedQuests = new HashMap<>();
    }

    public PlayerLevelSystemData(UUID playerId) {
        this();
        this.playerId = playerId;
    }

    // Getters and Setters
    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public Map<String, CategoryProgress> getCategoryProgress() {
        return categoryProgress;
    }

    public void setCategoryProgress(Map<String, CategoryProgress> categoryProgress) {
        this.categoryProgress = categoryProgress;
    }

    public Map<String, Integer> getAvailableSkillPoints() {
        return availableSkillPoints;
    }

    public void setAvailableSkillPoints(Map<String, Integer> availableSkillPoints) {
        this.availableSkillPoints = availableSkillPoints;
    }

    public Map<String, Map<String, Integer>> getActiveSkills() {
        return activeSkills;
    }

    public void setActiveSkills(Map<String, Map<String, Integer>> activeSkills) {
        this.activeSkills = activeSkills;
    }

    public Map<String, Set<Integer>> getCompletedQuests() {
        return completedQuests;
    }

    public void setCompletedQuests(Map<String, Set<Integer>> completedQuests) {
        this.completedQuests = completedQuests;
    }

    // Helper methods
    
    /**
     * Get or create category progress
     */
    public CategoryProgress getOrCreateCategoryProgress(String categoryId) {
        return categoryProgress.computeIfAbsent(categoryId, id -> new CategoryProgress(id));
    }

    /**
     * Get available skill points for a category
     */
    public int getAvailableSkillPoints(String categoryId) {
        return availableSkillPoints.getOrDefault(categoryId, 0);
    }

    /**
     * Add skill points to a category
     */
    public void addSkillPoints(String categoryId, int points) {
        availableSkillPoints.put(categoryId, getAvailableSkillPoints(categoryId) + points);
    }

    /**
     * Spend skill points from a category
     */
    public boolean spendSkillPoints(String categoryId, int points) {
        int available = getAvailableSkillPoints(categoryId);
        if (available >= points) {
            availableSkillPoints.put(categoryId, available - points);
            return true;
        }
        return false;
    }

    /**
     * Get skill level for a specific skill
     */
    public int getSkillLevel(String categoryId, String skillId) {
        Map<String, Integer> categorySkills = activeSkills.get(categoryId);
        if (categorySkills == null) {
            return 0;
        }
        return categorySkills.getOrDefault(skillId, 0);
    }

    /**
     * Set skill level
     */
    public void setSkillLevel(String categoryId, String skillId, int level) {
        Map<String, Integer> categorySkills = activeSkills.computeIfAbsent(categoryId, k -> new HashMap<>());
        if (level > 0) {
            categorySkills.put(skillId, level);
        } else {
            categorySkills.remove(skillId);
        }
    }

    /**
     * Check if a quest is completed
     */
    public boolean isQuestCompleted(String categoryId, int level) {
        Set<Integer> quests = completedQuests.get(categoryId);
        return quests != null && quests.contains(level);
    }

    /**
     * Mark a quest as completed
     */
    public void completeQuest(String categoryId, int level) {
        Set<Integer> quests = completedQuests.computeIfAbsent(categoryId, k -> new HashSet<>());
        quests.add(level);
    }

    /**
     * Initialize category with default values
     */
    public void initializeCategory(String categoryId) {
        if (!categoryProgress.containsKey(categoryId)) {
            categoryProgress.put(categoryId, new CategoryProgress(categoryId));
            availableSkillPoints.put(categoryId, 0);
            activeSkills.put(categoryId, new HashMap<>());
            completedQuests.put(categoryId, new HashSet<>());
        }
    }
}
