package dev.seyon.leveling.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Quest requirement for an item
 */
public class QuestItemRequirement {
    private String item_id;
    private int amount;

    public String getItemId() {
        return item_id;
    }

    public void setItemId(String item_id) {
        this.item_id = item_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
