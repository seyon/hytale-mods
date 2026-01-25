package dev.seyon.leveling.event;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ECS system: on UseBlockEvent.Post, grant Farming EXP when a player harvests a crop (uses a harvestable block).
 * Action IDs: "harvest_&lt;blockId&gt;" (from BlockType.getId()) or fallback "harvest_crop".
 * Add harvest_&lt;blockId&gt; or harvest_crop to actions/farming.json.
 */
public class UseBlockHarvestExpSystem extends EntityEventSystem<EntityStore, UseBlockEvent.Post> {

    private static final String ACTION_HARVEST_CROP = "harvest_crop";

    private final SeyonLevelSystemPlugin plugin;

    public UseBlockHarvestExpSystem(SeyonLevelSystemPlugin plugin) {
        super(UseBlockEvent.Post.class);
        this.plugin = plugin;
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull UseBlockEvent.Post event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        String blockId = event.getBlockType() != null ? event.getBlockType().getId() : null;
        if (blockId == null || blockId.isEmpty()) {
            return;
        }

        ActionRegistryService actionRegistry = plugin.getActionRegistryService();
        String actionId = actionRegistry.hasAction("harvest_" + blockId) ? "harvest_" + blockId : ACTION_HARVEST_CROP;
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
