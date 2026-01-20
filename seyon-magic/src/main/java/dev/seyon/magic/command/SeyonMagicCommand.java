package dev.seyon.magic.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.magic.SeyonMagicPlugin;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

/**
 * Main command for Arcane Arts magic system
 * Supports subcommands: help, reload
 */
public class SeyonMagicCommand extends AbstractAsyncCommand {

    // Register subcommand argument with default value "status"
    RequiredArg<String> subcommandArg = this.withRequiredArg("argument_name", "status, reload or help", ArgTypes.STRING);

    public SeyonMagicCommand() {
        super("seyon-magic", "Arcane Arts magic system commands");
        this.setPermissionGroups("OP");
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        
        // Get subcommand from argument (defaults to "status")
        String subcommand = this.subcommandArg.get(commandContext).toLowerCase();
        
        switch (subcommand) {
            case "reload":
                return handleReload(commandContext, sender);
            case "help":
                return handleHelp(commandContext, sender);
            case "status":
            default:
                return handleStatus(commandContext, sender);
        }
    }

    /**
     * Handle status subcommand (default)
     */
    private CompletableFuture<Void> handleStatus(CommandContext context, CommandSender sender) {
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            
            if (ref != null && ref.isValid()) {
                // Send status message to player
                player.sendMessage(Message.join(
                    Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                    Message.raw("Magic System").color(Color.YELLOW).bold(true)
                ));
                String version = String.valueOf(SeyonMagicPlugin.getInstance().getManifest().getVersion());
                player.sendMessage(Message.join(
                    Message.raw("Version: ").color(Color.GRAY),
                    Message.raw(version).color(Color.WHITE)
                ));
                player.sendMessage(Message.join(
                    Message.raw("Status: ").color(Color.GRAY),
                    Message.raw("Active").color(Color.GREEN)
                ));
                
                // Show available commands
                player.sendMessage(Message.raw(""));
                player.sendMessage(Message.join(
                    Message.raw("Available Commands:").color(Color.YELLOW)
                ));
                player.sendMessage(Message.join(
                    Message.raw("  /seyon-magic ").color(Color.GRAY),
                    Message.raw("- Show status").color(Color.WHITE)
                ));
                player.sendMessage(Message.join(
                    Message.raw("  /seyon-magic reload ").color(Color.GRAY),
                    Message.raw("- Reload configuration").color(Color.WHITE)
                ));
                player.sendMessage(Message.join(
                    Message.raw("  /seyon-magic help ").color(Color.GRAY),
                    Message.raw("- Show help").color(Color.WHITE)
                ));
                
                return CompletableFuture.completedFuture(null);
            } else {
                context.sendMessage(Message.raw("You must be in a world to use this command."));
                return CompletableFuture.completedFuture(null);
            }
        } else {
            // Console can also see status
            context.sendMessage(Message.join(
                Message.raw("[Arcane Arts] Status: ").color(Color.ORANGE),
                Message.raw("Active").color(Color.GREEN)
            ));
            String version = String.valueOf(SeyonMagicPlugin.getInstance().getManifest().getVersion());
            context.sendMessage(Message.join(
                Message.raw("Version: ").color(Color.GRAY),
                Message.raw(version).color(Color.WHITE)
            ));
            
            // Show available commands for console too
            context.sendMessage(Message.raw(""));
            context.sendMessage(Message.raw("Available Commands:"));
            context.sendMessage(Message.raw("  /seyon-magic - Show status"));
            context.sendMessage(Message.raw("  /seyon-magic reload - Reload configuration"));
            context.sendMessage(Message.raw("  /seyon-magic help - Show help"));
            
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Handle reload subcommand
     */
    private CompletableFuture<Void> handleReload(CommandContext context, CommandSender sender) {
        context.sendMessage(Message.join(
            Message.raw("[Arcane Arts] ").color(Color.ORANGE),
            Message.raw("Reloading configuration...").color(Color.YELLOW)
        ));
        
        try {
            SeyonMagicPlugin.getInstance().getConfigService().reload();
            
            context.sendMessage(Message.join(
                Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                Message.raw("Configuration reloaded successfully!").color(Color.GREEN)
            ));
        } catch (Exception e) {
            context.sendMessage(Message.join(
                Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                Message.raw("Failed to reload configuration! Check console for errors.").color(Color.RED)
            ));
            SeyonMagicPlugin.getInstance().getLogger()
                .at(java.util.logging.Level.SEVERE)
                .withCause(e)
                .log("Failed to reload configuration");
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Handle help subcommand
     */
    private CompletableFuture<Void> handleHelp(CommandContext context, CommandSender sender) {
        context.sendMessage(Message.join(
            Message.raw("[Arcane Arts] ").color(Color.ORANGE),
            Message.raw("Available Commands:").color(Color.YELLOW).bold(true)
        ));
        
        context.sendMessage(Message.join(
            Message.raw("  /seyon-magic ").color(Color.GRAY),
            Message.raw("- Show status").color(Color.WHITE)
        ));
        
        context.sendMessage(Message.join(
            Message.raw("  /seyon-magic reload ").color(Color.GRAY),
            Message.raw("- Reload configuration").color(Color.WHITE)
        ));
        
        context.sendMessage(Message.join(
            Message.raw("  /seyon-magic help ").color(Color.GRAY),
            Message.raw("- Show this help").color(Color.WHITE)
        ));
        
        return CompletableFuture.completedFuture(null);
    }
}
