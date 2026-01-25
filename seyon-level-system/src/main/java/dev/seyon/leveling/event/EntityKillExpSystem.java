package dev.seyon.leveling.event;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * OnDeathSystem: when an entity dies and the killer is a player, grant Combat Melee or Combat Ranged EXP.
 * - Ranged kill (ProjectileSource): action "kill_enemy_ranged" -> combat_ranged
 * - Melee kill (EntitySource): action "kill_enemy_melee" -> combat_melee
 */
public class EntityKillExpSystem extends DeathSystems.OnDeathSystem {

    private static final String ACTION_KILL_MELEE = "kill_enemy_melee";
    private static final String ACTION_KILL_RANGED = "kill_enemy_ranged";

    private final SeyonLevelSystemPlugin plugin;

    public EntityKillExpSystem(SeyonLevelSystemPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component,
                                  @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Damage deathInfo = component.getDeathInfo();
        if (deathInfo == null) {
            return;
        }

        Damage.Source source = deathInfo.getSource();
        if (!(source instanceof Damage.EntitySource entitySource)) {
            return;
        }

        Ref<EntityStore> killerRef = entitySource.getRef();
        Player killer = store.getComponent(killerRef, Player.getComponentType());
        if (killer == null) {
            return;
        }

        String actionId = source instanceof Damage.ProjectileSource ? ACTION_KILL_RANGED : ACTION_KILL_MELEE;

        ActionRegistryService actionRegistry = plugin.getActionRegistryService();
        if (!actionRegistry.hasAction(actionId)) {
            return;
        }

        ActionRegistryService.ActionMapping mapping = actionRegistry.getActionMapping(actionId);
        if (mapping == null) {
            return;
        }

        java.util.UUID playerId = dev.seyon.core.PlayerUtils.getPlayerUUID(killer);
        if (playerId == null) {
            return;
        }

        ExperienceService expService = plugin.getExperienceService();
        expService.grantExp(playerId, mapping.getCategoryId(), mapping.getExp(), killer);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
