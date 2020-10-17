/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.block.ModBlocks;
import com.handicraft.client.data.LootTableData;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(BlockLootTableGenerator.class)
public class BlockLootGenerator_Modify {

    @Shadow @Final private Map<Identifier, LootTable.Builder> lootTables;

    @Inject(method = "accept",at = @At("HEAD"))
    private void addBlocks(BiConsumer<Identifier, LootTable.Builder> biConsumer, CallbackInfo ci) {
        LootTableData.blocks((block, builder) -> {
            this.lootTables.put(block.getLootTableId(),builder);
        });
        LootTable.Builder b = lootTables.get(ModBlocks.PEONY.getLootTableId());
        if (b == null) {
            System.out.println("B IS NULL");
        }
    }

}
