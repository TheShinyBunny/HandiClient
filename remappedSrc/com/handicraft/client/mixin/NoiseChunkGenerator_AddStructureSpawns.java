/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.CommonMod;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGenerator_AddStructureSpawns {

    @Inject(method = "getEntitySpawnList",at = @At("HEAD"),cancellable = true)
    private void getSpawns(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos, CallbackInfoReturnable<List<SpawnSettings.SpawnEntry>> cir) {
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, true, CommonMod.DARK_FORTRESS).hasChildren()) {
                cir.setReturnValue(CommonMod.DARK_FORTRESS.getMonsterSpawns());
            }
        }
    }

}
