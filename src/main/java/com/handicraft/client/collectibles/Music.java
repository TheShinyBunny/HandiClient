/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

public class Music extends Collectible {

    public static final Identifier START_PLAYING = new Identifier("hcclient:play_music");
    public static final Identifier UPDATE_MUSIC = new Identifier("hcclient:music_update");
    private SoundEvent sound;
    private Text name;
    private Instance instance;

    public Music(SoundEvent sound) {
        super(CollectibleType.MUSIC);
        this.sound = sound;
    }

    public void play(PlayerEntity p, BlockPos pos, boolean loop, float volume) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeVarInt(Collectibles.REGISTRY.getRawId(this));
        buf.writeBoolean(loop);
        buf.writeFloat(volume);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,START_PLAYING,buf);
    }

    public void update(PlayerEntity p, boolean looping, float volume) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Collectibles.REGISTRY.getRawId(this));
        buf.writeBoolean(looping);
        buf.writeFloat(volume);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,UPDATE_MUSIC,buf);
    }

    public static void onUpdate(PacketContext ctx, PacketByteBuf buf) {
        System.out.println("updating music");
        Collectible c = Collectibles.REGISTRY.get(buf.readVarInt());
        if (c instanceof Music) {
            boolean loop = buf.readBoolean();
            float volume = buf.readFloat();
            ctx.getTaskQueue().execute(()->{
                ((Music) c).update(loop,volume);
            });

        }
    }

    public static void onPlay(PacketContext ctx, PacketByteBuf buf) {
        System.out.println("started playing");
        BlockPos pos = buf.readBlockPos();
        Collectible c = Collectibles.REGISTRY.get(buf.readVarInt());
        if (c instanceof Music) {
            boolean loop = buf.readBoolean();
            float volume = buf.readFloat();
            ctx.getTaskQueue().execute(()-> {
                ((Music) c).play(pos, loop, volume);
            });
        }
    }

    private void play(BlockPos pos, boolean loop, float volume) {
        if (instance != null) {
            instance.setStopped(true);
            MinecraftClient.getInstance().getSoundManager().stop(instance);
        }
        System.out.println("playing music " + sound.getId() + " with volume " + volume);
        instance = new Instance(sound, volume, loop, pos);
        MinecraftClient.getInstance().getSoundManager().play(instance);
    }

    private void update(boolean loop, float volume) {
        if (instance != null) {
            System.out.println("volume: " + volume);
            if (volume == 0) {
                System.out.println("stopping " + sound.getId());
                instance.setStopped(true);
                //MinecraftClient.getInstance().getSoundManager().stop(instance);
                instance = null;
            } else {
                instance.setVolume(volume);
                if (instance.isRepeatable() != loop) {
                    instance.setLooping(loop);
                    MinecraftClient.getInstance().getSoundManager().stop(instance);
                    MinecraftClient.getInstance().getSoundManager().play(instance);
                }
            }
        }
    }

    public Text getText() {
        if (name == null) {
            name = new TranslatableText(Util.createTranslationKey("music",getId()));
        }
        return name;
    }

    public void stop(PlayerEntity p) {
        update(p,false,0);
    }

    public static class Instance extends PositionedSoundInstance implements TickableSoundInstance {

        protected Instance(SoundEvent sound, float volume, boolean loop, BlockPos pos) {
            super(sound.getId(), SoundCategory.MASTER, volume, 1f, loop, 10, AttenuationType.LINEAR, pos.getX(),pos.getY(),pos.getZ(),false);
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public void tick() {

        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public void setLooping(boolean loop) {
            this.repeat = loop;
            //this.looping = loop;
        }

        public void setStopped(boolean stopped) {
            this.repeat = !stopped;
        }
    }


}
