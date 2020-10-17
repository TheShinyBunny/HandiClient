/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class DarkOreBlock extends OreBlock {
    private final int minXP;
    private final int maxXP;

    public DarkOreBlock(int minXP, int maxXP) {
        super(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES,3).strength(3f,3f));
        this.minXP = minXP;
        this.maxXP = maxXP;
    }

    @Override
    protected int getExperienceWhenMined(Random random) {
        return MathHelper.nextInt(random,minXP,maxXP);
    }
}
