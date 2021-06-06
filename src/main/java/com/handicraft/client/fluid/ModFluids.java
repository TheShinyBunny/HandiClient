/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModFluids {

    private static final Map<DyeColor, ColoredWaterFluid.Flowing> FLOWING_MAP = new HashMap<>();
    private static final Map<DyeColor, ColoredWaterFluid.Still> STILL_MAP = new HashMap<>();


    public static ColoredWaterFluid.Flowing getColoredFlowing(DyeColor color) {
        return FLOWING_MAP.get(color);
    }

    public static Stream<Fluid> getAll() {
        return Stream.concat(STILL_MAP.values().stream(), FLOWING_MAP.values().stream());
    }

    public static ColoredWaterFluid.Still getColoredStill(DyeColor color) {
        return STILL_MAP.get(color);
    }

    public static void register() {
        for (DyeColor color : DyeColor.values()) {
            STILL_MAP.put(color, Registry.register(Registry.FLUID,new Identifier(color.getName() + "_stained_water"),new ColoredWaterFluid.Still(color)));
            FLOWING_MAP.put(color, Registry.register(Registry.FLUID,new Identifier(color.getName() + "_stained_flowing_water"),new ColoredWaterFluid.Flowing(color)));
        }
    }


}
