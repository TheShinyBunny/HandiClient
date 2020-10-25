/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.gen.structure;

import com.google.common.collect.ImmutableList;
import com.handicraft.client.CommonMod;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.List;

public class DarkFortressStructure extends StructureFeature<DefaultFeatureConfig> {
    private static final List<SpawnSettings.SpawnEntry> SPAWN_LIST = ImmutableList.of(
            new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON,100,2,4),
            new SpawnSettings.SpawnEntry(EntityType.RAVAGER,50,1,1),
            new SpawnSettings.SpawnEntry(CommonMod.DARK_BLAZE,90,2,5),
            new SpawnSettings.SpawnEntry(EntityType.SPIDER,40,2,3),
            new SpawnSettings.SpawnEntry(CommonMod.DARK_PILLAGER,100,2,3)
    );

    public DarkFortressStructure() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public List<SpawnSettings.SpawnEntry> getMonsterSpawns() {
        return SPAWN_LIST;
    }

    private static class Start extends StructureStart<DefaultFeatureConfig> {

        public Start(StructureFeature<DefaultFeatureConfig> feature, int chunkX, int chunkZ, BlockBox box, int references, long seed) {
            super(feature, chunkX, chunkZ, box, references, seed);
        }

        @Override
        public void init(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, StructureManager manager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig config) {
            DarkFortressGenerator.Start start = new DarkFortressGenerator.Start(this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.children.add(start);
            start.fillOpenings(start, this.children, this.random);
            List<StructurePiece> list = start.field_14505;

            while(!list.isEmpty()) {
                int k = this.random.nextInt(list.size());
                StructurePiece structurePiece = list.remove(k);
                structurePiece.fillOpenings(start, this.children, this.random);
            }

            this.setBoundingBoxFromChildren();
            this.randomUpwardTranslation(this.random, 90, 110);
        }
    }
}
