/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FlowableFluid.class)
public interface FlowableFluidAccessor {
    @Accessor
    static ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> getField_15901() {
        throw new UnsupportedOperationException();
    }
}
