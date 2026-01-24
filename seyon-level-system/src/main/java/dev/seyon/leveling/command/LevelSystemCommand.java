package dev.seyon.leveling.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.gui.LevelSystemMainGui;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

/**
 * Main command for Level System
 * Commands: /leveling, /leveling stats, /leveling addexp, /leveling setlevel, /leveling reload
 */
public class LevelSystemCommand extends AbstractAsyncCommand {

    // Register command arguments
    RequiredArg<String> subcommandArg = this.withRequiredArg("subcommand", "gui, stats, addexp, setlevel, resetskills, or reload", ArgTypes.STRING);
    OptionalArg<String> playerArg = this.withOptionalArg("player", "Player name", ArgTypes.STRING);
    OptionalArg<String> categoryArg = this.withOptionalArg("category", "Category ID", ArgTypes.STRING);
    OptionalArg<Integer> amountArg = this.withOptionalArg("amount", "Amount", ArgTypes.INTEGER);
    OptionalArg<Integer> levelArg = this.withOptionalArg("level", "Level", ArgTypes.INTEGER);

    public LevelSystemCommand() {
        super("leveling", "Level System commands");
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        
        // Get subcommand from argument (defaults to "gui")
        String subcommand = this.subcommandArg.get(commandContext);
        if (subcommand == null || subcommand.isEmpty()) {
            subcommand = "gui";
        }
        subcommand = subcommand.toLowerCase();
        
        return switch (subcommand) {
            case "stats" -> handleStats(commandContext, sender);
            case "addexp" -> handleAddExp(commandContext, sender);
            case "setlevel" -> handleSetLevel(commandContext, sender);
            case "resetskills" -> handleResetSkills(commandContext, sender);
            case "reload" -> handleReload(commandContext, sender);
            case "gui" -> handleGui(commandContext, sender);
            default -> handleHelp(commandContext, sender);
        };
    }

    /**
     * Open Level System GUI (same pattern as seyon-motd)
     */
    private CompletableFuture<Void> handleGui(CommandContext context, CommandSender sender) {
        if (!(sender instanceof Player player)) {
            context.sendMessage(Message.raw("This command can only be used by players.").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }

        player.getWorldMapTracker().tick(0);
        Ref<EntityStore> ref = player.getReference();

        if (ref == null || !ref.isValid()) {
            context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
            return CompletableFuture.completedFuture(null);
        }

        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();

        return CompletableFuture.runAsync(() -> {
            PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRefComponent != null) {
                player.getPageManager().openCustomPage(
                        ref,
                        store,
                        new LevelSystemMainGui(playerRefComponent, CustomPageLifetime.CanDismiss)
                );
            }
        }, world);
    }

    /**
     * Show stats for a player
     */
    private CompletableFuture<Void> handleStats(CommandContext context, CommandSender sender) {
        Player player;
        
        if (sender instanceof Player senderPlayer) {
            String targetPlayerName = playerArg.get(context);
            if (targetPlayerName != null && !targetPlayerName.isEmpty()) {
                // TODO: Get player by name
                context.sendMessage(Message.raw("Player lookup not yet implemented").color(Color.RED));
                return CompletableFuture.completedFuture(null);
            } else {
                player = senderPlayer;
            }
        } else {
            context.sendMessage(Message.raw("You must specify a player name from console.").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }
        
        // Show stats
        // Note: Using deprecated getUuid() here because async commands run outside WorldThread
        // and cannot access the Component System (PlayerUtils.getPlayerUUID requires WorldThread)
        PlayerLevelSystemData data = SeyonLevelSystemPlugin.getInstance().getDataService().getPlayerData(player.getUuid());
        
        context.sendMessage(Message.raw("=== Level System Stats for " + player.getDisplayName() + " ===").color(Color.ORANGE).bold(true));
        
        for (LevelSystemCategory category : SeyonLevelSystemPlugin.getInstance().getCategoryService().getAllCategories()) {
            CategoryProgress progress = data.getCategoryProgress().get(category.getId());
            if (progress != null) {
                int level = progress.getCurrentLevel();
                double exp = progress.getCurrentExp();
                double expNeeded = progress.getExpForNextLevel();
                int skillPoints = data.getAvailableSkillPoints(category.getId());
                
                context.sendMessage(Message.join(
                    Message.raw(category.getDisplayName() + ": ").color(Color.CYAN),
                    Message.raw("Level " + level).color(Color.GREEN),
                    Message.raw(" (" + String.format("%.0f", exp) + "/" + String.format("%.0f", expNeeded) + " EXP)").color(Color.GRAY),
                    Message.raw(" [" + skillPoints + " SP]").color(Color.YELLOW)
                ));
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Add experience to a player
     */
    private CompletableFuture<Void> handleAddExp(CommandContext context, CommandSender sender) {
        String playerName = playerArg.get(context);
        String categoryId = categoryArg.get(context);
        Integer amount = amountArg.get(context);
        
        if (playerName == null || categoryId == null || amount == null) {
            context.sendMessage(Message.raw("Usage: /leveling addexp --player=<name> --category=<id> --amount=<exp>").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }
        
        // TODO: Get player by name and add EXP
        context.sendMessage(Message.raw("Player lookup not yet implemented").color(Color.YELLOW));
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Set player level
     */
    private CompletableFuture<Void> handleSetLevel(CommandContext context, CommandSender sender) {
        String playerName = playerArg.get(context);
        String categoryId = categoryArg.get(context);
        Integer level = levelArg.get(context);
        
        if (playerName == null || categoryId == null || level == null) {
            context.sendMessage(Message.raw("Usage: /leveling setlevel --player=<name> --category=<id> --level=<level>").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }
        
        // TODO: Get player by name and set level
        context.sendMessage(Message.raw("Player lookup not yet implemented").color(Color.YELLOW));
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Reset skills for a player
     */
    private CompletableFuture<Void> handleResetSkills(CommandContext context, CommandSender sender) {
        String playerName = playerArg.get(context);
        String categoryId = categoryArg.get(context);
        
        if (playerName == null || categoryId == null) {
            context.sendMessage(Message.raw("Usage: /leveling resetskills --player=<name> --category=<id>").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }
        
        // TODO: Get player by name and reset skills
        context.sendMessage(Message.raw("Player lookup not yet implemented").color(Color.YELLOW));
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Reload configuration
     */
    private CompletableFuture<Void> handleReload(CommandContext context, CommandSender sender) {
        context.sendMessage(Message.join(
            Message.raw("[Level System] ").color(Color.ORANGE),
            Message.raw("Reloading configuration...").color(Color.YELLOW)
        ));
        
        try {
            SeyonLevelSystemPlugin.getInstance().getConfigService().reload();
            SeyonLevelSystemPlugin.getInstance().getCategoryService().loadCategories(
                SeyonLevelSystemPlugin.getInstance().getConfigService()
            );
            SeyonLevelSystemPlugin.getInstance().getActionRegistryService().loadActions(
                SeyonLevelSystemPlugin.getInstance().getConfigService()
            );
            
            context.sendMessage(Message.join(
                Message.raw("[Level System] ").color(Color.ORANGE),
                Message.raw("Configuration reloaded successfully!").color(Color.GREEN)
            ));
        } catch (Exception e) {
            context.sendMessage(Message.join(
                Message.raw("[Level System] ").color(Color.ORANGE),
                Message.raw("Failed to reload configuration! Check console for errors.").color(Color.RED)
            ));
            SeyonLevelSystemPlugin.getInstance().getLogger()
                .at(java.util.logging.Level.SEVERE)
                .withCause(e)
                .log("Failed to reload configuration");
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Show help
     */
    private CompletableFuture<Void> handleHelp(CommandContext context, CommandSender sender) {
        context.sendMessage(Message.raw("=== Level System Commands ===").color(Color.ORANGE).bold(true));
        context.sendMessage(Message.raw("/leveling - Open Level System GUI").color(Color.GRAY));
        context.sendMessage(Message.raw("/leveling stats [player] - View Level System statistics").color(Color.GRAY));
        context.sendMessage(Message.raw("/leveling addexp <player> <category> <amount> - Grant experience (admin)").color(Color.GRAY));
        context.sendMessage(Message.raw("/leveling setlevel <player> <category> <level> - Set player level (admin)").color(Color.GRAY));
        context.sendMessage(Message.raw("/leveling resetskills <player> <category> - Reset skills (admin)").color(Color.GRAY));
        context.sendMessage(Message.raw("/leveling reload - Reload configuration").color(Color.GRAY));
        
        return CompletableFuture.completedFuture(null);
    }
}
