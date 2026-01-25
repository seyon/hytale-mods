package dev.seyon.leveling.event;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DiscoverZoneEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ECS system: on DiscoverZoneEvent.Display, grant Exploration EXP when a player discovers a zone.
 * Action ID: "discover_zone" (configurable in actions/exploration.json).
 */
public class DiscoverZoneExpSystem extends EntityEventSystem<EntityStore, DiscoverZoneEvent.Display> {

    private static final String ACTION_DISCOVER_ZONE = "discover_zone";

    private final SeyonLevelSystemPlugin plugin;

    public DiscoverZoneExpSystem(SeyonLevelSystemPlugin plugin) {
        super(DiscoverZoneEvent.Display.class);
        this.plugin = plugin;
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull DiscoverZoneEvent.Display event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        ActionRegistryService actionRegistry = plugin.getActionRegistryService();
        if (!actionRegistry.hasAction(ACTION_DISCOVER_ZONE)) {
            return;
        }

        ActionRegistryService.ActionMapping mapping = actionRegistry.getActionMapping(ACTION_DISCOVER_ZONE);
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
