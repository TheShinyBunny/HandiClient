/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import com.handicraft.client.client.ClientMod;
import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.client.screen.NewTitleScreen;
import com.handicraft.client.rewards.CollectibleReward;
import com.handicraft.client.rewards.Reward;
import com.handicraft.client.util.HandiUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

import java.util.*;
import java.util.stream.Collectors;

public class ClientCollectibleCache {

    private static List<Collectible> owned = new ArrayList<>();
    private static Map<CollectibleType<?>,Collectible> selected = new HashMap<>();
    private static Reward newClaim;
    private static Collectible newSelected;
    private static CollectibleType<?> newSelectedType;
    private static List<Reward> claimed = new ArrayList<>();
    private static int passLevel = 0;

    public static void fillPacket(PacketByteBuf buf) {
        if (newClaim == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeVarInt(Reward.REGISTRY.getRawId(newClaim));
        }
        if (newSelectedType == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeVarInt(newSelectedType.getIndex());
            if (newSelected == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeVarInt(Collectibles.REGISTRY.getRawId(newSelected));
            }
        }

        newClaim = null;
        newSelected = null;
        newSelectedType = null;
    }

    public static void readResponse(PacketByteBuf buf) {
        System.out.println("collectible response");
        owned = HandiUtils.readRegistryList(buf,Collectibles.REGISTRY);
        claimed = HandiUtils.readRegistryList(buf,Reward.REGISTRY);
        selected = HandiUtils.readMap(buf,b->CollectibleType.get(b.readString()),b->Collectibles.REGISTRY.get(b.readVarInt()));
        passLevel = buf.readVarInt();
        if (HandiPassScreen.CURRENT != null) {
            HandiPassScreen.CURRENT.reload();
        }
        if (NewTitleScreen.CURRENT != null) {
            NewTitleScreen.CURRENT.reload();
        }
    }

    public static void claimOffline(CollectibleReward<?> reward) {
        claimed.add(reward);
        newClaim = reward;
        ClientMod.pingServer();
    }

    public static void claim(Reward reward) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Reward.REGISTRY.getRawId(reward));
        ClientSidePacketRegistry.INSTANCE.sendToServer(PlayerCollectibles.CLAIM_REWARD,buf);
    }

    public static boolean needsClaim(Reward r) {
        return !claimed.contains(r) && r.isObtainable(passLevel);
    }

    public static boolean wasClaimed(Reward r) {
        return claimed.contains(r);
    }

    public static int getPassLevel() {
        return passLevel;
    }

    public static List<Collectible> getOwned(CollectibleType<?> t) {
        return owned.stream().filter(o->o.getType() == t).collect(Collectors.toList());
    }

    public static boolean isSelected(Collectible collectible) {
        return selected.get(collectible.getType()) == collectible;
    }

    public static void selectOffline(CollectibleType<?> t, Collectible c) {
        select(t,c);
        newSelected = c;
        newSelectedType = t;
        ClientMod.pingServer();
    }

    public static void select(CollectibleType<?> t, Collectible c) {
        if (c == null) {
            selected.remove(t);
        } else {
            selected.put(t, c);
        }
    }

    public static boolean isNoneSelected(CollectibleType<?> t) {
        return selected.get(t) == null;
    }

    public static <T extends Collectible> T getSelected(CollectibleType<T> type) {
        Collectible c = selected.get(type);
        return c == null ? null : c.getType() == type ? (T)c : null;
    }

    public static List<Reward> getNextRewards() {
        List<Reward> current = Reward.getByLevel(passLevel);
        for (Reward r : current) {
            if (needsClaim(r)) return current;
        }
        return Reward.getNext(passLevel);
    }

    public static int unclaimedCount() {
        return (int) Reward.REGISTRY.stream().filter(r->r.isObtainable(passLevel) && !wasClaimed(r)).count();
    }

    public static void clear() {
        claimed.clear();
        selected.clear();
        passLevel = 0;
        owned.clear();
    }
}
