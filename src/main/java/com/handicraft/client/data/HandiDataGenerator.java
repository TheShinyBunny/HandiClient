/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import com.handicraft.client.ModTags;
import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.fluid.ModFluids;
import com.handicraft.client.item.ModItems;
import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class HandiDataGenerator {

    public static void run() {
        DataGenerator generator = new DataGenerator(Paths.get("generated"), Collections.emptyList());
        generator.install(new LootTableData(generator));
        generator.install(new RecipeData(generator));
        ModBlocks.Tags.DARK_LOGS.getId();
        ModItems.Tags.DARK_LOGS.getId();
        HandiTagsData<Block> blockTags = new HandiTagsData<>(generator, Registry.BLOCK, "blocks", adder->{
            adder.addAll(ModTags.blocks,b->b);
            adder.make(BlockTags.WOODEN_BUTTONS).add(ModBlocks.DARK_BUTTON);
            adder.make(BlockTags.FENCE_GATES).add(ModBlocks.DARK_FENCE_GATE);
            adder.make(BlockTags.WOODEN_FENCES).add(ModBlocks.DARK_FENCE);
            adder.make(BlockTags.FIRE).add(ModBlocks.GREEN_DARK_FIRE,ModBlocks.PURPLE_DARK_FIRE);
            adder.make(BlockTags.SMALL_FLOWERS).add(ModBlocks.PEONY);
            adder.make(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_PEONY);
            adder.make(BlockTags.LEAVES).add(ModBlocks.DARK_LEAVES);
            adder.make(BlockTags.LOGS_THAT_BURN).addTag(ModBlocks.Tags.DARK_LOGS);
            adder.make(BlockTags.PLANKS).add(ModBlocks.DARK_PLANKS);
            adder.make(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.DARK_PRESSURE_PLATE);
            adder.make(BlockTags.SAPLINGS).add(ModBlocks.DARK_SAPLING);
            adder.make(BlockTags.WOODEN_SLABS).add(ModBlocks.DARK_SLAB);
            adder.make(BlockTags.WOODEN_STAIRS).add(ModBlocks.DARK_STAIRS);
            adder.make(BlockTags.WOODEN_BUTTONS).add(ModBlocks.DARK_BUTTON);
        });
        generator.install(blockTags);
        generator.install(new ItemTagsData(generator, blockTags, adder->{
            adder.addAll(ModTags.items, ItemConvertible::asItem);
            adder.copy(BlockTags.WOODEN_BUTTONS,ItemTags.WOODEN_BUTTONS);
            adder.copy(BlockTags.WOODEN_FENCES,ItemTags.WOODEN_FENCES);
            adder.copy(BlockTags.SMALL_FLOWERS,ItemTags.SMALL_FLOWERS);
            adder.copy(BlockTags.LEAVES,ItemTags.LEAVES);
            adder.copy(BlockTags.LOGS_THAT_BURN,ItemTags.LOGS_THAT_BURN);
            adder.copy(BlockTags.PLANKS,ItemTags.PLANKS);
            adder.copy(BlockTags.SAPLINGS,ItemTags.SAPLINGS);
            adder.copy(BlockTags.WOODEN_SLABS,ItemTags.WOODEN_SLABS);
            adder.copy(BlockTags.WOODEN_STAIRS,ItemTags.WOODEN_STAIRS);
            adder.copy(BlockTags.WOODEN_PRESSURE_PLATES,ItemTags.WOODEN_PRESSURE_PLATES);
        }));
        generator.install(new HandiTagsData<>(generator,Registry.FLUID,"fluids",adder->{
            adder.make(FluidTags.WATER).add(ModFluids.getAll().toArray(Fluid[]::new));
        }));

        try {
            generator.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
