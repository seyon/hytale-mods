package dev.seyon.leveling.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Milestone quest configuration
 */
public class MilestoneQuestConfig {
    private String type = "simple_talk"; // simple_talk, item_collection, both
    private String npc_id;
    private String dialog_key;
    private List<QuestItemRequirement> required_items = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNpcId() {
        return npc_id;
    }

    public void setNpcId(String npc_id) {
        this.npc_id = npc_id;
    }

    public String getDialogKey() {
        return dialog_key;
    }

    public void setDialogKey(String dialog_key) {
        this.dialog_key = dialog_key;
    }

    public List<QuestItemRequirement> getRequiredItems() {
        return required_items;
    }

    public void setRequiredItems(List<QuestItemRequirement> required_items) {
        this.required_items = required_items;
    }

    /** Merge from loaded: non-null from wins. */
    public void mergeFrom(MilestoneQuestConfig from) {
        if (from == null) return;
        if (from.type != null) this.type = from.type;
        if (from.npc_id != null) this.npc_id = from.npc_id;
        if (from.dialog_key != null) this.dialog_key = from.dialog_key;
        if (from.required_items != null && !from.required_items.isEmpty()) {
            this.required_items = new ArrayList<>(from.required_items);
        }
    }
}
