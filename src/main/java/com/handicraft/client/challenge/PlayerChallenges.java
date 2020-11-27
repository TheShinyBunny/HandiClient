/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.CommonMod;
import com.handicraft.client.PlayerPersistentData;
import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tracks challenges of a player on the server
 */
public class PlayerChallenges {

    public static final Identifier COMPLETED = new Identifier("hcclient:completed_challenge");
    public static final Identifier PROGRESS_UPDATE = new Identifier("hcclient:challenge_progress");
    public static final Identifier UPDATE_CHALLENGES = new Identifier("hcclient:update_challenges");

    /**
     * The current challenge progresses of the player
     */
    private List<ChallengeInstance> challenges;
    /**
     * The player owning this instance
     */
    private ServerPlayerEntity player;

    public PlayerChallenges(ServerPlayerEntity player) {
        this.player = player;
        challenges = getChallengesFor(player.getUuid(),player.server);
        if (challenges == null) {
            challenges = new ArrayList<>();
        }
    }

    public void addChallenge(ServerChallenge<?> c) {
        if (get(c) == null) {
            challenges.add(new ChallengeInstance(c, 0));
        }
    }

    /**
     * Triggers a challenge progress update, If the challenge is not done already.
     * Sends a progress / completion notification to the player's client
     * @param challenge The challenge that was progressed
     * @param times The times it was done
     */
    public <T extends ObjectiveInstance> void trigger(ServerChallenge<T> challenge, int times) {
        for (ChallengeInstance i : challenges) {
            if (!i.isCompleted() && i.getChallenge().equals(challenge)) {
                i.trigger(times);
                if (i.isCompleted()) {
                    sendPacket(COMPLETED,i);
                    PlayerPersistentData.of(player).collectibles.levelUp(player);
                } else {
                    sendPacket(PROGRESS_UPDATE,i);
                }
                return;
            }
        }
    }

    public ChallengeInstance get(Challenge<?> c) {
        return challenges.stream().filter(ch->ch.getChallenge().equals(c)).findFirst().orElse(null);
    }

    private void sendPacket(Identifier id, ChallengeInstance challenge) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        challenge.writePacket(buf);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,id,buf);
    }

    /**
     * Updates the challenge progresses with a new challenge list.
     * All challenges without a progress will create a new progress, and any challenge progress that is not present in the passed list will be removed.
     * Will send an update to the player about the current challenges
     * @param c The challenge list to update to
     */
    public void update(List<ServerChallenge<?>> c) {
        List<Challenge<?>> newChallenges = new ArrayList<>();
        for (ServerChallenge<?> ch : c) {
            ChallengeInstance i = get(ch);
            if (i == null) {
                addChallenge(ch);
                newChallenges.add(ch);
            }
        }
        if (newChallenges.size() == 1) {
            player.sendMessage(new TranslatableText("challenges.new.single",newChallenges.get(0).getText()),false);
        } else if (newChallenges.size() > 1) {
            player.sendMessage(new TranslatableText("challenges.new.multiple",newChallenges.size()),false);
        }
        ChallengesManager m = ChallengesManager.get(player.server.getOverworld());
        challenges.removeIf(ci -> m.get(ci.getChallenge().getId()) == null);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(c.size());
        for (ChallengeInstance ch : challenges) {
            ch.writeFullPacket(buf);
        }
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,UPDATE_CHALLENGES,buf);
    }

    public static List<ChallengeInstance> readFrom(ListTag tag) {
        List<ChallengeInstance> challenges = new ArrayList<>();
        for (Tag t : tag) {
            if (t instanceof CompoundTag) {
                ChallengeInstance in = ChallengeInstance.fromNBT((CompoundTag)t,i->ChallengesManager.get(CommonMod.SERVER.get().getOverworld()).get(i));
                if (in.getChallenge() == null) {
                    System.out.println("null challenge read from: " + t);
                } else {
                    challenges.add(in);
                }
            }
        }
        return challenges;
    }

    public void write(ListTag tag) {
        for (ChallengeInstance i : challenges) {
            tag.add(i.toNBT());
        }
    }

    /**
     * Get the list of challenges a player has saved in memory, to retrieve it even when he's offline.
     * @param uuid The player's UUID
     * @param server The server
     * @return A list of his challenge instances
     */
    public static List<ChallengeInstance> getChallengesFor(UUID uuid, MinecraftServer server) {
        File dir = new File(server.getRunDirectory(),"challenges");
        File f = new File(dir,uuid + ".dat");
        if (!f.exists()) {
            return null;
        }
        try {
            CompoundTag tag = NbtIo.readCompressed(f);
            return readFrom(tag.getList("challenges", NbtType.COMPOUND));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveToFile(MinecraftServer server) {
        File dir = new File(server.getRunDirectory(),"challenges");
        dir.mkdirs();
        File f = new File(dir,player.getUuidAsString() + ".dat");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        write(list);
        tag.put("challenges",list);
        try {
            NbtIo.writeCompressed(tag,f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        update(ChallengesManager.get(player.server.getOverworld()).getChallenges());
    }

    public void print() {
        player.sendMessage(new LiteralText("============"),false);
        player.sendMessage(new LiteralText(" Challenges of ").append(player.getName()),false);
        player.sendMessage(new LiteralText("============"),false);
        for (ChallengeInstance c : challenges) {
            player.sendMessage(c.getChallenge().getText().copy().append(": ").append(new LiteralText(c.getCompleteCount() + "/" + c.getChallenge().getMinCount()).formatted(c.isCompleted() ? Formatting.GREEN : Formatting.RED)),false);
        }
    }

    public void reset() {
        challenges.forEach(c->{
            c.setCompleteCount(0);
        });

    }
}
