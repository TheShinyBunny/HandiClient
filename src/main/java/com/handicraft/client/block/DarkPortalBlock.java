/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Random;

public class DarkPortalBlock extends NetherPortalBlock {
    public DarkPortalBlock() {
        super(FabricBlockSettings.copyOf(Blocks.NETHER_PORTAL).dropsNothing().lightLevel(15).strength(-1.0F, 3600000.0F));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
            if (!world.isClient) {
                entity.moveToWorld(world.getServer().getWorld(RegistryKey.of(Registry.DIMENSION,new Identifier("handicraft:darkness"))));
            }
        }
    }
}
