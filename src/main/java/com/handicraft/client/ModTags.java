/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModTags {

    public static final Map<Tag.Identified<Block>,List<Block>> blocks = new HashMap<>();
    public static final Map<Tag.Identified<Item>,List<ItemConvertible>> items = new HashMap<>();

    private static <T> Tag.Identified<T> create(String id, Supplier<TagGroup<T>> group) {
        return TagRegistry.create(new Identifier(id),group);
    }

    public static Tag.Identified<Block> block(String id, Block... entries) {
        Tag.Identified<Block> t = create(id,BlockTags::getTagGroup);
        blocks.put(t, Arrays.asList(entries));
        return t;
    }

    public static Tag.Identified<Item> item(String id, ItemConvertible... entries) {
        Tag.Identified<Item> t = create(id,ItemTags::getTagGroup);
        items.put(t, Arrays.asList(entries));
        return t;
    }

}
