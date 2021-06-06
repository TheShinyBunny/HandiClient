/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import com.handicraft.client.CommonMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DarkPortalBlock extends Block {

    public DarkPortalBlock() {
        super(FabricBlockSettings.of(Material.PORTAL).noCollision().ticksRandomly().sounds(BlockSoundGroup.GLASS).dropsNothing().lightLevel(15).strength(-1.0F, 3600000.0F));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
            if (!world.isClient) {
                if (world.getRegistryKey() == CommonMod.DARKNESS_KEY) {
                    entity.moveToWorld(world.getServer().getOverworld());
                } else {
                    ServerWorld dest = world.getServer().getWorld(CommonMod.DARKNESS_KEY);
                    if (dest == null) {
                        System.out.println("NO DARKNESS DIMENSION!");
                    } else {
                        entity.moveToWorld(dest);
                    }
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }
}
