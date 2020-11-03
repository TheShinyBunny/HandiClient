/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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

    public void play(PlayerEntity p, BlockPos pos, boolean loop, float volume, double range) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeVarInt(Collectibles.REGISTRY.getRawId(this));
        buf.writeBoolean(loop);
        buf.writeFloat(volume);
        buf.writeDouble(range);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,START_PLAYING,buf);
    }

    public void update(PlayerEntity p, boolean looping, float volume, double range) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Collectibles.REGISTRY.getRawId(this));
        buf.writeBoolean(looping);
        buf.writeFloat(volume);
        buf.writeDouble(range);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,UPDATE_MUSIC,buf);
    }

    public static void onUpdate(PacketContext ctx, PacketByteBuf buf) {
        Collectible c = Collectibles.REGISTRY.get(buf.readVarInt());
        if (c instanceof Music) {
            boolean loop = buf.readBoolean();
            float volume = buf.readFloat();
            double range = buf.readDouble();
            ctx.getTaskQueue().execute(()->{
                ((Music) c).update(loop,volume,range);
            });

        }
    }

    public static void onPlay(PacketContext ctx, PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Collectible c = Collectibles.REGISTRY.get(buf.readVarInt());
        if (c instanceof Music) {
            boolean loop = buf.readBoolean();
            float volume = buf.readFloat();
            double range = buf.readDouble();
            ctx.getTaskQueue().execute(()-> {
                ((Music) c).play(pos, loop, volume, range);
            });
        }
    }

    private void play(BlockPos pos, boolean loop, float volume, double range) {
        if (instance != null) {
            instance.stop();
            MinecraftClient.getInstance().getSoundManager().stop(instance);
        }
        instance = new Instance(sound, volume, loop, pos, range);
        MinecraftClient.getInstance().getSoundManager().play(instance);
    }

    private void update(boolean loop, float volume, double range) {
        if (instance != null) {
            if (volume == 0) {
                instance.stop();
                instance = null;
            } else {
                instance.baseVolume = volume;
                instance.range = range;
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
        update(p,false,0,0);
    }

    public static class Instance extends PositionedSoundInstance implements TickableSoundInstance {

        private float baseVolume;
        private double range;

        protected Instance(SoundEvent sound, float volume, boolean loop, BlockPos pos, double range) {
            super(sound.getId(), SoundCategory.MASTER, volume, 1f, loop, 10, AttenuationType.LINEAR, pos.getX(),pos.getY(),pos.getZ(),false);
            this.baseVolume = volume;
            this.range = range;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public void tick() {
            Vec3d pos = MinecraftClient.getInstance().getCameraEntity().getPos();
            double dist = pos.distanceTo(new Vec3d(x,y,z));
            if (dist <= range) {
                double diff = (range - dist) / range;
                this.volume = (float) (baseVolume * diff);
            } else {
                this.volume = 0;
            }
        }

        public void setLooping(boolean loop) {
            this.repeat = loop;
        }

        public void stop() {
            this.repeat = false;
        }
    }


}
