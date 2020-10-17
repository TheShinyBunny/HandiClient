/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

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
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerChallenges {

    public static final Identifier COMPLETED = new Identifier("hcclient:completed_challenge");
    public static final Identifier PROGRESS_UPDATE = new Identifier("hcclient:challenge_progress");
    public static final Identifier UPDATE_CHALLENGES = new Identifier("hcclient:update_challenges");

    private List<ChallengeInstance> challenges;
    private ServerPlayerEntity player;

    public PlayerChallenges(ServerPlayerEntity player) {
        this.player = player;
        challenges = new ArrayList<>();
    }

    public void addChallenge(ServerChallenge<?> c) {
        if (get(c) == null) {
            challenges.add(new ChallengeInstance(c, 0));
        }
    }

    public <T extends ObjectiveInstance> void trigger(ServerChallenge<T> challenge) {
        for (ChallengeInstance i : challenges) {
            if (!i.isCompleted() && i.getChallenge().equals(challenge)) {
                i.trigger();
                if (i.isCompleted()) {
                    sendPacket(COMPLETED,i);
                } else {
                    sendPacket(PROGRESS_UPDATE,i);
                }
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

    public void update(List<ServerChallenge<?>> c) {
        for (ServerChallenge<?> ch : c) {
            ChallengeInstance i = get(ch);
            if (i == null) {
                addChallenge(ch);
            }
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

    public void read(ListTag list) {
        challenges = readFrom(list);
    }

    public static List<ChallengeInstance> readFrom(ListTag tag) {
        List<ChallengeInstance> challenges = new ArrayList<>();
        for (Tag t : tag) {
            if (t instanceof CompoundTag) {
                challenges.add(ChallengeInstance.fromNBT((CompoundTag)t,false));
            }
        }
        return challenges;
    }

    public void write(ListTag tag) {
        for (ChallengeInstance i : challenges) {
            tag.add(i.toNBT());
        }
    }

    public static List<ChallengeInstance> getChallengesFor(UUID uuid, MinecraftServer server) {
        File f = new File(server.getRunDirectory(),"challenges/" + uuid + ".dat");
        try {
            CompoundTag tag = NbtIo.read(f);
            return readFrom(tag.getList("challenges", NbtType.COMPOUND));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveToFile(MinecraftServer server) {
        File f = new File(server.getRunDirectory(),"challenges/" + player.getUuid() + ".dat");
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        write(list);
        tag.put("challenges",list);
        try {
            NbtIo.write(tag,f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        update(ChallengesManager.get(player.server.getOverworld()).getChallenges());
    }

    public void writeToClient(PacketByteBuf buf) {
        init();
        buf.writeVarInt(challenges.size());
    }


}
