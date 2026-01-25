package dev.seyon.leveling.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.seyon.leveling.SeyonLevelSystemPlugin;
import dev.seyon.leveling.config.LevelSystemCategory;
import dev.seyon.leveling.model.CategoryProgress;
import dev.seyon.leveling.model.PlayerLevelSystemData;
import dev.seyon.core.PlayerUtils;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import java.util.UUID;

/**
 * Main GUI for Level System - categories, progress, Level Up.
 */
public class LevelSystemMainGui extends InteractiveCustomUIPage<LevelSystemMainGui.GuiData> {

    public LevelSystemMainGui(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder,
                     @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/SeyonLevelSystem/SeyonLevelSystem_MainPage.ui");

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        UUID playerId = PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            return;
        }

        SeyonLevelSystemPlugin plugin = SeyonLevelSystemPlugin.getInstance();
        PlayerLevelSystemData data = plugin.getDataService().getPlayerData(playerId);

        // Header
        uiCommandBuilder.set("#Header #PlayerNameLabel.Text", player.getDisplayName());
        int totalSkillPoints = plugin.getCategoryService().getAllCategories().stream()
                .mapToInt(c -> data.getAvailableSkillPoints(c.getId()))
                .sum();
        uiCommandBuilder.set("#Header #TotalLevelLabel.Text", "Total Skill Points: " + totalSkillPoints);

        // Categories
        uiCommandBuilder.clear("#CategoriesContent");
        int i = 0;
        for (LevelSystemCategory category : plugin.getCategoryService().getAllCategories()) {
            uiCommandBuilder.append("#CategoriesContent", "Pages/SeyonLevelSystem/SeyonLevelSystem_CategoryCard.ui");

            String cardPath = "#CategoriesContent[" + i + "]";
            CategoryProgress progress = data.getOrCreateCategoryProgress(category.getId());

            uiCommandBuilder.set(cardPath + " #CategoryName.Text", category.getDisplayName());
            uiCommandBuilder.set(cardPath + " #CategoryLevel.Text", "Level " + progress.getCurrentLevel());

            double expNext = progress.getExpForNextLevel();
            int pct = (expNext > 0) ? Math.min(100, (int) ((progress.getCurrentExp() / expNext) * 100)) : 0;
            uiCommandBuilder.set(cardPath + " #ExpBar.Width", String.valueOf(pct));

            uiCommandBuilder.set(cardPath + " #ExpLabel.Text", String.format("%.0f / %.0f EXP", progress.getCurrentExp(), expNext));
            uiCommandBuilder.set(cardPath + " #SkillPointsLabel.Text", "Skill Points: " + data.getAvailableSkillPoints(category.getId()));
            uiCommandBuilder.set(cardPath + " #PendingLevelsLabel.Text", progress.getPendingLevelUps() > 0 ? (progress.getPendingLevelUps() + " Level Up(s) pending!") : "");

            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, cardPath + " #LevelUpButton", EventData.of("LevelUp", category.getId()));
            i++;
        }

        // Footer buttons
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Content #Footer #RefreshButton", EventData.of("Button", "Refresh"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Content #Footer #CloseButton", EventData.of("Button", "Close"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("Button", "Close"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            this.sendUpdate();
            return;
        }

        UUID playerId = PlayerUtils.getPlayerUUID(player);
        if (playerId == null) {
            this.sendUpdate();
            return;
        }

        SeyonLevelSystemPlugin plugin = SeyonLevelSystemPlugin.getInstance();

        if (data.levelUp != null) {
            plugin.getExperienceService().processLevelUp(player, data.levelUp);
            // Rebuild to reflect new state
            UICommandBuilder cb = new UICommandBuilder();
            UIEventBuilder eb = new UIEventBuilder();
            this.build(ref, cb, eb, store);
            this.sendUpdate(cb, eb, false);
            return;
        }

        if (data.button != null) {
            if ("Refresh".equals(data.button)) {
                UICommandBuilder cb = new UICommandBuilder();
                UIEventBuilder eb = new UIEventBuilder();
                this.build(ref, cb, eb, store);
                this.sendUpdate(cb, eb, false);
                return;
            }
            if ("Close".equals(data.button)) {
                // Dismiss is handled by CanDismiss; no extra call needed
            }
        }

        this.sendUpdate();
    }

    public static class GuiData {
        public String button;
        public String levelUp;

        public static final BuilderCodec<GuiData> CODEC = BuilderCodec.<GuiData>builder(GuiData.class, GuiData::new)
                .addField(new KeyedCodec<>("Button", Codec.STRING), (d, s) -> d.button = s, d -> d.button)
                .addField(new KeyedCodec<>("LevelUp", Codec.STRING), (d, s) -> d.levelUp = s, d -> d.levelUp)
                .build();
    }
}
