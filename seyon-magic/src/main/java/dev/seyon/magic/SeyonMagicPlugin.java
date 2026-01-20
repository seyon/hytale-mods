package dev.seyon.magic;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.seyon.magic.command.SeyonMagicCommand;
import dev.seyon.magic.event.MagicEventHandler;
import dev.seyon.magic.service.MagicConfigService;
import dev.seyon.magic.service.MagicItemService;
import dev.seyon.magic.service.SpellService;

import javax.annotation.Nonnull;

/**
 * Main plugin class for Arcane Arts magic system
 */
public class SeyonMagicPlugin extends JavaPlugin {

    private static SeyonMagicPlugin INSTANCE;
    private JavaPluginInit pluginInit;
    
    // Services
    private MagicConfigService configService;
    private MagicItemService itemService;
    private SpellService spellService;

    public SeyonMagicPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        this.pluginInit = init;
    }

    public static SeyonMagicPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    protected void setup() {
        super.setup();

        // Create plugin directory
        var folder = new java.io.File("SeyonMagic");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Initialize services
        this.configService = new MagicConfigService(this.getLogger());
        this.itemService = new MagicItemService(this.getLogger(), this.configService);
        this.spellService = new SpellService(this.getLogger());

        // Load configuration
        this.configService.load();

        // Register command
        this.getCommandRegistry().registerCommand(new SeyonMagicCommand());

        // Register event handler
        this.getEventRegistry().registerGlobal(
            PlayerReadyEvent.class, 
            event -> MagicEventHandler.onPlayerReady(event, this)
        );

        this.getLogger().at(java.util.logging.Level.INFO).log("Arcane Arts Magic Plugin loaded successfully!");
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        // Save configuration on shutdown
        this.configService.save();
        this.getLogger().at(java.util.logging.Level.INFO).log("Arcane Arts Magic Plugin shutdown successfully!");
    }

    public MagicConfigService getConfigService() {
        return configService;
    }

    public MagicItemService getItemService() {
        return itemService;
    }

    public SpellService getSpellService() {
        return spellService;
    }
}
