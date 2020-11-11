/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import com.handicraft.client.block.ModBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CashRegisterItem extends BlockItem {
    public CashRegisterItem() {
        super(ModBlocks.CASH_REGISTER, new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS).fireproof());
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.getOrCreateTag().putUuid("Owner",player.getUuid());
        stack.getOrCreateTag().putString("OwnerName",player.getEntityName());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.hasTag() && stack.getTag().contains("OwnerName", NbtType.STRING) && world != null) {
            tooltip.add(new TranslatableText("item.cash_register.owner",stack.getTag().getString("OwnerName")));
        }
    }
}
