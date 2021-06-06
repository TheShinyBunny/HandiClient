/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public class ItemTagsData extends HandiTagsData<Item> {
    private final HandiTagsData<Block> blockTags;

    protected ItemTagsData(DataGenerator root, HandiTagsData<Block> blockTags, Consumer<ItemTagsData> generator) {
        super(root, Registry.ITEM, "items", gen->generator.accept((ItemTagsData)gen));
        this.blockTags = blockTags;
    }

    public void copy(Tag.Identified<Block> from, Tag.Identified<Item> to) {
        Tag.Builder builder = getTagBuilder(to);
        Tag.Builder builder2 = blockTags.getBuilder(from);
        builder2.streamEntries().forEach(builder::add);
    }
}
