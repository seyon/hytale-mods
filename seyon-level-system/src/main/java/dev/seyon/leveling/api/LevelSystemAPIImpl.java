package dev.seyon.leveling.api;

import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.service.*;

import java.util.UUID;

/**
 * Implementation of the Level System API
 */
public class LevelSystemAPIImpl implements LevelSystemAPI {

    private final CategoryService categoryService;
    private final ActionRegistryService actionRegistryService;
    private final ExperienceService experienceService;
    private final LevelSystemDataService dataService;
    private final ModifierService modifierService;

    public LevelSystemAPIImpl(CategoryService categoryService,
                          ActionRegistryService actionRegistryService,
                          ExperienceService experienceService,
                          LevelSystemDataService dataService,
                          ModifierService modifierService) {
        this.categoryService = categoryService;
        this.actionRegistryService = actionRegistryService;
        this.experienceService = experienceService;
        this.dataService = dataService;
        this.modifierService = modifierService;
    }

    @Override
    public void registerCategory(LevelSystemCategory category) {
        categoryService.registerCategory(category);
    }

    @Override
    public void registerAction(String actionId, String categoryId, double exp) {
        actionRegistryService.registerAction(actionId, categoryId, exp);
    }

    @Override
    public void grantExperience(UUID playerId, String categoryId, double amount) {
        experienceService.grantExp(playerId, categoryId, amount, null);
    }

    @Override
    public int getPlayerLevel(UUID playerId, String categoryId) {
        return experienceService.getPlayerLevel(playerId, categoryId);
    }

    @Override
    public boolean hasSkill(UUID playerId, String categoryId, String skillId) {
        return dataService.getPlayerData(playerId).getSkillLevel(categoryId, skillId) > 0;
    }

    @Override
    public double getModifierValue(UUID playerId, String modifierId) {
        return modifierService.getModifierValue(playerId, modifierId);
    }

    @Override
    public boolean hasCategory(String categoryId) {
        return categoryService.hasCategory(categoryId);
    }
}
