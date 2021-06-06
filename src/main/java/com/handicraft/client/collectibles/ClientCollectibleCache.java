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
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client-side manager of the player's collectibles.
 */
public class ClientCollectibleCache {

    /**
     * A list of the player's owned collectibles
     */
    private static List<Collectible> owned = new ArrayList<>();
    /**
     * A map of the selected collectible for each type. A key mapped to a null value means nothing is selected for that category.
     */
    private static Map<CollectibleType,Collectible> selected = new HashMap<>();
    /**
     * The last claimed reward in the offline lobby. Will be set when the "Claim" button is clicked, and call a server ping immediately after and set it back to null.
     */
    private static Reward newClaim;
    /**
     * Same as {@link #newClaim} but for the locker when selecting a collectible
     */
    private static Collectible newSelected;
    private static CollectibleType newSelectedType;
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
            buf.writeEnumConstant(newSelectedType);
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
        selected = HandiUtils.readMap(buf,b->b.readEnumConstant(CollectibleType.class),b->Collectibles.REGISTRY.get(b.readVarInt()));
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

    public static List<Collectible> getOwned(CollectibleType t) {
        return owned.stream().filter(o->o.getType() == t).collect(Collectors.toList());
    }

    public static boolean isSelected(Collectible collectible) {
        return selected.get(collectible.getType()) == collectible;
    }

    public static void selectOffline(CollectibleType t, Collectible c) {
        select(t,c);
        newSelected = c;
        newSelectedType = t;
        ClientMod.pingServer();
    }

    public static void select(CollectibleType t, Collectible c) {
        if (c == null) {
            selected.remove(t);
        } else {
            selected.put(t, c);
        }
    }

    public static boolean isNoneSelected(CollectibleType t) {
        return selected.get(t) == null;
    }

    public static Collectible getSelected(CollectibleType type) {
        Collectible c = selected.get(type);
        return c == null ? null : c.getType() == type ? c : null;
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
