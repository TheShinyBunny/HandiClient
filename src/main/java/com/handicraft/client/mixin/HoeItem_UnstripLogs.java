/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(HoeItem.class)
public class HoeItem_UnstripLogs {

    private static final Map<Block,Block> UNTILL_MAP = new ImmutableMap.Builder<Block,Block>().put(Blocks.STRIPPED_OAK_WOOD, Blocks.OAK_WOOD).put(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_LOG).put(Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.DARK_OAK_WOOD).put(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_LOG).put(Blocks.STRIPPED_ACACIA_WOOD, Blocks.ACACIA_WOOD).put(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_LOG).put(Blocks.STRIPPED_BIRCH_WOOD, Blocks.BIRCH_WOOD).put(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_LOG).put(Blocks.STRIPPED_JUNGLE_WOOD, Blocks.JUNGLE_WOOD).put(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_LOG).put(Blocks.STRIPPED_SPRUCE_WOOD, Blocks.SPRUCE_WOOD).put(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_LOG).put(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_STEM).put(Blocks.STRIPPED_WARPED_HYPHAE, Blocks.WARPED_HYPHAE).put(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_STEM).put(Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.CRIMSON_HYPHAE).build();


    @Inject(method = "useOnBlock",at = @At(value = "HEAD"),cancellable = true)
    private void customTillables(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockState clicked = world.getBlockState(context.getBlockPos());
        Block b = UNTILL_MAP.get(clicked.getBlock());
        if (b != null) {
            PlayerEntity playerEntity = context.getPlayer();
            world.playSound(playerEntity, context.getBlockPos(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient) {
                world.setBlockState(context.getBlockPos(), b.getDefaultState().with(PillarBlock.AXIS, clicked.get(PillarBlock.AXIS)), 11);
                if (playerEntity != null) {
                    context.getStack().damage(1, (LivingEntity)playerEntity, (p) -> {
                        p.sendToolBreakStatus(context.getHand());
                    });
                }
            }
            cir.setReturnValue(ActionResult.success(world.isClient));
        }
    }

}
