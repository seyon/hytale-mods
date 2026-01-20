package dev.seyon.magic.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import dev.seyon.magic.config.ItemQualityConfig;
import dev.seyon.magic.config.MagicItemConfig;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonInt32;
import org.bson.BsonDouble;

import java.util.logging.Level;

/**
 * Service for managing magic items (wands, grimoires)
 */
public class MagicItemService {

    private final HytaleLogger logger;
    private final MagicConfigService configService;
    
    // Base item IDs (using vanilla items for now - can be customized later)
    private static final String WAND_BASE_ITEM = "Common_Grass"; // Placeholder
    private static final String GRIMOIRE_BASE_ITEM = "Common_Grass"; // Placeholder

    public MagicItemService(HytaleLogger logger, MagicConfigService configService) {
        this.logger = logger;
        this.configService = configService;
    }

    /**
     * Create a magic wand with given quality
     */
    public ItemStack createWand(String quality) {
        logger.at(Level.INFO).log("MagicItemService: Creating wand with quality: " + quality);
        
        MagicItemConfig wandConfig = configService.getWandConfig();
        ItemQualityConfig qualityConfig = wandConfig.getQualities().get(quality.toLowerCase());
        
        if (qualityConfig == null) {
            logger.at(Level.WARNING).log("Unknown quality: " + quality + ", using 'common'");
            qualityConfig = wandConfig.getQualities().get("common");
        }
        
        // Create metadata with magic properties
        BsonDocument metadata = new BsonDocument();
        metadata.put("magic_type", new BsonString("wand"));
        metadata.put("quality", new BsonString(quality.toLowerCase()));
        metadata.put("mana", new BsonInt32(qualityConfig.getMana()));
        metadata.put("power_mult", new BsonDouble(qualityConfig.getPowerMult()));
        metadata.put("speed_mult", new BsonDouble(qualityConfig.getSpeedMult()));
        metadata.put("max_modifiers", new BsonInt32(qualityConfig.getMaxModifiers()));
        metadata.put("point_limit", new BsonInt32(qualityConfig.getPointLimit()));
        metadata.put("affinity_slots", new BsonInt32(qualityConfig.getAffinitySlots()));
        metadata.put("spell_slots", new BsonInt32(qualityConfig.getSpellSlots()));
        
        return new ItemStack(WAND_BASE_ITEM, 1, metadata);
    }

    /**
     * Create a grimoire with given quality
     */
    public ItemStack createGrimoire(String quality) {
        logger.at(Level.INFO).log("MagicItemService: Creating grimoire with quality: " + quality);
        
        MagicItemConfig grimoireConfig = configService.getGrimoireConfig();
        ItemQualityConfig qualityConfig = grimoireConfig.getQualities().get(quality.toLowerCase());
        
        if (qualityConfig == null) {
            logger.at(Level.WARNING).log("Unknown quality: " + quality + ", using 'common'");
            qualityConfig = grimoireConfig.getQualities().get("common");
        }
        
        // Create metadata with magic properties
        BsonDocument metadata = new BsonDocument();
        metadata.put("magic_type", new BsonString("grimoire"));
        metadata.put("quality", new BsonString(quality.toLowerCase()));
        metadata.put("mana", new BsonInt32(qualityConfig.getMana()));
        metadata.put("power_mult", new BsonDouble(qualityConfig.getPowerMult()));
        metadata.put("speed_mult", new BsonDouble(qualityConfig.getSpeedMult()));
        metadata.put("max_modifiers", new BsonInt32(qualityConfig.getMaxModifiers()));
        metadata.put("point_limit", new BsonInt32(qualityConfig.getPointLimit()));
        metadata.put("affinity_slots", new BsonInt32(qualityConfig.getAffinitySlots()));
        metadata.put("spell_slots", new BsonInt32(qualityConfig.getSpellSlots()));
        
        return new ItemStack(GRIMOIRE_BASE_ITEM, 1, metadata);
    }
    
    /**
     * Check if an item is a magic wand
     */
    public boolean isWand(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        BsonDocument metadata = item.getMetadata();
        if (metadata == null) return false;
        return metadata.containsKey("magic_type") && 
               "wand".equals(metadata.getString("magic_type").getValue());
    }
    
    /**
     * Check if an item is a magic grimoire
     */
    public boolean isGrimoire(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        BsonDocument metadata = item.getMetadata();
        if (metadata == null) return false;
        return metadata.containsKey("magic_type") && 
               "grimoire".equals(metadata.getString("magic_type").getValue());
    }
}
