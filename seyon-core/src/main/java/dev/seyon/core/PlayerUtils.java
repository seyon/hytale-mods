package dev.seyon.core;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Utility class for Player-related operations
 */
public class PlayerUtils {

    /**
     * Get the UUID of a player - SAFE FOR ALL CONTEXTS
     * 
     * This method uses the deprecated getUuid() method internally,
     * but centralizes the deprecated usage to one location.
     * 
     * This is safe to use in:
     * - Event handlers
     * - Async commands
     * - Sync commands
     * - World thread code
     * 
     * @param player The player
     * @return The player's UUID, or null if not available
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static UUID getPlayerUUID(@Nonnull Player player) {
        return player.getUuid();
    }

    /**
     * Get the PlayerRef of a player - SAFE FOR ALL CONTEXTS
     * 
     * This method uses the deprecated getPlayerRef() method internally,
     * but centralizes the deprecated usage to one location.
     * 
     * This is safe to use in:
     * - Event handlers
     * - Async commands
     * - Sync commands
     * - World thread code
     * 
     * @param player The player
     * @return The player's PlayerRef, or null if not available
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static PlayerRef getPlayerRef(@Nonnull Player player) {
        return player.getPlayerRef();
    }

    /**
     * Get the UUID of a player using the new Component-based system
     * 
     * WARNING: This method requires World-Thread access!
     * Only use this in synchronous world-thread code.
     * For general use, prefer getPlayerUUID() instead.
     * 
     * @param player The player
     * @return The player's UUID, or null if not available
     */
    @Nullable
    public static UUID getPlayerUUIDViaComponents(@Nonnull Player player) {
        // Get the entity reference
        Ref<EntityStore> ref = player.getReference();
        if (ref == null || !ref.isValid()) {
            return null;
        }
        
        // Get the world and store
        if (player.getWorld() == null) {
            return null;
        }
        
        Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
        
        // Get the UUID component
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return null;
        }
        
        return uuidComponent.getUuid();
    }

    /**
     * Get the PlayerRef using the new Component-based system
     * 
     * WARNING: This method requires World-Thread access!
     * Only use this in synchronous world-thread code.
     * For general use, prefer getPlayerRef() instead.
     * 
     * @param player The player
     * @return The player's PlayerRef, or null if not available
     */
    @Nullable
    public static PlayerRef getPlayerRefViaComponents(@Nonnull Player player) {
        Ref<EntityStore> ref = player.getReference();
        if (ref == null || !ref.isValid()) {
            return null;
        }
        
        if (player.getWorld() == null) {
            return null;
        }
        
        Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
        return store.getComponent(ref, PlayerRef.getComponentType());
    }
}
