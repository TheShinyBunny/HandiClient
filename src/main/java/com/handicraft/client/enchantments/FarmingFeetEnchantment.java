/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.enchantments;

import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class FarmingFeetEnchantment extends Enchantment {
    public FarmingFeetEnchantment() {
        super(Rarity.UNCOMMON,EnchantmentTarget.ARMOR_FEET,new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 1;
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
        return 8;
    }

    @Override
    public int getMaxPower(int level) {
        return 23;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    public static void farm(LivingEntity entity) {
        BlockPos pos = entity.getBlockPos().up();
        BlockState state = entity.world.getBlockState(pos);
        if (entity.getBlockStateAtPos().isOf(Blocks.FARMLAND)) {
            if (state.getBlock() instanceof CropBlock) {
                IntProperty ap = state.isOf(Blocks.BEETROOTS) ? BeetrootsBlock.AGE : CropBlock.AGE;
                int max = state.isOf(Blocks.BEETROOTS) ? 3 : 7;
                int age = state.get(ap);
                if (age >= max) {
                    replant(entity.world,pos,state,ap);
                }
            }
        } else if (entity.getBlockStateAtPos().isOf(Blocks.SOUL_SAND)) {
            if (state.isOf(Blocks.NETHER_WART)) {
                int age = state.get(NetherWartBlock.AGE);
                if (age >= 3) {
                    replant(entity.world,pos,state,NetherWartBlock.AGE);
                }
            }
        }
    }

    private static void replant(World world, BlockPos pos, BlockState state, IntProperty ageProp) {
        List<ItemStack> stacks = Block.getDroppedStacks(state, (ServerWorld) world, pos, null);
        for (ItemStack s : stacks) {
            if (s.getItem() instanceof BlockItem) {
                s.decrement(1);
                break;
            }
        }
        stacks.forEach(s->Block.dropStack(world,pos,s));
        world.breakBlock(pos,false);
        world.setBlockState(pos,state.with(ageProp,0));
    }
}
