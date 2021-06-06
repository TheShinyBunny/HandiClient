/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class DarkOreBlock extends OreBlock {
    private final int minXP;
    private final int maxXP;

    public DarkOreBlock(int minXP, int maxXP) {
        super(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES,3).strength(3f,3f));
        this.minXP = minXP;
        this.maxXP = maxXP;
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            int i = MathHelper.nextInt(world.random,minXP,maxXP);
            if (i > 0) {
                this.dropExperience(world, pos, i);
            }
        }
    }

}
