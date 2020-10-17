/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.challenge.objectives.Objectives;
import com.handicraft.client.fluid.ColoredWaterFluid;
import com.handicraft.client.fluid.ModFluids;
import com.handicraft.client.mixin.client.ClientWorld_CalculateColors;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ColoredWaterBlock extends FluidBlock {
    private DyeColor color;

    protected ColoredWaterBlock(ColoredWaterFluid fluid) {
        super(fluid,FabricBlockSettings.of(Material.WATER,fluid.getColor()).noCollision().strength(100).dropsNothing());
        this.color = fluid.getColor();
    }

    public static void registerAll() {
        for (DyeColor color : DyeColor.values()) {
            ModBlocks.COLORED_WATER_BLOCK_MAP.put(color,Registry.register(Registry.BLOCK,new Identifier(color.getName() + "_stained_water"),new ColoredWaterBlock(ModFluids.getColoredStill(color))));
        }
    }

    public static boolean usedColor(PlayerEntity player, World world, DyeColor color) {
        float f = player.pitch;
        float g = player.yaw;
        Vec3d vec3d = player.getCameraPosVec(1.0F);
        float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
        float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
        float j = -MathHelper.cos(-f * 0.017453292F);
        float k = MathHelper.sin(-f * 0.017453292F);
        float l = i * j;
        float n = h * j;
        BlockState waterState = ModBlocks.COLORED_WATER_BLOCK_MAP.get(color).getDefaultState();
        Vec3d vec3d2 = vec3d.add((double)l * 5.0D, (double)k * 5.0D, (double)n * 5.0D);
        BlockHitResult res = world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, player));
        if (res.getType() == HitResult.Type.BLOCK) {
            FluidState hitFluid = world.getFluidState(res.getBlockPos());
            if (hitFluid.isEmpty() || !hitFluid.isIn(FluidTags.WATER)) return false;
            if (!world.isClient) {
                Objectives.PAINT_WATER.trigger(player,1);
            }
            boolean painted = false;
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos pos = res.getBlockPos().add(x, y, z);
                        if (res.getBlockPos().getSquaredDistance(pos) <= 5) {
                            FluidState fs = world.getFluidState(pos);
                            BlockState st = world.getBlockState(pos);
                            if (fs.isIn(FluidTags.WATER) && fs.isStill() && (!(st.getBlock() instanceof Waterloggable)) && (!(st.getBlock() instanceof ColoredWaterBlock) || ((ColoredWaterBlock) st.getBlock()).getColor() != color)) {
                                if (color == DyeColor.LIGHT_BLUE) {
                                    if (st.getBlock() instanceof ColoredWaterBlock) {
                                        painted = true;
                                        world.setBlockState(pos, Blocks.WATER.getDefaultState());
                                    }
                                } else {
                                    painted = true;
                                    world.setBlockState(pos, waterState);
                                }
                            }
                        }
                    }
                }
            }
            return painted;
        }
        return false;
    }

    public static DyeColor getColor(BlockState state) {
        if (state.getBlock() instanceof ColoredWaterBlock) {
            return ((ColoredWaterBlock) state.getBlock()).color;
        }
        return DyeColor.WHITE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public DyeColor getColor() {
        return color;
    }
}
