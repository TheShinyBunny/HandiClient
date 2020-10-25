/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.mixin;

import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.rewards.Reward;
import com.handicraft.client.util.ExtendedQueryRequest;
import com.handicraft.client.util.HandiUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(QueryRequestC2SPacket.class)
public class QueryRequestPacket_Extend implements ExtendedQueryRequest {

    @Unique
    private UUID uuid;

    @Unique
    private Reward newClaim;

    @Unique
    private CollectibleType<?> newSelectedType;

    @Unique
    private Collectible newSelected;

    @Inject(method = "read",at = @At("HEAD"))
    private void read(PacketByteBuf buf, CallbackInfo ci) {
        this.uuid = buf.readUuid();
        if (buf.readBoolean()) {
            newClaim = Reward.REGISTRY.get(buf.readVarInt());
        } else {
            newClaim = null;
        }
        if (buf.readBoolean()) {
            newSelectedType = CollectibleType.byIndex(buf.readVarInt());
            if (buf.readBoolean()) {
                newSelected = Collectibles.REGISTRY.get(buf.readVarInt());
            } else {
                newSelected = null;
            }
        } else {
            newSelectedType = null;
        }

    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Reward getNewClaim() {
        return newClaim;
    }

    @Override
    public Collectible getNewSelected() {
        return newSelected;
    }

    @Override
    public CollectibleType<?> getNewSelectedType() {
        return newSelectedType;
    }
}
