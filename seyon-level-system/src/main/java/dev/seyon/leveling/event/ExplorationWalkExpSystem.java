package dev.seyon.leveling.event;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

/**
 * EntityTickingSystem: every 100 blocks walked, grant Exploration EXP (action: explore_steps).
 * Tracks per-player distance via plugin's walk tracker map; cleaned on disconnect.
 */
public class ExplorationWalkExpSystem extends com.hypixel.hytale.component.system.tick.EntityTickingSystem<EntityStore> {

    private static final String ACTION_EXPLORE_STEPS = "explore_steps";
    /** Distance in blocks that must be walked to grant one explore_steps EXP. */
    private static final double STEPS_PER_GRANT = 100.0;

    private final com.hypixel.hytale.component.ComponentType<EntityStore, Player> playerType =
        Player.getComponentType();
    private final com.hypixel.hytale.component.ComponentType<EntityStore, TransformComponent> transformType =
        TransformComponent.getComponentType();
    private final Query<EntityStore> query = Query.and(playerType, transformType);

    private final SeyonLevelSystemPlugin plugin;

    public ExplorationWalkExpSystem(SeyonLevelSystemPlugin plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(index, playerType);
        TransformComponent transform = archetypeChunk.getComponent(index, transformType);
        if (player == null || transform == null) {
            return;
        }

        UUID playerId = dev.seyon.core.PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            return;
        }

        ActionRegistryService actionRegistry = plugin.getActionRegistryService();
        if (!actionRegistry.hasAction(ACTION_EXPLORE_STEPS)) {
            return;
        }
        ActionRegistryService.ActionMapping mapping = actionRegistry.getActionMapping(ACTION_EXPLORE_STEPS);
        if (mapping == null) {
            return;
        }

        Vector3d pos = transform.getPosition();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        Map<UUID, ExplorationWalkData> tracker = plugin.getExplorationWalkTracker();
        ExplorationWalkData data = tracker.computeIfAbsent(playerId, uuid -> new ExplorationWalkData(x, y, z, 0.0));

        if (data.isFirstTick()) {
            data.setLast(x, y, z);
            return;
        }

        double dx = x - data.getLastX();
        double dy = y - data.getLastY();
        double dz = z - data.getLastZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        data.setLast(x, y, z);

        // Cap per-tick distance to avoid teleports giving huge EXP
        double maxPerTick = 20.0;
        if (distance > maxPerTick) {
            distance = maxPerTick;
        }

        data.addDistance(distance);

        ExperienceService expService = plugin.getExperienceService();
        while (data.getAccumulatedDistance() >= STEPS_PER_GRANT) {
            data.addDistance(-STEPS_PER_GRANT);
            expService.grantExp(playerId, mapping.getCategoryId(), mapping.getExp(), player);
        }
    }

    /**
     * Per-player state: last position and accumulated walk distance.
     */
    public static final class ExplorationWalkData {
        private double lastX;
        private double lastY;
        private double lastZ;
        private double accumulatedDistance;
        private boolean firstTick = true;

        public ExplorationWalkData(double lastX, double lastY, double lastZ, double accumulatedDistance) {
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastZ = lastZ;
            this.accumulatedDistance = accumulatedDistance;
        }

        public boolean isFirstTick() {
            return firstTick;
        }

        public void setLast(double x, double y, double z) {
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.firstTick = false;
        }

        public double getLastX() { return lastX; }
        public double getLastY() { return lastY; }
        public double getLastZ() { return lastZ; }

        public void addDistance(double d) {
            this.accumulatedDistance += d;
        }

        public double getAccumulatedDistance() {
            return accumulatedDistance;
        }
    }
}
