package dev.seyon.leveling.model;

/**
 * Progress data for a single category
 */
public class CategoryProgress {
    private String categoryId;
    private int currentLevel;
    private double currentExp;
    private double expForNextLevel;
    private boolean canGainExp;
    private int pendingLevelUps;

    public CategoryProgress() {
        this.currentLevel = 1;
        this.currentExp = 0;
        this.expForNextLevel = 100; // Default, will be recalculated
        this.canGainExp = true;
        this.pendingLevelUps = 0;
    }

    public CategoryProgress(String categoryId) {
        this();
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public double getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(double currentExp) {
        this.currentExp = currentExp;
    }

    public double getExpForNextLevel() {
        return expForNextLevel;
    }

    public void setExpForNextLevel(double expForNextLevel) {
        this.expForNextLevel = expForNextLevel;
    }

    public boolean isCanGainExp() {
        return canGainExp;
    }

    public void setCanGainExp(boolean canGainExp) {
        this.canGainExp = canGainExp;
    }

    public int getPendingLevelUps() {
        return pendingLevelUps;
    }

    public void setPendingLevelUps(int pendingLevelUps) {
        this.pendingLevelUps = pendingLevelUps;
    }

    /**
     * Add EXP and check if level up is needed
     * @param amount EXP to add
     * @return true if level up is needed
     */
    public boolean addExp(double amount) {
        if (!canGainExp) {
            return false;
        }
        
        this.currentExp += amount;
        
        if (this.currentExp >= this.expForNextLevel) {
            this.pendingLevelUps++;
            this.canGainExp = false; // Prevent further EXP gain until level up is processed
            return true;
        }
        
        return false;
    }

    /**
     * Process a level up
     */
    public void levelUp() {
        if (this.pendingLevelUps > 0) {
            this.currentLevel++;
            this.currentExp = 0;
            this.pendingLevelUps--;
            
            if (this.pendingLevelUps == 0) {
                this.canGainExp = true;
            }
        }
    }
}
