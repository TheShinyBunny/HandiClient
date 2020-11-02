/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.block.entity;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.SpotifyBlock;
import com.handicraft.client.client.screen.SpotifyScreen;
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
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SpotifyBlockEntity extends BlockEntity implements Tickable {

    private Set<PlayerEntity> trackedPlayers = new HashSet<>();
    private Music music;
    private float volume = 1.0f;

    public SpotifyBlockEntity() {
        super(CommonMod.SPOTIFY_BLOCK_ENTITY);
    }

    @Override
    public void tick() {
        if (!world.isClient && music != null) {
            List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, new Box(this.pos).expand(8));
            trackedPlayers.removeIf(p -> {
                if (!players.contains(p)) {
                    music.stop(p);
                    return true;
                }
                return false;
            });
            for (PlayerEntity p : players) {
                if (trackedPlayers.add(p)) {
                    System.out.println("started playing");
                    music.play(p,pos,getCachedState().get(SpotifyBlock.POWERED),volume);
                }
            }
        }
    }

    public void loopStateUpdated(boolean looping) {
        if (music != null) {
            for (PlayerEntity p : trackedPlayers) {
                music.update(p,looping,volume);
            }
        }
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
        ctx.getTaskQueue().execute(()->{
            BlockEntity be = ctx.getPlayer().world.getBlockEntity(pos);
            if (be instanceof SpotifyBlockEntity) {
                ((SpotifyBlockEntity) be).update(music,volume);
            }
        });
    }

    private void update(Music music, float volume) {
        if (this.music != music) {
            for (PlayerEntity p : trackedPlayers) {
                this.music.stop(p);
            }
        }
        this.music = music;
        this.volume = volume;
        if (music != null) {
            for (PlayerEntity p : trackedPlayers) {
                music.update(p,getCachedState().get(SpotifyBlock.POWERED),volume);
            }
        } else {
            trackedPlayers.clear();
        }
        markDirty();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (music != null) {
            tag.putString("Music",Collectibles.REGISTRY.getId(music).toString());
        }
        tag.putFloat("Volume",volume);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("Music", NbtType.STRING)) {
            Collectible c = Collectibles.REGISTRY.get(new Identifier(tag.getString("Music")));
            if (c instanceof Music) {
                System.out.println("set music to " + c.getId());
                this.music = ((Music) c);
            } else {
                System.out.println("no music");
            }
        }
        this.volume = tag.getFloat("Volume");
    }

    public PacketByteBuf toPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeCompoundTag(toTag(new CompoundTag()));
        return buf;
    }
}
