/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.enchantments;

import com.handicraft.client.block.HeatedObsidianBlock;
import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HeatWalkerEnchantment extends Enchantment {
    public HeatWalkerEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public int getMinPower(int level) {
        return level * 8;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 15;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static void heatLava(LivingEntity entity, World world, BlockPos blockPos, int level) {
        BlockState heated = ModBlocks.HEATED_OBSIDIAN.getDefaultState();
        float f = (float)Math.min(16, 2 + level);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos pos : BlockPos.iterate(blockPos.add(-f, -1, -f), blockPos.add(f, -2, f))) {
            if (pos.isWithinDistance(entity.getPos(), f)) {
                mutable.set(pos.getX(), pos.getY() + 1, pos.getZ());
                if (world.getBlockState(mutable.down()).isOf(ModBlocks.HEATED_OBSIDIAN)) {
                    world.setBlockState(mutable.down(),ModBlocks.HEATED_OBSIDIAN.getDefaultState().with(HeatedObsidianBlock.AGE,0));
                } else {
                    BlockState above = world.getBlockState(mutable);
                    if (above.isAir()) {
                        BlockState state = world.getBlockState(pos);
                        if (state.getMaterial() == Material.LAVA && state.get(FluidBlock.LEVEL) == 0 && heated.canPlaceAt(world, pos) && world.canPlace(heated, pos, ShapeContext.absent())) {
                            world.setBlockState(pos, heated);
                            world.getBlockTickScheduler().schedule(pos, ModBlocks.HEATED_OBSIDIAN, MathHelper.nextInt(entity.getRandom(), 60, 120));
                        }
                    }
                }
            }
        }
    }
}
