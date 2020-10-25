/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.server.world.ServerWorld;

public class DarknessSwordItem extends SwordItem {
    public DarknessSwordItem() {
        super(DarknessMaterial.INSTANCE, 3, 10f, new Settings().group(ItemGroup.COMBAT).maxCount(1).fireproof());
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        /*if (!target.world.isClient) {
            ((ServerWorld) target.world).getChunkManager().sendToNearbyPlayers(attacker,new EntityAnimationS2CPacket(target,6));
        }*/
        return super.postHit(stack, target, attacker);
    }
}
