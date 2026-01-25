package dev.seyon.leveling.service;

import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.seyon.leveling.config.LevelSystemCategory;

import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Sends in-game notifications when a player gains EXP. Uses Hytale's NotificationUtil
 * (item-pickup style). Icon is taken from the category's notification_icon (item ID).
 *
 * @see <a href="https://hytalemodding.dev/en/docs/guides/plugin/send-notifications">Sending notifications</a>
 */
public class ExpNotificationService {

    /** Fallback item ID when category has no notification_icon. Must be a valid Hytale item ID. */
    private static final String DEFAULT_ICON_ITEM_ID = "Item_Material_Coin";

    public ExpNotificationService() {}

    /**
     * Send an EXP gain notification to the player. No-op if player is offline or category is null.
     *
     * @param playerId  player UUID
     * @param category  category (for display name and notification_icon)
     * @param amount   EXP amount (format: integer if whole, else one decimal)
     */
    public void sendExpGain(UUID playerId, @Nullable LevelSystemCategory category, double amount) {
        if (playerId == null || category == null || amount <= 0) {
            return;
        }

        PlayerRef playerRef = Universe.get().getPlayer(playerId);
        if (playerRef == null) {
            return;
        }

        PacketHandler handler = playerRef.getPacketHandler();
        String displayName = category.getDisplayName() != null ? category.getDisplayName() : category.getId();
        String formattedAmount = amount == (long) amount ? String.valueOf((long) amount) : String.valueOf(amount);
        Message primary = Message.raw("+" + formattedAmount + " " + displayName + " EXP").color("#00FF00");

        String iconItemId = category.getNotificationIcon();
        if (iconItemId == null || iconItemId.isEmpty()) {
            iconItemId = DEFAULT_ICON_ITEM_ID;
        }

        ItemWithAllMetadata itemPacket = toItemPacket(iconItemId);
        NotificationUtil.sendNotification(handler, primary, null, itemPacket);
    }

    /**
     * Creates ItemWithAllMetadata for the notification icon. Returns null if the item ID is invalid
     * (avoids throwing; notification would be sent without icon — NotificationUtil allows null item).
     */
    @Nullable
    private static ItemWithAllMetadata toItemPacket(String itemId) {
        try {
            return new ItemStack(itemId, 1).toPacket();
        } catch (Exception e) {
            return null;
        }
    }
}
