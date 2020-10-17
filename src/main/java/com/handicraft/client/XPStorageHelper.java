/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

import java.util.Map;

public class XPStorageHelper {

    public static void update(PacketContext ctx, PacketByteBuf buf) {
        int points = buf.readVarInt();
        if (points > 0) {
            if (ctx.getPlayer().experienceLevel >= 1) {
                ctx.getPlayer().addExperience(-points);
                PlayerPersistentData.of(ctx.getPlayer()).storedXP += points;
            }
        } else if (points < 0) {
            PlayerPersistentData data = PlayerPersistentData.of(ctx.getPlayer());
            if (data.storedXP >= 1) {
                data.storedXP += points;
                int amount = -points;
                Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, ctx.getPlayer(), ItemStack::isDamaged);
                if (entry != null) {
                    ItemStack stack = entry.getValue();
                    if (!stack.isEmpty() && stack.isDamaged()) {
                        int i = Math.min(amount * 2, stack.getDamage());
                        amount -= i / 2;
                        stack.setDamage(stack.getDamage() - i);
                    }
                }

                if (amount > 0) {
                    ctx.getPlayer().addExperience(amount);
                }
            }
        }
    }
}
