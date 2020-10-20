/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.sapling;

import com.handicraft.client.CommonMod;
import com.mojang.serialization.DataResult;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class DarkSaplingGenerator extends SaplingGenerator {
    @Override
    protected @Nullable ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        MutableRegistry<ConfiguredFeature<?, ?>> registry = CommonMod.SERVER.get().getRegistryManager().get(Registry.CONFIGURED_FEATURE_WORLDGEN);
        ConfiguredFeature<TreeFeatureConfig,?> feature = (ConfiguredFeature<TreeFeatureConfig, ?>) registry.get(new Identifier("handicraft:dark_tree"));
        if (feature == null) {
            System.out.println("no feature");
        }
        return feature;
    }
}
