/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelper_IncreaseSpawns {

    @Shadow
    private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
        return null;
    }

    @Shadow public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner) {}

    @Overwrite
    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, SpawnHelper.Checker checker, SpawnHelper.Runner runner) {
        boolean inFortress = world.getStructureAccessor().getStructureAt(chunk.getPos().getStartPos().add(8,70,8),true, CommonMod.DARK_FORTRESS).hasChildren();
        int times = inFortress ? world.random.nextInt(5) + 3 : 1;
        for (int i = 0; i < times; i++) {
            BlockPos blockPos = getSpawnPos(world, chunk);
            if (blockPos.getY() >= 1) {
                spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
            }
        }
    }
}
