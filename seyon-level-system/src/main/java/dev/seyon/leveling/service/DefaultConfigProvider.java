package dev.seyon.leveling.service;

import dev.seyon.leveling.config.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides default configuration as Java objects. Used by LevelSystemConfigService to merge with
 * file configs (file has higher priority); merged result is saved back.
 */
public class DefaultConfigProvider {

    public DefaultConfigProvider() {}

    public LevelSystemMainConfig getDefaultMainConfig() {
        return new LevelSystemMainConfig();
    }

    public List<LevelSystemCategory> getDefaultCategories() {
        List<LevelSystemCategory> list = new ArrayList<>();
        list.add(buildMiningCategory());
        list.add(buildWoodcuttingCategory());
        list.add(buildCombatMeleeCategory());
        list.add(buildCombatRangedCategory());
        list.add(buildExplorationCategory());
        list.add(buildFarmingCategory());
        list.add(buildCraftingCategory());
        return list;
    }

    public List<ActionConfig> getDefaultActionConfigs() {
        List<ActionConfig> list = new ArrayList<>();
        list.add(buildMiningActions());
        list.add(buildWoodcuttingActions());
        list.add(buildCombatMeleeActions());
        list.add(buildCombatRangedActions());
        list.add(buildExplorationActions());
        list.add(buildFarmingActions());
        list.add(buildCraftingActions());
        return list;
    }

    public FarmingHarvestOverrideConfig getDefaultFarmingHarvestOverrides() {
        return new FarmingHarvestOverrideConfig();
    }

    // ---------- Mining ----------
    private LevelSystemCategory buildMiningCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("mining");
        category.setDisplayName("Miner");
        category.setDescription("Mine ores and gather resources from the depths");
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
        skill1.setName("Efficient Mining");
        skill1.setDescription("Increases ore mining speed by 2% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("mining_speed_ores", 0.02);
        category.getSkills().add(skill1);
        
        SkillConfig skill2 = new SkillConfig();
        skill2.setId("fortune_miner");
        skill2.setTier(2);
        skill2.setName("Fortunate Miner");
        skill2.setDescription("5% chance for double ores");
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
        return category;
    }

    private ActionConfig buildMiningActions() {
        // Ore block IDs from https://www.hytaleitemids.com/api/items?category=Blocks.Ores
        // difficulty_factor: reflects Hytale ore rarity and "hardness" (slower to mine = higher factor).
        // No column counting; formula: exp × difficulty_factor per block.
        // Rarity: Common (Iron, Copper) → Rare (Cobalt) → Very Rare (Adamantite). Mithril/Onyxium not in Survival.
        ActionConfig actions = new ActionConfig();
        actions.setCategory("mining");
        addMiningAction(actions, "Stone", 1.0, 1.0);

        // Common: Iron, Copper — exp 2.0, factor 1.0
        addMiningAction(actions, "Ore_Iron", 2.0, 1.0);
        addMiningAction(actions, "Ore_Iron_Basalt", 2.0, 1.0);
        addMiningAction(actions, "Ore_Iron_Sandstone", 2.0, 1.0);
        addMiningAction(actions, "Ore_Iron_Shale", 2.0, 1.0);
        addMiningAction(actions, "Ore_Iron_Slate", 2.0, 1.0);
        addMiningAction(actions, "Ore_Iron_Stone", 2.0, 1.0);
        addMiningAction(actions, "Ore_Iron_Volcanic", 2.0, 1.0);
        addMiningAction(actions, "Ore_Copper", 2.0, 1.0);
        addMiningAction(actions, "Ore_Copper_Basalt", 2.0, 1.0);
        addMiningAction(actions, "Ore_Copper_Sandstone", 2.0, 1.0);
        addMiningAction(actions, "Ore_Copper_Shale", 2.0, 1.0);
        addMiningAction(actions, "Ore_Copper_Stone", 2.0, 1.0);
        addMiningAction(actions, "Ore_Copper_Volcanic", 2.0, 1.0);

        // Slightly rarer: Gold — exp 6.0, factor 1.2
        addMiningAction(actions, "Ore_Gold", 6.0, 1.2);
        addMiningAction(actions, "Ore_Gold_Basalt", 6.0, 1.2);
        addMiningAction(actions, "Ore_Gold_Sandstone", 6.0, 1.2);
        addMiningAction(actions, "Ore_Gold_Shale", 6.0, 1.2);
        addMiningAction(actions, "Ore_Gold_Stone", 6.0, 1.2);
        addMiningAction(actions, "Ore_Gold_Volcanic", 6.0, 1.2);

        // Medium: Silver, Thorium — exp 8.0, factor 1.3
        addMiningAction(actions, "Ore_Silver", 8.0, 1.3);
        addMiningAction(actions, "Ore_Silver_Basalt", 8.0, 1.3);
        addMiningAction(actions, "Ore_Silver_Sandstone", 8.0, 1.3);
        addMiningAction(actions, "Ore_Silver_Shale", 8.0, 1.3);
        addMiningAction(actions, "Ore_Silver_Slate", 8.0, 1.3);
        addMiningAction(actions, "Ore_Silver_Stone", 8.0, 1.3);
        addMiningAction(actions, "Ore_Silver_Volcanic", 8.0, 1.3);
        addMiningAction(actions, "Ore_Thorium", 8.0, 1.3);
        addMiningAction(actions, "Ore_Thorium_Basalt", 8.0, 1.3);
        addMiningAction(actions, "Ore_Thorium_Sandstone", 8.0, 1.3);
        addMiningAction(actions, "Ore_Thorium_Shale", 8.0, 1.3);
        addMiningAction(actions, "Ore_Thorium_Stone", 8.0, 1.3);
        addMiningAction(actions, "Ore_Thorium_Volcanic", 8.0, 1.3);

        // Rare: Cobalt (Zone 3) — exp 18.0, factor 1.7
        addMiningAction(actions, "Ore_Cobalt", 18.0, 1.7);
        addMiningAction(actions, "Ore_Cobalt_Basalt", 18.0, 1.7);
        addMiningAction(actions, "Ore_Cobalt_Sandstone", 18.0, 1.7);
        addMiningAction(actions, "Ore_Cobalt_Shale", 18.0, 1.7);
        addMiningAction(actions, "Ore_Cobalt_Slate", 18.0, 1.7);
        addMiningAction(actions, "Ore_Cobalt_Stone", 18.0, 1.7);
        addMiningAction(actions, "Ore_Cobalt_Volcanic", 18.0, 1.7);

        // Very rare / Endgame: Adamantite (Zone 4, volcanic) — exp 40.0, factor 2.2
        addMiningAction(actions, "Ore_Adamantite", 40.0, 2.2);
        addMiningAction(actions, "Ore_Adamantite_Basalt", 40.0, 2.2);
        addMiningAction(actions, "Ore_Adamantite_Shale", 40.0, 2.2);
        addMiningAction(actions, "Ore_Adamantite_Slate", 40.0, 2.2);
        addMiningAction(actions, "Ore_Adamantite_Stone", 40.0, 2.2);
        addMiningAction(actions, "Ore_Adamantite_Volcanic", 40.0, 2.2);

        // Special: Prisma, Emerald
        addMiningAction(actions, "Ore_Prisma", 25.0, 1.8);
        addMiningAction(actions, "Rock_Gem_Emerald", 30.0, 2.0);

        // Not in Survival (included for future use): Mithril, Onyxium — exp 50.0, factor 2.5
        addMiningAction(actions, "Ore_Mithril", 50.0, 2.5);
        addMiningAction(actions, "Ore_Mithril_Stone", 50.0, 2.5);
        addMiningAction(actions, "Ore_Onyxium", 50.0, 2.5);
        addMiningAction(actions, "Ore_Onyxium_Basalt", 50.0, 2.5);
        addMiningAction(actions, "Ore_Onyxium_Sandstone", 50.0, 2.5);
        addMiningAction(actions, "Ore_Onyxium_Shale", 50.0, 2.5);
        addMiningAction(actions, "Ore_Onyxium_Stone", 50.0, 2.5);
        addMiningAction(actions, "Ore_Onyxium_Volcanic", 50.0, 2.5);
        return actions;
    }

    private void addMiningAction(ActionConfig actions, String blockId, double exp, double difficultyFactor) {
        ActionConfig.ActionMapping m = new ActionConfig.ActionMapping();
        m.setActionId("break_" + blockId);
        m.setExp(exp);
        m.setDifficultyFactor(difficultyFactor);
        actions.getActions().add(m);
    }

    // ---------- Woodcutting ----------
    private LevelSystemCategory buildWoodcuttingCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("woodcutting");
        category.setDisplayName("Woodcutter");
        category.setDescription("Chop trees and gather wood resources");
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
        skill1.setName("Tree Feller");
        skill1.setDescription("Increases wood cutting speed by 3% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("woodcutting_speed", 0.03);
        category.getSkills().add(skill1);
        return category;
    }

    private ActionConfig buildWoodcuttingActions() {
        // Log block IDs from https://www.hytaleitemids.com/api/items?search=log
        // difficulty_factor: 1.0=normal, 1.5=special (Fire, Ice, Petrified), 2.0=rare (Crystal)
        ActionConfig actions = new ActionConfig();
        actions.setCategory("woodcutting");
        double baseExp = 5.0;
        addWoodAction(actions, "Wood_Amber_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Ash_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Aspen_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Azure_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Bamboo_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Bamboo_Trunk_Deco", baseExp, 1.0);
        addWoodAction(actions, "Wood_Banyan_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Beech_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Birch_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Bottletree_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Burnt_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Camphor_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Cedar_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Crystal_Trunk", baseExp, 2.0);
        addWoodAction(actions, "Wood_Dry_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Fig_Blue_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Fire_Trunk", baseExp, 1.5);
        addWoodAction(actions, "Wood_Fir_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Gumboab_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Ice_Trunk", baseExp, 1.5);
        addWoodAction(actions, "Wood_Jungle_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Maple_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Oak_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Palm_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Palo_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Petrified_Trunk", baseExp, 1.5);
        addWoodAction(actions, "Wood_Poisoned_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Redwood_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Sallow_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Spiral_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Stormbark_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Stripped_Deco", baseExp, 1.0);
        addWoodAction(actions, "Wood_Windwillow_Trunk", baseExp, 1.0);
        addWoodAction(actions, "Wood_Wisteria_Wild_Trunk", baseExp, 1.0);
        return actions;
    }

    private void addWoodAction(ActionConfig actions, String blockId, double exp, double difficultyFactor) {
        ActionConfig.ActionMapping m = new ActionConfig.ActionMapping();
        m.setActionId("break_" + blockId);
        m.setExp(exp);
        m.setDifficultyFactor(difficultyFactor);
        actions.getActions().add(m);
    }

    // ---------- Combat Melee ----------
    private LevelSystemCategory buildCombatMeleeCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("combat_melee");
        category.setDisplayName("Melee Fighter");
        category.setDescription("Master close combat with sword and axe");
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
        skill1.setName("Sword Master");
        skill1.setDescription("Increases sword damage by 5% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("sword_damage", 0.05);
        category.getSkills().add(skill1);
        
        SkillConfig skill2 = new SkillConfig();
        skill2.setId("critical_strike");
        skill2.setTier(2);
        skill2.setName("Critical Strike");
        skill2.setDescription("10% chance for critical damage");
        skill2.setCost(2);
        skill2.setMaxPoints(3);
        skill2.getEffects().put("crit_chance", 0.10);
        category.getSkills().add(skill2);
        return category;
    }

    private ActionConfig buildCombatMeleeActions() {
        ActionConfig actions = new ActionConfig();
        actions.setCategory("combat_melee");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("kill_enemy_melee");
        map1.setExp(20.0);
        actions.getActions().add(map1);
        return actions;
    }

    // ---------- Combat Ranged ----------
    private LevelSystemCategory buildCombatRangedCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("combat_ranged");
        category.setDisplayName("Ranged Fighter");
        category.setDescription("Master ranged combat with bow and crossbow");
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
        skill1.setName("Bow Master");
        skill1.setDescription("Increases bow damage by 5% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("bow_damage", 0.05);
        category.getSkills().add(skill1);
        return category;
    }

    private ActionConfig buildCombatRangedActions() {
        ActionConfig actions = new ActionConfig();
        actions.setCategory("combat_ranged");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("kill_enemy_ranged");
        map1.setExp(25.0);
        actions.getActions().add(map1);
        return actions;
    }

    // ---------- Exploration ----------
    private LevelSystemCategory buildExplorationCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("exploration");
        category.setDisplayName("Explorer");
        category.setDescription("Explore the world and discover new places");
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
        skill1.setName("Pathfinder");
        skill1.setDescription("Increases movement speed by 1% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("movement_speed", 0.01);
        category.getSkills().add(skill1);
        return category;
    }

    private ActionConfig buildExplorationActions() {
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

        ActionConfig.ActionMapping map4 = new ActionConfig.ActionMapping();
        map4.setActionId("discover_instance");
        map4.setExp(50.0);
        actions.getActions().add(map4);
        return actions;
    }

    // ---------- Farming ----------
    private LevelSystemCategory buildFarmingCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("farming");
        category.setDisplayName("Farmer");
        category.setDescription("Grow crops and raise animals");
        category.setIcon("Server/Item/Icons/Farming.png");

        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(90.0);
        expCurve.setMultiplier(1.14);
        category.setExpCurve(expCurve);

        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("farming_speed", 0.01);
            if (i % 5 == 0) {
                bonus.getModifiers().put("max_stamina", 4.0);
            }
            category.getLevelBonuses().add(bonus);
        }

        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("green_thumb");
        skill1.setTier(1);
        skill1.setName("Green Thumb");
        skill1.setDescription("Increases crop growth speed by 2% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("farming_speed", 0.02);
        category.getSkills().add(skill1);

        SkillConfig skill2 = new SkillConfig();
        skill2.setId("fast_harvest");
        skill2.setTier(2);
        skill2.setName("Fast Harvest");
        skill2.setDescription("5% chance for double harvest yield");
        skill2.setCost(2);
        skill2.setMaxPoints(3);
        skill2.getEffects().put("harvest_bonus", 0.05);
        category.getSkills().add(skill2);
        return category;
    }

    private ActionConfig buildFarmingActions() {
        // harvest_<blockId> (UseBlockHarvestExpSystem uses BlockType.getId(); block IDs from
        // https://www.hytaleitemids.com/api/items?search=Plant_Crop_&quality=Common). harvest_crop is fallback.
        // Ordered EXP (minimal steps): Wheat 3.0 -> Potato 5.6. Overrides: config/farming_harvest.json.
        ActionConfig actions = new ActionConfig();
        actions.setCategory("farming");

        addFarmingHarvestAction(actions, "harvest_crop", 5.0);
        addFarmingHarvestAction(actions, "harvest_animal", 15.0);

        // 14 crops in order: slightly more EXP each (farming is easy and done in bulk)
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Wheat_Block", 3.0);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Lettuce_Block", 3.2);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Carrot_Block", 3.4);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Corn_Block", 3.6);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Cauliflower_Block", 3.8);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Turnip_Block", 4.0);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Aubergine_Block", 4.2);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Pumpkin_Block", 4.4);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Tomato_Block", 4.6);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Chilli_Block", 4.8);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Cotton_Block", 5.0);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Rice_Block", 5.2);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Onion_Block", 5.4);
        addFarmingHarvestAction(actions, "harvest_Plant_Crop_Potato_Block", 5.6);

        // All other Plant_Crop_*_Block from API matching the schema (default 4.0); overridable via farming_harvest.json
        java.util.Set<String> ordered = java.util.Set.of(
                "harvest_Plant_Crop_Wheat_Block", "harvest_Plant_Crop_Lettuce_Block", "harvest_Plant_Crop_Carrot_Block",
                "harvest_Plant_Crop_Corn_Block", "harvest_Plant_Crop_Cauliflower_Block", "harvest_Plant_Crop_Turnip_Block",
                "harvest_Plant_Crop_Aubergine_Block", "harvest_Plant_Crop_Pumpkin_Block", "harvest_Plant_Crop_Tomato_Block",
                "harvest_Plant_Crop_Chilli_Block", "harvest_Plant_Crop_Cotton_Block", "harvest_Plant_Crop_Rice_Block",
                "harvest_Plant_Crop_Onion_Block", "harvest_Plant_Crop_Potato_Block");
        double defaultExp = 4.0;
        for (String actionId : FARMING_HARVEST_BLOCK_IDS) {
            if (ordered.contains(actionId)) continue;
            addFarmingHarvestAction(actions, actionId, defaultExp);
        }
        return actions;
    }

    private void addFarmingHarvestAction(ActionConfig actions, String actionId, double exp) {
        ActionConfig.ActionMapping m = new ActionConfig.ActionMapping();
        m.setActionId(actionId);
        m.setExp(exp);
        actions.getActions().add(m);
    }

    /** All harvest_&lt;blockId&gt; for Plant_Crop_*_Block from hytaleitemids.com (Plant_Crop_ search). Excludes _Item, _Block_Wall, _Branch. */
    private static final String[] FARMING_HARVEST_BLOCK_IDS = {
            "harvest_Plant_Crop_Apple_Block",
            "harvest_Plant_Crop_Aubergine_Block",
            "harvest_Plant_Crop_Berry_Block",
            "harvest_Plant_Crop_Berry_Wet_Block",
            "harvest_Plant_Crop_Berry_Winter_Block",
            "harvest_Plant_Crop_Carrot_Block",
            "harvest_Plant_Crop_Cauliflower_Block",
            "harvest_Plant_Crop_Chilli_Block",
            "harvest_Plant_Crop_Corn_Block",
            "harvest_Plant_Crop_Cotton_Block",
            "harvest_Plant_Crop_Health1_Block",
            "harvest_Plant_Crop_Health2_Block",
            "harvest_Plant_Crop_Health3_Block",
            "harvest_Plant_Crop_Lettuce_Block",
            "harvest_Plant_Crop_Mana1_Block",
            "harvest_Plant_Crop_Mana2_Block",
            "harvest_Plant_Crop_Mana3_Block",
            "harvest_Plant_Crop_Mushroom_Block",
            "harvest_Plant_Crop_Mushroom_Block_Blue",
            "harvest_Plant_Crop_Mushroom_Block_Blue_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_Blue_Trunk",
            "harvest_Plant_Crop_Mushroom_Block_Brown",
            "harvest_Plant_Crop_Mushroom_Block_Brown_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_Brown_Trunk",
            "harvest_Plant_Crop_Mushroom_Block_Green",
            "harvest_Plant_Crop_Mushroom_Block_Green_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_Green_Trunk",
            "harvest_Plant_Crop_Mushroom_Block_Purple",
            "harvest_Plant_Crop_Mushroom_Block_Purple_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_Purple_Trunk",
            "harvest_Plant_Crop_Mushroom_Block_Red",
            "harvest_Plant_Crop_Mushroom_Block_Red_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_Red_Trunk",
            "harvest_Plant_Crop_Mushroom_Block_White",
            "harvest_Plant_Crop_Mushroom_Block_White_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_White_Trunk",
            "harvest_Plant_Crop_Mushroom_Block_Yellow",
            "harvest_Plant_Crop_Mushroom_Block_Yellow_Mycelium",
            "harvest_Plant_Crop_Mushroom_Block_Yellow_Trunk",
            "harvest_Plant_Crop_Mushroom_Boomshroom_Large",
            "harvest_Plant_Crop_Mushroom_Boomshroom_Small",
            "harvest_Plant_Crop_Mushroom_Cap_Brown",
            "harvest_Plant_Crop_Mushroom_Cap_Green",
            "harvest_Plant_Crop_Mushroom_Cap_Poison",
            "harvest_Plant_Crop_Mushroom_Cap_Red",
            "harvest_Plant_Crop_Mushroom_Cap_White",
            "harvest_Plant_Crop_Mushroom_Common_Blue",
            "harvest_Plant_Crop_Mushroom_Common_Brown",
            "harvest_Plant_Crop_Mushroom_Common_Lime",
            "harvest_Plant_Crop_Mushroom_Flatcap_Blue",
            "harvest_Plant_Crop_Mushroom_Flatcap_Green",
            "harvest_Plant_Crop_Mushroom_Glowing_Blue",
            "harvest_Plant_Crop_Mushroom_Glowing_Green",
            "harvest_Plant_Crop_Mushroom_Glowing_Orange",
            "harvest_Plant_Crop_Mushroom_Glowing_Purple",
            "harvest_Plant_Crop_Mushroom_Glowing_Red",
            "harvest_Plant_Crop_Mushroom_Glowing_Violet",
            "harvest_Plant_Crop_Mushroom_Shelve_Brown",
            "harvest_Plant_Crop_Mushroom_Shelve_Green",
            "harvest_Plant_Crop_Mushroom_Shelve_Yellow",
            "harvest_Plant_Crop_Onion_Block",
            "harvest_Plant_Crop_Potato_Block",
            "harvest_Plant_Crop_Pumpkin_Block",
            "harvest_Plant_Crop_Rice_Block",
            "harvest_Plant_Crop_Stamina1_Block",
            "harvest_Plant_Crop_Stamina2_Block",
            "harvest_Plant_Crop_Stamina3_Block",
            "harvest_Plant_Crop_Tomato_Block",
            "harvest_Plant_Crop_Turnip_Block",
            "harvest_Plant_Crop_Wheat_Block",
            "harvest_Plant_Crop_Wheat_Stage_4_Burnt"
    };

    // ---------- Crafting ----------
    private LevelSystemCategory buildCraftingCategory() {
        LevelSystemCategory category = new LevelSystemCategory();
        category.setId("crafting");
        category.setDisplayName("Crafter");
        category.setDescription("Craft items and refine materials");
        category.setIcon("Server/Item/Icons/Crafting.png");

        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(80.0);
        expCurve.setMultiplier(1.12);
        category.setExpCurve(expCurve);

        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("crafting_speed", 0.01);
            if (i % 10 == 0) {
                bonus.getModifiers().put("craft_bonus_output", 0.01);
            }
            category.getLevelBonuses().add(bonus);
        }

        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("efficient_craft");
        skill1.setTier(1);
        skill1.setName("Efficient Craft");
        skill1.setDescription("Increases crafting speed by 2% per point");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("crafting_speed", 0.02);
        category.getSkills().add(skill1);

        SkillConfig skill2 = new SkillConfig();
        skill2.setId("master_crafter");
        skill2.setTier(2);
        skill2.setName("Master Crafter");
        skill2.setDescription("3% chance for bonus craft output");
        skill2.setCost(2);
        skill2.setMaxPoints(3);
        skill2.getEffects().put("craft_bonus_output", 0.03);
        category.getSkills().add(skill2);
        return category;
    }

    private ActionConfig buildCraftingActions() {
        ActionConfig actions = new ActionConfig();
        actions.setCategory("crafting");
        ActionConfig.ActionMapping map1 = new ActionConfig.ActionMapping();
        map1.setActionId("craft_item");
        map1.setExp(3.0);
        actions.getActions().add(map1);
        return actions;
    }
}
