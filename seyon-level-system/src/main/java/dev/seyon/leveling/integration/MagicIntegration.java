package dev.seyon.leveling.integration;

import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.api.LevelSystemAPI;
import dev.seyon.leveling.config.*;

import java.util.logging.Level;

/**
 * Integration with Seyon Magic mod
 * 
 * Automatically registers a "magic" category if SeyonMagic is installed
 */
public class MagicIntegration {

    /**
     * Check if SeyonMagic is installed and integrate
     */
    public static void integrate() {
        try {
            // Seyon Arcane Arts (seyon-arcane-arts) still uses package dev.seyon.magic
            Class.forName("dev.seyon.magic.SeyonMagicPlugin");
            
            SeyonLevelSystemPlugin.getInstance().getLogger()
                .at(Level.INFO)
                .log("Seyon Arcane Arts detected! Registering magic category...");
            
            registerMagicCategory();
            
        } catch (ClassNotFoundException e) {
            SeyonLevelSystemPlugin.getInstance().getLogger()
                .at(Level.INFO)
                .log("Seyon Arcane Arts not found, skipping magic integration");
        }
    }

    /**
     * Register the magic category
     */
    private static void registerMagicCategory() {
        LevelSystemAPI api = SeyonLevelSystemPlugin.getInstance().getAPI();
        
        // Check if magic category already exists
        if (api.hasCategory("magic")) {
            SeyonLevelSystemPlugin.getInstance().getLogger()
                .at(Level.INFO)
                .log("Magic category already exists, skipping registration");
            return;
        }
        
        LevelSystemCategory magic = new LevelSystemCategory();
        magic.setId("magic");
        magic.setDisplayName("Magie");
        magic.setDescription("Meistere die arkanen Künste und mächtige Zauber");
        magic.setIcon("Server/Item/Icons/Magic.png");
        
        // EXP Curve - higher than combat due to spell complexity
        ExpCurveConfig expCurve = new ExpCurveConfig();
        expCurve.setType("exponential");
        expCurve.setBase(150.0);
        expCurve.setMultiplier(1.25);
        magic.setExpCurve(expCurve);
        
        // Level Bonuses
        for (int i = 1; i <= 100; i++) {
            LevelBonusConfig bonus = new LevelBonusConfig(i);
            bonus.getModifiers().put("max_mana", 5.0);
            bonus.getModifiers().put("spell_power", 1.0);
            if (i % 5 == 0) {
                bonus.getModifiers().put("mana_regen", 0.5);
            }
            magic.getLevelBonuses().add(bonus);
        }
        
        // Skills
        SkillConfig skill1 = new SkillConfig();
        skill1.setId("mana_efficiency");
        skill1.setTier(1);
        skill1.setName("Mana-Effizienz");
        skill1.setDescription("Reduziert Manakosten um 5% pro Punkt");
        skill1.setCost(1);
        skill1.setMaxPoints(5);
        skill1.getEffects().put("mana_cost_reduction", 0.05);
        magic.getSkills().add(skill1);
        
        SkillConfig skill2 = new SkillConfig();
        skill2.setId("spell_mastery");
        skill2.setTier(1);
        skill2.setName("Zaubermeisterung");
        skill2.setDescription("Erhöht Zauberschaden um 8% pro Punkt");
        skill2.setCost(1);
        skill2.setMaxPoints(5);
        skill2.getEffects().put("spell_damage", 0.08);
        magic.getSkills().add(skill2);
        
        SkillConfig skill3 = new SkillConfig();
        skill3.setId("arcane_focus");
        skill3.setTier(2);
        skill3.setName("Arkaner Fokus");
        skill3.setDescription("Reduziert Zauber-Cooldown um 10% pro Punkt");
        skill3.setCost(2);
        skill3.setMaxPoints(3);
        skill3.getEffects().put("spell_cooldown_reduction", 0.10);
        magic.getSkills().add(skill3);
        
        SkillConfig skill4 = new SkillConfig();
        skill4.setId("elemental_mastery");
        skill4.setTier(3);
        skill4.setName("Elementare Meisterschaft");
        skill4.setDescription("Erhöht Elementarschaden um 15% pro Punkt");
        skill4.setCost(3);
        skill4.setMaxPoints(3);
        skill4.getEffects().put("elemental_damage", 0.15);
        magic.getSkills().add(skill4);
        
        // Milestones
        MilestoneQuestConfig quest10 = new MilestoneQuestConfig();
        quest10.setType("simple_talk");
        quest10.setNpcId("arcane_master");
        quest10.setDialogKey("magic_milestone_10");
        magic.getMilestones().put(10, quest10);
        
        MilestoneQuestConfig quest25 = new MilestoneQuestConfig();
        quest25.setType("simple_talk");
        quest25.setNpcId("arcane_master");
        quest25.setDialogKey("magic_milestone_25");
        magic.getMilestones().put(25, quest25);
        
        // Register category
        api.registerCategory(magic);
        
        // Register spell casting actions
        api.registerAction("cast_spell_common", "magic", 10.0);
        api.registerAction("cast_spell_uncommon", "magic", 20.0);
        api.registerAction("cast_spell_rare", "magic", 40.0);
        api.registerAction("cast_spell_epic", "magic", 80.0);
        api.registerAction("cast_spell_legendary", "magic", 150.0);
        
        SeyonLevelSystemPlugin.getInstance().getLogger()
            .at(Level.INFO)
            .log("Magic category registered successfully!");
        
        // TODO: Hook into SeyonMagic's spell cast events to grant EXP
        // This would require access to SeyonMagic's event system
    }
}
