package dev.seyon.leveling;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.seyon.leveling.api.LevelSystemAPI;
import dev.seyon.leveling.api.LevelSystemAPIImpl;
import dev.seyon.leveling.command.LevelSystemCommand;
import dev.seyon.leveling.event.BreakBlockExpSystem;
import dev.seyon.leveling.event.LevelSystemEventHandler;
import dev.seyon.leveling.service.*;

import javax.annotation.Nonnull;

/**
 * Main plugin class for Seyon Level System
 */
public class SeyonLevelSystemPlugin extends JavaPlugin {

    private static SeyonLevelSystemPlugin INSTANCE;
    private JavaPluginInit pluginInit;
    
    // Services
    private LevelSystemConfigService configService;
    private CategoryService categoryService;
    private LevelSystemDataService dataService;
    private ExperienceService experienceService;
    private SkillService skillService;
    private ModifierService modifierService;
    private QuestService questService;
    private ActionRegistryService actionRegistryService;
    
    // API
    private LevelSystemAPI api;

    public SeyonLevelSystemPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        this.pluginInit = init;
    }

    public static SeyonLevelSystemPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    protected void setup() {
        super.setup();

        // Create plugin directory
        var folder = new java.io.File("SeyonLevelSystem");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Initialize services in correct order
        this.configService = new LevelSystemConfigService(this.getLogger());
        this.categoryService = new CategoryService(this.getLogger());
        this.dataService = new LevelSystemDataService(this.getLogger());
        this.actionRegistryService = new ActionRegistryService(this.getLogger());
        this.experienceService = new ExperienceService(this.getLogger(), this.categoryService, this.dataService, this.configService);
        this.skillService = new SkillService(this.getLogger(), this.categoryService, this.dataService);
        this.modifierService = new ModifierService(this.getLogger(), this.categoryService, this.dataService, this.skillService);
        this.questService = new QuestService(this.getLogger(), this.categoryService, this.dataService);

        // Load configuration
        this.configService.load();
        this.categoryService.loadCategories(this.configService);
        this.actionRegistryService.loadActions(this.configService);

        // Initialize API
        this.api = new LevelSystemAPIImpl(
            this.categoryService,
            this.actionRegistryService,
            this.experienceService,
            this.dataService,
            this.modifierService
        );

        // Register command
        this.getCommandRegistry().registerCommand(new LevelSystemCommand());

        // Register event handler
        this.getEventRegistry().registerGlobal(
            PlayerReadyEvent.class, 
            event -> LevelSystemEventHandler.onPlayerReady(event, this)
        );

        // Register BreakBlock -> EXP system (action IDs: break_<blockType.getId()>)
        this.getEntityStoreRegistry().registerSystem(new BreakBlockExpSystem(this));

        // Try to integrate with Seyon Arcane Arts if installed
        try {
            dev.seyon.leveling.integration.MagicIntegration.integrate();
        } catch (Exception e) {
            this.getLogger().at(java.util.logging.Level.WARNING)
                .withCause(e)
                .log("Failed to integrate with Seyon Arcane Arts");
        }

        this.getLogger().at(java.util.logging.Level.INFO).log("Seyon Level System Plugin loaded successfully!");
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        // Save all player data on shutdown
        this.dataService.saveAll();
        this.getLogger().at(java.util.logging.Level.INFO).log("Seyon Level System Plugin shutdown successfully!");
    }

    // Getters for services
    public LevelSystemConfigService getConfigService() {
        return configService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public LevelSystemDataService getDataService() {
        return dataService;
    }

    public ExperienceService getExperienceService() {
        return experienceService;
    }

    public SkillService getSkillService() {
        return skillService;
    }

    public ModifierService getModifierService() {
        return modifierService;
    }

    public QuestService getQuestService() {
        return questService;
    }

    public ActionRegistryService getActionRegistryService() {
        return actionRegistryService;
    }

    public LevelSystemAPI getAPI() {
        return api;
    }
}
