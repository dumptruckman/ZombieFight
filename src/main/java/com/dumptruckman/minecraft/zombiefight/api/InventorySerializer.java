package com.dumptruckman.minecraft.zombiefight.api;

import com.dumptruckman.minecraft.pluginbase.config.EntrySerializer;
import org.bukkit.inventory.ItemStack;

class InventorySerializer implements EntrySerializer<ItemStack[]> {

    private final int maxSize;

    public InventorySerializer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public ItemStack[] deserialize(Object o) {
        return DataStrings.parseInventory(o.toString(), maxSize);
    }

    @Override
    public Object serialize(ItemStack[] itemStacks) {
        return DataStrings.valueOf(itemStacks);
    }
}
