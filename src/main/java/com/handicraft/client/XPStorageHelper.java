/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public class XPStorageHelper {

    public static void update(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler h, PacketByteBuf buf, PacketSender sender) {
        int points = buf.readVarInt();
        if (points > 0) {
            if (player.experienceLevel >= 1) {
                player.addExperience(-points);
                PlayerPersistentData.of(player).storedXP += points;
            }
        } else if (points < 0) {
            PlayerPersistentData data = PlayerPersistentData.of(player);
            if (data.storedXP >= 1) {
                data.storedXP += points;
                int amount = -points;
                Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
                if (entry != null) {
                    ItemStack stack = entry.getValue();
                    if (!stack.isEmpty() && stack.isDamaged()) {
                        int i = Math.min(amount * 2, stack.getDamage());
                        amount -= i / 2;
                        stack.setDamage(stack.getDamage() - i);
                    }
                }

                if (amount > 0) {
                    player.addExperience(amount);
                }
            }
        }
    }
}
