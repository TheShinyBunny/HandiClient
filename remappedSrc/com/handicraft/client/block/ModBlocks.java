/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.util.BlockItem;
import com.handicraft.client.util.CreativeTab;
import com.handicraft.client.util.Register;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {

    @Register("peony")
    @BlockItem(CreativeTab.DECORATION)
    public static final FlowerBlock PEONY = new FlowerBlock(StatusEffects.GLOWING,20, FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));

    @Register("potted_peony")
    public static final FlowerPotBlock POTTED_PEONY = new FlowerPotBlock(PEONY,FabricBlockSettings.of(Material.SUPPORTED).breakInstantly().nonOpaque());

    @Register("heated_obsidian")
    public static final HeatedObsidianBlock HEATED_OBSIDIAN = new HeatedObsidianBlock();

}
