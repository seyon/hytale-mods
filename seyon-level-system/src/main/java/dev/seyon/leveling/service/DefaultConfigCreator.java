package dev.seyon.leveling.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.seyon.leveling.config.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper service to create default configuration files
 */
public class DefaultConfigCreator {

    private final Gson gson;

    public DefaultConfigCreator() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Create all default config files
     */
    public void createDefaultConfigs(File configRoot) {
        File categoriesDir = new File(configRoot, "categories");
        File actionsDir = new File(configRoot, "actions");
        
        categoriesDir.mkdirs();
        actionsDir.mkdirs();
        
        // Create categories
        createMiningCategory(categoriesDir, actionsDir);
        createWoodcuttingCategory(categoriesDir, actionsDir);
        createCombatMeleeCategory(categoriesDir, actionsDir);
        createCombatRangedCategory(categoriesDir, actionsDir);
        createExplorationCategory(categoriesDir, actionsDir);
    }

    /**
     * Create Mining category
     */
    private void createMiningCategory(File categoriesDir, File actionsDir) {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("mining");
        category.setDisplayName("Minenarbeiter");
        category.setDescription("Grabe Erze und sammle Ressourcen aus der Tiefe");
        category.setIcon("Server/Item/Icons/Mining.png");
        
        // EXP Curve
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(100.0);
        expCurve.setMultiplier(1.15);
        category.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            if (i % 5 == 0) {
                bonus.getModifiers().put("max_health", 2.0);
            }
            bonus.getModifiers().put("mining_speed", 0.01);
            category.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("efficient_mining");
        skill1.setTier(1);
        skill1.setName("Effizientes Abbauen");
        skill1.setDescription("Erhöht Abbaugeschwindigkeit von Erzen um 2% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("mining_speed_ores", 0.02);
        category.getSkills().add(skill1);
        
        SkillConfig skill2 = new SkillConfig();
        skill2.setId("fortune_miner");
        skill2.setTier(2);
        skill2.setName("Glücklicher Bergmann");
        skill2.setDescription("5% Chance auf doppelte Erze");
        skill2.setCost(2);
        skill2.setMaxPoints(3);
        skill2.getEffects().put("mining_fortune", 0.05);
        category.getSkills().add(skill2);
        
        // Milestones
        MilestoneQuestConfig quest10 = new MilestoneQuestConfig();
        quest10.setType("simple_talk");
        quest10.setNpcId("mining_master");
        quest10.setDialogKey("mining_milestone_10");
        category.getMilestones().put(10, quest10);
        
        MilestoneQuestConfig quest20 = new MilestoneQuestConfig();
        quest20.setType("item_collection");
        quest20.setNpcId("mining_master");
        quest20.setDialogKey("mining_milestone_20");
        QuestItemRequirement req1 = new QuestItemRequirement();
        req1.setItemId("iron_ore");
        req1.setAmount(50);
        quest20.getRequiredItems().add(req1);
        category.getMilestones().put(20, quest20);
        
        // Save category
        saveConfig(new File(categoriesDir, "mining.json"), category);
        
        // Create actions
        ActionConfig actions = new ActionConfig();
        actions.setCategory("mining");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("break_stone");
        map1.setExp(1.0);
        actions.getActions().add(map1);
        
        ActionConfig.ActionMapping map2 = new ActionConfig.ActionMapping();
        map2.setActionId("break_iron_ore");
        map2.setExp(10.0);
        actions.getActions().add(map2);
        
        ActionConfig.ActionMapping map3 = new ActionConfig.ActionMapping();
        map3.setActionId("break_diamond_ore");
        map3.setExp(50.0);
        actions.getActions().add(map3);
        
        saveConfig(new File(actionsDir, "mining.json"), actions);
    }

    /**
     * Create Woodcutting category
     */
    private void createWoodcuttingCategory(File categoriesDir, File actionsDir) {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("woodcutting");
        category.setDisplayName("Holzfäller");
        category.setDescription("Fälle Bäume und sammle Holzressourcen");
        category.setIcon("Server/Item/Icons/Woodcutting.png");
        
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(80.0);
        expCurve.setMultiplier(1.12);
        category.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("woodcutting_speed", 0.01);
            if (i % 10 == 0) {
                bonus.getModifiers().put("max_stamina", 5.0);
            }
            category.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("tree_feller");
        skill1.setTier(1);
        skill1.setName("Baumfäller");
        skill1.setDescription("Erhöht Holzabbaugeschwindigkeit um 3% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("woodcutting_speed", 0.03);
        category.getSkills().add(skill1);
        
        saveConfig(new File(categoriesDir, "woodcutting.json"), category);
        
        // Actions
        ActionConfig actions = new ActionConfig();
        actions.setCategory("woodcutting");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("break_oak_log");
        map1.setExp(5.0);
        actions.getActions().add(map1);
        
        saveConfig(new File(actionsDir, "woodcutting.json"), actions);
    }

    /**
     * Create Combat Melee category
     */
    private void createCombatMeleeCategory(File categoriesDir, File actionsDir) {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("combat_melee");
        category.setDisplayName("Nahkämpfer");
        category.setDescription("Meistere den Nahkampf mit Schwert und Axt");
        category.setIcon("Server/Item/Icons/CombatMelee.png");
        
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(120.0);
        expCurve.setMultiplier(1.18);
        category.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("melee_damage", 0.5);
            if (i % 5 == 0) {
                bonus.getModifiers().put("max_health", 5.0);
            }
            category.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("sword_mastery");
        skill1.setTier(1);
        skill1.setName("Schwertmeister");
        skill1.setDescription("Erhöht Schwertschaden um 5% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("sword_damage", 0.05);
        category.getSkills().add(skill1);
        
        SkillConfig skill2 = new SkillConfig();
        skill2.setId("critical_strike");
        skill2.setTier(2);
        skill2.setName("Kritischer Treffer");
        skill2.setDescription("10% Chance auf kritischen Schaden");
        skill2.setCost(2);
        skill2.setMaxPoints(3);
        skill2.getEffects().put("crit_chance", 0.10);
        category.getSkills().add(skill2);
        
        saveConfig(new File(categoriesDir, "combat_melee.json"), category);
        
        // Actions
        ActionConfig actions = new ActionConfig();
        actions.setCategory("combat_melee");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("kill_enemy_melee");
        map1.setExp(20.0);
        actions.getActions().add(map1);
        
        saveConfig(new File(actionsDir, "combat_melee.json"), actions);
    }

    /**
     * Create Combat Ranged category
     */
    private void createCombatRangedCategory(File categoriesDir, File actionsDir) {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("combat_ranged");
        category.setDisplayName("Fernkämpfer");
        category.setDescription("Meistere den Fernkampf mit Bogen und Armbrust");
        category.setIcon("Server/Item/Icons/CombatRanged.png");
        
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(120.0);
        expCurve.setMultiplier(1.18);
        category.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("ranged_damage", 0.5);
            if (i % 10 == 0) {
                bonus.getModifiers().put("accuracy", 0.02);
            }
            category.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("bow_mastery");
        skill1.setTier(1);
        skill1.setName("Bogenmeister");
        skill1.setDescription("Erhöht Bogenschaden um 5% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("bow_damage", 0.05);
        category.getSkills().add(skill1);
        
        saveConfig(new File(categoriesDir, "combat_ranged.json"), category);
        
        // Actions
        ActionConfig actions = new ActionConfig();
        actions.setCategory("combat_ranged");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("kill_enemy_ranged");
        map1.setExp(25.0);
        actions.getActions().add(map1);
        
        saveConfig(new File(actionsDir, "combat_ranged.json"), actions);
    }

    /**
     * Create Exploration category
     */
    private void createExplorationCategory(File categoriesDir, File actionsDir) {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("exploration");
        category.setDisplayName("Entdecker");
        category.setDescription("Erkunde die Welt und entdecke neue Orte");
        category.setIcon("Server/Item/Icons/Exploration.png");
        
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("linear");
        expCurve.setBase(150.0);
        expCurve.setMultiplier(1.0);
        category.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("movement_speed", 0.01);
            if (i % 5 == 0) {
                bonus.getModifiers().put("max_stamina", 3.0);
            }
            category.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("pathfinder");
        skill1.setTier(1);
        skill1.setName("Pfadfinder");
        skill1.setDescription("Erhöht Bewegungsgeschwindigkeit um 1% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("movement_speed", 0.01);
        category.getSkills().add(skill1);
        
        saveConfig(new File(categoriesDir, "exploration.json"), category);
        
        // Actions
        ActionConfig actions = new ActionConfig();
        actions.setCategory("exploration");
        ActionConfig.ActionMapping map0 = new ActionConfig.ActionMapping();
        map0.setActionId("explore_steps");
        map0.setExp(2.0);
        actions.getActions().add(map0);

        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("discover_zone");
        map1.setExp(15.0);
        actions.getActions().add(map1);

        ActionConfig.ActionMapping map2 = new ActionConfig.ActionMapping();
        map2.setActionId("discover_new_chunk");
        map2.setExp(10.0);
        actions.getActions().add(map2);

        ActionConfig.ActionMapping map3 = new ActionConfig.ActionMapping();
        map3.setActionId("discover_new_biome");
        map3.setExp(100.0);
        actions.getActions().add(map3);

        saveConfig(new File(actionsDir, "exploration.json"), actions);
    }

    /**
     * Save config file
     */
    private <T> void saveConfig(File file, T config) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
