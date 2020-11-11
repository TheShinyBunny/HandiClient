/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.ModSounds;
import com.handicraft.client.block.entity.NetheriteFurnaceBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class NetheriteFurnaceBlock extends AbstractFurnaceBlock {
    protected NetheriteFurnaceBlock() {
        super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES,3).requiresTool().strength(3.5F).luminance(state->state.get(LIT) ? 13 : 0));
    }

    @Override
    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof NetheriteFurnaceBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory)blockEntity);
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NetheriteFurnaceBlockEntity) {
                ((NetheriteFurnaceBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }

    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NetheriteFurnaceBlockEntity) {
                ItemScatterer.spawn(world, pos, (NetheriteFurnaceBlockEntity)blockEntity);
                ((NetheriteFurnaceBlockEntity)blockEntity).dropAllExperience(world, Vec3d.ofCenter(pos));
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new NetheriteFurnaceBlockEntity();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double x = (double)pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double)pos.getZ() + 0.5D;
            if (random.nextDouble() < 0.1D) {
                world.playSound(x, y, z, ModSounds.NETHERITE_FURNACE_CRACKLES, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(FACING);
            spawnParticle(direction,random,world,x,y,z);
            spawnParticle(direction.rotateYClockwise(),random,world,x,y,z);
            spawnParticle(direction.rotateYCounterclockwise(),random,world,x,y,z);
        }
    }

    private void spawnParticle(Direction dir, Random random, World world, double x, double y, double z) {
        Direction.Axis axis = dir.getAxis();
        double h = random.nextDouble() * 0.6D - 0.3D;
        double i = axis == Direction.Axis.X ? (double)dir.getOffsetX() * 0.52D : h;
        double j = random.nextDouble() * 9.0D / 16.0D;
        double k = axis == Direction.Axis.Z ? (double)dir.getOffsetZ() * 0.52D : h;
        world.addParticle(ParticleTypes.SMOKE, x + i, y + j, z + k, 0.0D, 0.0D, 0.0D);
    }
}
