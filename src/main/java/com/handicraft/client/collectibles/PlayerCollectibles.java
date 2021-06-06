/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import com.handicraft.client.CommonMod;
import com.handicraft.client.ModSounds;
import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.rewards.CollectibleReward;
import com.handicraft.client.rewards.ItemReward;
import com.handicraft.client.rewards.Reward;
import com.handicraft.client.screen.ItemClaimScreenHandler;
import com.handicraft.client.util.HandiUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerCollectibles {

    public static final Identifier UPDATE_COLLECTIBLES = new Identifier("hcclient:update_collectibles");
    public static final Identifier CLAIM_REWARD = new Identifier("hcclient:claim_reward");
    public static final Identifier SELECT_COLLECTIBLE = new Identifier("hcclient:select_collectible");
    public static final Identifier RESET_CAPES = new Identifier("hcclient:reset_capes");

    private Map<CollectibleType,Collectible> selected;
    private List<Collectible> owned;
    private List<Reward> claimedRewards;
    private int season;
    private int level;

    public PlayerCollectibles() {
        selected = new HashMap<>();
        owned = new ArrayList<>();
        claimedRewards = new ArrayList<>();
        level = 0;
        season = 1;
    }

    public static void give(PlayerEntity player, Collectible c) {
        PlayerCollectibles collectibles = PlayerPersistentData.of(player).collectibles;
        collectibles.add(c);
        collectibles.sendUpdate(player);
    }

    public static PlayerCollectibles of(ServerPlayerEntity player) {
        return PlayerPersistentData.of(player).collectibles;
    }

    public static void claimOffline(UUID id, Reward r) {
        PlayerCollectibles c = load(CommonMod.SERVER.get(),id);
        if (r instanceof CollectibleReward) {
            c.claim(null,r);
        }
        c.save(CommonMod.SERVER.get(),id);
    }

    public static void selectOffline(UUID id, CollectibleType type, @Nullable Collectible collectible) {
        PlayerCollectibles c = load(CommonMod.SERVER.get(),id);
        c.select(null,type,collectible);
        c.save(CommonMod.SERVER.get(),id);
    }

    public void select(@Nullable PlayerEntity player, CollectibleType type, @Nullable Collectible c) {
        if (c == null) {
            selected.remove(type);
        } else if (owns(c)) {
            selected.put(c.getType(),c);
        }
        if (player != null) {
            if (type == CollectibleType.CAPE) {
                for (PlayerEntity p : player.getServer().getPlayerManager().getPlayerList()) {
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(p, RESET_CAPES, new PacketByteBuf(Unpooled.buffer()));
                }
            }
        }
    }

    public void claim(@Nullable PlayerEntity player, Reward r) {
        if (!didClaim(r)) {
            claimedRewards.add(r);
            if (r instanceof CollectibleReward) {
                add(((CollectibleReward<?>) r).getCollectible());
            }
            if (player != null) {
                giveReward(r,player);
                sendUpdate(player);
            }
        }
    }

    private void giveReward(Reward r, PlayerEntity player) {
        if (r instanceof ItemReward) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> new ItemClaimScreenHandler(syncId,inv,new SimpleInventory(((ItemReward) r).getStack()), ScreenHandlerContext.create(player.world,player.getBlockPos())),new TranslatableText("gui.item_claim.title")));
        }
    }

    public boolean didClaim(Reward r) {
        return claimedRewards.contains(r);
    }

    public boolean owns(Collectible c) {
        return owned.contains(c);
    }

    private void add(Collectible collectible) {
        if (!owns(collectible)) {
            owned.add(collectible);
        }
    }

    public List<Collectible> getOwned() {
        return owned;
    }

    public List<? extends Collectible> getOwned(CollectibleType type) {
        return owned.stream().filter(c->c.getType() == type).collect(Collectors.toList());
    }

    public Collectible getSelected(CollectibleType type) {
        return selected.get(type);
    }

    public static PlayerCollectibles load(MinecraftServer server, UUID uuid) {
        PlayerCollectibles c = new PlayerCollectibles();
        File dir = new File(server.getRunDirectory(),"collectibles");
        File f = new File(dir,uuid + ".dat");
        if (!f.exists()) {
            return c;
        }

        try {
            NbtCompound tag = NbtIo.readCompressed(f);
            c.owned = HandiUtils.fromRegistryListTag(tag,"ownedCollectibles",Collectibles.REGISTRY);
            c.claimedRewards = HandiUtils.fromRegistryListTag(tag,"claimed",Reward.REGISTRY);
            NbtCompound sel = tag.getCompound("selectedCollectibles");
            for (String k : sel.getKeys()) {
                c.selected.put(CollectibleType.get(k),Collectibles.REGISTRY.get(new Identifier(sel.getString(k))));
            }
            c.season = tag.contains("season") ? tag.getInt("season") : 1;
            c.level = tag.contains("passLevel") ? tag.getInt("passLevel") : 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (c.season < CommonMod.SEASON) {
            c.level = 0;
            c.claimedRewards.clear();
            c.season = CommonMod.SEASON;
        }
        return c;
    }

    public void save(MinecraftServer server, UUID id) {
        NbtCompound tag = new NbtCompound();
        tag.put("ownedCollectibles", HandiUtils.toRegistryListTag(owned,Collectibles.REGISTRY));
        tag.put("claimed",HandiUtils.toRegistryListTag(claimedRewards,Reward.REGISTRY));
        NbtCompound sel = new NbtCompound();
        for (Map.Entry<CollectibleType,Collectible> e : selected.entrySet()) {
            sel.putString(e.getKey().getId(),e.getValue().getId().toString());
        }
        tag.put("selectedCollectibles",sel);
        tag.putInt("passLevel",level);
        tag.putInt("season",season);
        File dir = new File(server.getRunDirectory(),"collectibles");
        dir.mkdirs();
        File f = new File(dir,id.toString() + ".dat");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            NbtIo.writeCompressed(tag,f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void levelUp(PlayerEntity player) {
        level++;
        sendUpdate(player);
        player.world.playSound(null,player.getBlockPos(),ModSounds.LEVEL_UP, SoundCategory.PLAYERS,1,1);
        player.getServer().getPlayerManager().broadcastChatMessage(new TranslatableText("collectibles.level_up",player.getName(),level), MessageType.CHAT,player.getUuid());
    }

    public void sendUpdate(PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writePacket(buf);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,UPDATE_COLLECTIBLES,buf);
    }

    public void writePacket(PacketByteBuf buf) {
        HandiUtils.writeRegistryList(buf,owned, Collectibles.REGISTRY);
        HandiUtils.writeRegistryList(buf,claimedRewards,Reward.REGISTRY);
        HandiUtils.writeMap(buf,selected, PacketByteBuf::writeEnumConstant,(b, c)->b.writeVarInt(Collectibles.REGISTRY.getRawId(c)));
        buf.writeVarInt(level);
    }

    public int getLevel() {
        return level;
    }


    public void setLevel(int level) {
        this.level = level;
    }

    public void unclaim(PlayerEntity player, Reward r) {
        if (didClaim(r)) {
            claimedRewards.remove(r);
            if (r instanceof CollectibleReward) {
                Collectible c = ((CollectibleReward<?>) r).getCollectible();
                owned.remove(c);
                if (c.getType() != null) {
                    if (selected.get(c.getType()) == c) {
                        selected.remove(c.getType());
                    }
                }
            }
            sendUpdate(player);
        }
    }

    public void print(PlayerEntity player) {
        player.sendMessage(new LiteralText("===== HANDIPASS ====="),false);
        for (Reward r : Reward.REGISTRY) {
            printReward(r,player);
        }
    }

    public void printReward(Reward r, PlayerEntity player) {
        if (didClaim(r)) {
            player.sendMessage(new LiteralText(r.getLevel() + ": ").formatted(Formatting.YELLOW).append(new LiteralText(r.getName()).formatted(Formatting.GRAY)).append(new LiteralText(" [CLAIMED]").formatted(Formatting.GRAY)),false);
        } else if (r.isObtainable(level)) {
            player.sendMessage(new LiteralText(r.getLevel() + ": ").formatted(Formatting.YELLOW).append(new LiteralText(r.getName()).formatted(Formatting.WHITE)).append(new LiteralText(" [CLAIM]").styled(s->s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/claim " + Reward.REGISTRY.getId(r).getPath()))).formatted(Formatting.GREEN)),false);
        } else {
            player.sendMessage(new LiteralText(r.getLevel() + ": ").formatted(Formatting.YELLOW).append(new LiteralText(r.getName()).formatted(Formatting.RED)),false);
        }
    }
}
