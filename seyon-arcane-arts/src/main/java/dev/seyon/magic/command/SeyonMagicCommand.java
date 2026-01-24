package dev.seyon.magic.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
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

    // Register command arguments
    RequiredArg<String> subcommandArg = this.withRequiredArg("subcommand", "status, reload, give, or help", ArgTypes.STRING);
    OptionalArg<String> itemTypeArg = this.withOptionalArg("itemType", "wand or grimoire", ArgTypes.STRING);
    OptionalArg<String> qualityArg = this.withOptionalArg("quality", "common, uncommon, rare, epic, or legendary", ArgTypes.STRING);

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
            case "give":
                return handleGive(commandContext, sender);
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
                    Message.raw("  /seyon-magic give ").color(Color.GRAY),
                    Message.raw("- Give magic item (usage: give <wand|grimoire> <quality>)").color(Color.WHITE)
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
            context.sendMessage(Message.raw("  /seyon-magic give - Give magic item"));
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
            Message.raw("  /seyon-magic give ").color(Color.GRAY),
            Message.raw("- Give magic item").color(Color.WHITE)
        ));
        
        context.sendMessage(Message.join(
            Message.raw("  /seyon-magic help ").color(Color.GRAY),
            Message.raw("- Show this help").color(Color.WHITE)
        ));
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Handle give subcommand
     */
    private CompletableFuture<Void> handleGive(CommandContext context, CommandSender sender) {
        if (!(sender instanceof Player player)) {
            context.sendMessage(Message.join(
                Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                Message.raw("This command can only be used by players.").color(Color.RED)
            ));
            return CompletableFuture.completedFuture(null);
        }
        
        Ref<EntityStore> ref = player.getReference();
        if (ref == null || !ref.isValid()) {
            context.sendMessage(Message.raw("You must be in a world to use this command."));
            return CompletableFuture.completedFuture(null);
        }
        
        // Get arguments
        String itemType = itemTypeArg.get(context);
        String quality = qualityArg.get(context);
        
        if (itemType == null || itemType.isEmpty()) {
            context.sendMessage(Message.join(
                Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                Message.raw("Usage: /seyon-magic give --itemType=<wand|grimoire> --quality=<quality>").color(Color.RED)
            ));
            context.sendMessage(Message.raw("Example: /seyon-magic give --itemType=wand --quality=legendary"));
            context.sendMessage(Message.raw("Qualities: common, uncommon, rare, epic, legendary"));
            return CompletableFuture.completedFuture(null);
        }
        
        itemType = itemType.toLowerCase();
        quality = (quality == null || quality.isEmpty()) ? "common" : quality.toLowerCase();
        
        // Debug log to see what we received
        SeyonMagicPlugin.getInstance().getLogger()
            .at(java.util.logging.Level.INFO)
            .log("Give command - itemType: " + itemType + ", quality: " + quality);
        
        // Create item
        com.hypixel.hytale.server.core.inventory.ItemStack itemStack;
        try {
            if ("wand".equals(itemType)) {
                itemStack = SeyonMagicPlugin.getInstance().getItemService().createWand(quality);
            } else if ("grimoire".equals(itemType)) {
                itemStack = SeyonMagicPlugin.getInstance().getItemService().createGrimoire(quality);
            } else {
                context.sendMessage(Message.join(
                    Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                    Message.raw("Unknown item type: " + itemType).color(Color.RED)
                ));
                context.sendMessage(Message.raw("Valid types: wand, grimoire"));
                return CompletableFuture.completedFuture(null);
            }
            
            // Add to player inventory
            var transaction = player.getInventory().getCombinedHotbarFirst().addItemStack(itemStack);
            var remainder = transaction.getRemainder();
            
            if (remainder != null && !remainder.isEmpty()) {
                context.sendMessage(Message.join(
                    Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                    Message.raw("Insufficient inventory space!").color(Color.RED)
                ));
            } else {
                context.sendMessage(Message.join(
                    Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                    Message.raw("Gave you a ").color(Color.GREEN),
                    Message.raw(quality + " " + itemType).color(Color.YELLOW),
                    Message.raw("!").color(Color.GREEN)
                ));
            }
            
        } catch (Exception e) {
            context.sendMessage(Message.join(
                Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                Message.raw("Error creating item: " + e.getMessage()).color(Color.RED)
            ));
            SeyonMagicPlugin.getInstance().getLogger()
                .at(java.util.logging.Level.SEVERE)
                .withCause(e)
                .log("Failed to create magic item");
        }
        
        return CompletableFuture.completedFuture(null);
    }
}
