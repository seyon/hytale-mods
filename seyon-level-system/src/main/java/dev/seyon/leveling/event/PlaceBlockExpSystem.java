package dev.seyon.leveling.event;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ECS system: on PlaceBlockEvent, grant EXP when a player places a block.
 * Action IDs: "place_&lt;blockKey&gt;" (from ItemStack.getBlockKey()) or "place_&lt;itemId&gt;" (from ItemStack.getItemId()).
 * Add these to actions/farming.json (or other categories) for crop planting, e.g. place_wheat, place_carrot.
 */
public class PlaceBlockExpSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    private final SeyonLevelSystemPlugin plugin;

    public PlaceBlockExpSystem(SeyonLevelSystemPlugin plugin) {
        super(PlaceBlockEvent.class);
        this.plugin = plugin;
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull PlaceBlockEvent event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand == null || itemInHand.isEmpty()) {
            return;
        }

        String actionId = null;
        String blockKey = itemInHand.getBlockKey();
        if (blockKey != null && !blockKey.isEmpty() && !"Empty".equals(blockKey)) {
            actionId = "place_" + blockKey;
        }
        if (actionId == null || !plugin.getActionRegistryService().hasAction(actionId)) {
            String itemId = itemInHand.getItemId();
            if (itemId != null && !itemId.isEmpty() && !"Empty".equals(itemId)) {
                actionId = "place_" + itemId;
            }
        }
        if (actionId == null) {
            return;
        }

        ActionRegistryService actionRegistry = plugin.getActionRegistryService();
        if (!actionRegistry.hasAction(actionId)) {
            return;
        }

        ActionRegistryService.ActionMapping mapping = actionRegistry.getActionMapping(actionId);
        if (mapping == null) {
            return;
        }

        java.util.UUID playerId = dev.seyon.core.PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            return;
        }

        ExperienceService expService = plugin.getExperienceService();
        expService.grantExp(playerId, mapping.getCategoryId(), mapping.getExp(), player);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
