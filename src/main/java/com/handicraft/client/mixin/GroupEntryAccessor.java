/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GroupEntry.class)
public interface GroupEntryAccessor {
    @Invoker
    static GroupEntry createGroupEntry(LootPoolEntry[] lootPoolEntries, LootCondition[] lootConditions) {
        throw new UnsupportedOperationException();
    }
}
