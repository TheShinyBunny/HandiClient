/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.entity;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.SpeakerBlock;
import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.collectibles.Music;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeakerBlockEntity extends BlockEntity implements Tickable {

    private Set<PlayerEntity> trackedPlayers = new HashSet<>();
    private Music music;
    private float volume = 1.0f;
    private boolean loop = false;
    private double range = 8.0;

    public SpeakerBlockEntity() {
        super(CommonMod.SPEAKER_BLOCK_ENTITY_TYPE);
    }

    @Override
    public void tick() {
        if (!world.isClient && music != null) {
            List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, new Box(this.pos).expand(range));
            trackedPlayers.removeIf(p -> {
                if (!players.contains(p)) {
                    music.stop(p);
                    return true;
                }
                return false;
            });
            for (PlayerEntity p : players) {
                if (trackedPlayers.add(p)) {
                    music.play(p,pos,loop,volume,range);
                }
            }
        }
    }

    public double getRange() {
        return range;
    }

    public boolean isLooping() {
        return loop;
    }

    public Music getMusic() {
        return music;
    }

    public float getVolume() {
        return volume;
    }

    public static void updateFromPacket(PacketContext ctx, PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Collectible c = Collectibles.REGISTRY.get(buf.readVarInt());
        Music music;
        if (c instanceof Music) {
            music = (Music) c;
        } else {
            music = null;
        }
        float volume = buf.readFloat();
        double range = buf.readDouble();
        boolean loop = buf.readBoolean();
        ctx.getTaskQueue().execute(()->{
            BlockEntity be = ctx.getPlayer().world.getBlockEntity(pos);
            if (be instanceof SpeakerBlockEntity) {
                ((SpeakerBlockEntity) be).update(music,volume,range,loop);
            }
        });
    }

    private void update(Music music, float volume, double range, boolean loop) {
        if (this.music != music) {
            for (PlayerEntity p : trackedPlayers) {
                this.music.stop(p);
            }
        }
        this.music = music;
        this.volume = volume;
        this.range = range;
        this.loop = loop;
        if (music != null) {
            for (PlayerEntity p : trackedPlayers) {
                music.update(p,loop,volume,range);
            }
        } else {
            trackedPlayers.clear();
        }
        markDirty();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (music != null) {
            for (PlayerEntity p : trackedPlayers) {
                music.stop(p);
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (music != null) {
            tag.putString("Music",Collectibles.REGISTRY.getId(music).toString());
        }
        tag.putFloat("Volume",volume);
        tag.putDouble("Range",range);
        tag.putBoolean("Loop",loop);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("Music", NbtType.STRING)) {
            Collectible c = Collectibles.REGISTRY.get(new Identifier(tag.getString("Music")));
            if (c instanceof Music) {
                this.music = ((Music) c);
            }
        }
        this.volume = tag.getFloat("Volume");
        this.range = tag.getDouble("Range");
        this.loop = tag.getBoolean("Loop");
    }

    public PacketByteBuf toPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeCompoundTag(toTag(new CompoundTag()));
        return buf;
    }
}
