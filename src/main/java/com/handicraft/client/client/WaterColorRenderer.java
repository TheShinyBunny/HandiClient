/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client;

import com.handicraft.client.block.ColoredWaterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.ColorResolver;

import java.util.HashMap;
import java.util.Map;

public class WaterColorRenderer {

    public static final Map<DyeColor,Integer> COLOR_MAP = new HashMap<DyeColor, Integer>(){{
        put(DyeColor.WHITE, 0xffffff);
        put(DyeColor.ORANGE, 0xeba954);
        put(DyeColor.MAGENTA, 0xe530d6);
        put(DyeColor.LIGHT_BLUE, 0x3AAFD9);
        put(DyeColor.YELLOW, 0xfce946);
        put(DyeColor.LIME, 0x46fc60);
        put(DyeColor.PINK,0xfd79f5);
        put(DyeColor.GRAY,0x393939);
        put(DyeColor.LIGHT_GRAY,0x9a9a9a);
        put(DyeColor.CYAN,0x87fff8);
        put(DyeColor.PURPLE,0xae33fd);
        put(DyeColor.BLUE,0x000cff);
        put(DyeColor.BROWN,0x663306);
        put(DyeColor.GREEN,0x378428);
        put(DyeColor.RED,0xff0030);
        put(DyeColor.BLACK,0x1b1b1b);
    }};

    public static int getColor(BlockPos pos, World world, ColorResolver colorResolver) {
        int i = MinecraftClient.getInstance().options.biomeBlendRadius;
        BlockState center = world.getBlockState(pos);
        if (i == 0) {
            if (center.getBlock() instanceof ColoredWaterBlock && colorResolver == BiomeColors.WATER_COLOR) {
                return COLOR_MAP.get(((ColoredWaterBlock) center.getBlock()).getColor());
            }
            return colorResolver.getColor(world.getBiome(pos), pos.getX(), pos.getZ());
        } else {
            int j = (i * 2 + 1) * (i * 2 + 1);
            int k = 0;
            int l = 0;
            int m = 0;
            CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(pos.getX() - i, pos.getY(), pos.getZ() - i, pos.getX() + i, pos.getY(), pos.getZ() + i);


            int centerColor = center.getBlock() instanceof ColoredWaterBlock ? COLOR_MAP.get(((ColoredWaterBlock) center.getBlock()).getColor()) : 0;

            int n;
            for (BlockPos.Mutable mutable = new BlockPos.Mutable(); cuboidBlockIterator.step(); m += n & 255) {
                mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
                BlockState state = world.getBlockState(mutable);
                if (state.getBlock() instanceof ColoredWaterBlock && colorResolver == BiomeColors.WATER_COLOR) {
                    n = COLOR_MAP.get(((ColoredWaterBlock) state.getBlock()).getColor());
                } else if (state.getFluidState().isIn(FluidTags.WATER) || centerColor == 0) {
                    n = colorResolver.getColor(world.getBiome(mutable), mutable.getX(), mutable.getZ());
                } else {
                    n = centerColor;
                }
                k += (n & 16711680) >> 16;
                l += (n & '\uff00') >> 8;
            }

            /*if (coloredWaterCount > i * 10 && center.getBlock() instanceof ColoredWaterBlock) {
                return ((ColoredWaterBlock) center.getBlock()).getColor().getSignColor();
            }*/

            return (k / j & 255) << 16 | (l / j & 255) << 8 | m / j & 255;
        }
    }
}
