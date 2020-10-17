/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import com.google.common.collect.Lists;
import com.handicraft.client.mixin.GroupEntryAccessor;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.LootPoolEntry;

import java.util.List;

public class GroupEntryBuilder extends LootPoolEntry.Builder<GroupEntryBuilder> {

    private final List<LootPoolEntry> children = Lists.newArrayList();

    public GroupEntryBuilder(LootPoolEntry.Builder<?>... children) {
        for (LootPoolEntry.Builder<?> e : children) {
            this.children.add(e.build());
        }
    }

    public static LootPoolEntry.Builder<?> create(LootPoolEntry.Builder<?>... children) {
        return new GroupEntryBuilder(children);
    }

    public GroupEntryBuilder and(LootPoolEntry.Builder<?> builder) {
        this.children.add(builder.build());
        return this;
    }

    @Override
    protected GroupEntryBuilder getThisBuilder() {
        return this;
    }

    @Override
    public LootPoolEntry build() {
        return GroupEntryAccessor.createGroupEntry(this.children.toArray(new LootPoolEntry[0]),getConditions());
    }
}
