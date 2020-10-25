/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(StructurePiece.class)
public class StructurePiece_PostProcess {

    @Redirect(method = "addBlock",at = @At(value = "INVOKE",target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean needsPostProcessing(Set<Block> set, Object o) {
        if (o instanceof Block) {
            if (o == ModBlocks.DARK_FENCE) return true;
        }
        return set.contains(o);
    }

}
