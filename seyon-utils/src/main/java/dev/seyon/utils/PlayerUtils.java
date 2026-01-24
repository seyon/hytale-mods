package dev.seyon.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Utility class for Player-related operations
 */
public class PlayerUtils {

    /**
     * Get the UUID of a player using the new Component-based system
     * This is the non-deprecated way to get a player's UUID
     * 
     * WARNING: This method requires World-Thread access!
     * For async commands, use player.getUuid() instead (deprecated but functional)
     * 
     * @param player The player
     * @return The player's UUID, or null if not available
     */
    @Nullable
    public static UUID getPlayerUUID(@Nonnull Player player) {
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
}
