package dev.seyon.leveling.event;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.CraftRecipeEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.service.ActionRegistryService;
import dev.seyon.leveling.service.ExperienceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ECS system: on CraftRecipeEvent.Post, grant Crafting EXP when a player crafts an item.
 * Action IDs: "craft_item" (generic, exp * quantity) or "craft_&lt;recipeId&gt;" (per-recipe, exp * quantity).
 */
public class CraftRecipeExpSystem extends EntityEventSystem<EntityStore, CraftRecipeEvent.Post> {

    private static final String ACTION_CRAFT_ITEM = "craft_item";

    private final SeyonLevelSystemPlugin plugin;

    public CraftRecipeExpSystem(SeyonLevelSystemPlugin plugin) {
        super(CraftRecipeEvent.Post.class);
        this.plugin = plugin;
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull CraftRecipeEvent.Post event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        int quantity = Math.max(1, event.getQuantity());
        ActionRegistryService actionRegistry = plugin.getActionRegistryService();

        // Try "craft_<recipeId>" first, then fallback to "craft_item"
        String recipeId = event.getCraftedRecipe().getId();
        String actionId = actionRegistry.hasAction("craft_" + recipeId) ? "craft_" + recipeId : ACTION_CRAFT_ITEM;
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

        double exp = mapping.getExp() * quantity;
        ExperienceService expService = plugin.getExperienceService();
        expService.grantExp(playerId, mapping.getCategoryId(), exp, player);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
