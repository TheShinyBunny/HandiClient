/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.screen.EnderChestScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnderChestBlock.class)
public class EnderChest_CustomUI {

    @Overwrite
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        EnderChestInventory enderChestInventory = player.getEnderChestInventory();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (enderChestInventory != null && blockEntity instanceof EnderChestBlockEntity) {
            BlockPos blockPos = pos.up();
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
             return ActionResult.success(world.isClient);
            } else if (world.isClient) {
             return ActionResult.SUCCESS;
            } else {
             EnderChestBlockEntity enderChestBlockEntity = (EnderChestBlockEntity) blockEntity;
             enderChestInventory.setActiveBlockEntity(enderChestBlockEntity);
             player.openHandledScreen(EnderChestScreenHandler.create());
             player.incrementStat(Stats.OPEN_ENDERCHEST);
             PiglinBrain.onGuardedBlockInteracted(player, true);
             return ActionResult.CONSUME;
            }
        } else {
            return ActionResult.success(world.isClient);
        }
    }
}
