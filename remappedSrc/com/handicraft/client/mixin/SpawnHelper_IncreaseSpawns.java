/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelper_IncreaseSpawns {

    @Shadow
    private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
        return null;
    }

    @Shadow public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner) {}

    @Overwrite
    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, SpawnHelper.Checker checker, SpawnHelper.Runner runner) {
        boolean inFortress = world.getStructureAccessor().getStructureAt(chunk.getPos().getStartPos().add(0,105,0),false,CommonMod.DARK_FORTRESS).hasChildren();
        int times = inFortress ? world.random.nextInt(8) + 3 : 1;
        for (int i = 0; i < times; i++) {
            BlockPos blockPos = inFortress ? getBetterSpawnPos(world,chunk) : getSpawnPos(world, chunk);
            if (blockPos.getY() >= 1) {
                spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
            }
        }
    }

    private static BlockPos getBetterSpawnPos(ServerWorld world, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX() + world.random.nextInt(16);
        int j = chunkPos.getStartZ() + world.random.nextInt(16);
        int y = MathHelper.nextInt(world.random, 90, 120);
        if (world.getStructureAccessor().getStructuresWithChildren(ChunkSectionPos.from(chunkPos,y),CommonMod.DARK_FORTRESS).findAny().isPresent()) {
            return new BlockPos(i,y,j);
        }
        int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = world.random.nextInt(k + 1);
        return new BlockPos(i,l,j);
    }
}
