package dev.seyon.magic.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.magic.SeyonMagicPlugin;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

/**
 * Test command for Arcane Arts magic system
 */
public class SeyonMagicCommand extends AbstractAsyncCommand {

    public SeyonMagicCommand() {
        super("seyon-magic", "Test command for Arcane Arts magic system");
        this.setPermissionGroups("OP");
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                
                // Send test message to player
                player.sendMessage(Message.join(
                    Message.raw("[Arcane Arts] ").color(Color.ORANGE),
                    Message.raw("Magic system is loaded!").color(Color.GRAY)
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
                
                return CompletableFuture.completedFuture(null);
            } else {
                commandContext.sendMessage(Message.raw("You must be in a world to use this command."));
                return CompletableFuture.completedFuture(null);
            }
        } else {
            commandContext.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }
    }
}
