package dev.seyon.leveling.event;

import com.hypixel.hytale.builtin.instances.event.DiscoverInstanceEvent;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ECS system: on DiscoverInstanceEvent.Display, grant Exploration EXP when a player discovers an instance.
 * Action ID: "discover_instance" (add to actions/exploration.json).
 * Requires the Hytale instances/builtin to be available; if DiscoverInstanceEvent is not on the classpath,
 * do not register this system.
 */
public class DiscoverInstanceExpSystem extends EntityEventSystem<EntityStore, DiscoverInstanceEvent.Display> {

    private static final String ACTION_DISCOVER_INSTANCE = "discover_instance";

    private final SeyonLevelSystemPlugin plugin;

    public DiscoverInstanceExpSystem(SeyonLevelSystemPlugin plugin) {
        super(DiscoverInstanceEvent.Display.class);
        this.plugin = plugin;
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull DiscoverInstanceEvent.Display event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        ActionRegistryService actionRegistry = plugin.getActionRegistryService();
        if (!actionRegistry.hasAction(ACTION_DISCOVER_INSTANCE)) {
            return;
        }

        ActionRegistryService.ActionMapping mapping = actionRegistry.getActionMapping(ACTION_DISCOVER_INSTANCE);
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
