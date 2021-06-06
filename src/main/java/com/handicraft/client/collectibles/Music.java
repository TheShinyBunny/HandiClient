/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.collectibles;

import com.handicraft.client.ModSounds;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

/**
 * Represents a music instance that can be played by the Speaker block.
 * A Music object holds an {@link Instance} value on the client side.
 * That sound instance is created every time a music starts playing.
 */
public class Music {

    public static final Identifier START_PLAYING = new Identifier("hcclient:play_music");
    public static final Identifier UPDATE_MUSIC = new Identifier("hcclient:music_update");
    public static final Registry<Music> REGISTRY = FabricRegistryBuilder.createSimple(Music.class,new Identifier("hcclient:music")).buildAndRegister();
    private SoundEvent sound;
    private Text name;
    private Instance instance;

    static {
        register("jingle_bells",ModSounds.JINGLE_BELLS);
        register("zombie",SoundEvents.ENTITY_ZOMBIE_AMBIENT);
        register("ender_dragon_death",SoundEvents.ENTITY_ENDER_DRAGON_DEATH);
    }

    private static void register(String id, SoundEvent sound) {
        Music m = new Music(sound);
        Registry.register(REGISTRY,new Identifier(id),m);
    }

    public Music(SoundEvent sound) {
        this.sound = sound;
    }

    /**
     * Starts playing this music to a player
     * @param p The target player
     * @param pos The position of the speaker block that plays this music.
     * @param loop Whether the sound should loop
     * @param volume The base volume of the music
     * @param range The max range from the speaker the player can hear the sound
     */
    public void play(PlayerEntity p, BlockPos pos, boolean loop, float volume, double range) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeVarInt(REGISTRY.getRawId(this));
        buf.writeBoolean(loop);
        buf.writeFloat(volume);
        buf.writeDouble(range);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,START_PLAYING,buf);
    }

    /**
     * Update the music for a player with the provided settings.
     * @param p The player to update
     * @param looping Whether the sound should loop
     * @param volume The base volume of the music
     * @param range The max range from the speaker the player can hear the sound
     */
    public void update(PlayerEntity p, boolean looping, float volume, double range) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(REGISTRY.getRawId(this));
        buf.writeBoolean(looping);
        buf.writeFloat(volume);
        buf.writeDouble(range);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,UPDATE_MUSIC,buf);
    }

    @Environment(EnvType.CLIENT)
    public static void onUpdate(MinecraftClient mc, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender sender) {
        Music music = REGISTRY.get(buf.readVarInt());
        boolean loop = buf.readBoolean();
        float volume = buf.readFloat();
        double range = buf.readDouble();
        mc.execute(()->{
            music.update(loop,volume,range);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void onPlay(MinecraftClient mc, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender sender) {
        BlockPos pos = buf.readBlockPos();
        Music music = REGISTRY.get(buf.readVarInt());
        boolean loop = buf.readBoolean();
        float volume = buf.readFloat();
        double range = buf.readDouble();
        mc.execute(()-> {
            music.play(pos, loop, volume, range);
        });
    }

    @Environment(EnvType.CLIENT)
    private void play(BlockPos pos, boolean loop, float volume, double range) {
        if (instance != null) {
            instance.stop();
            MinecraftClient.getInstance().getSoundManager().stop(instance);
        }
        instance = new Instance(sound, volume, loop, pos, range);
        MinecraftClient.getInstance().getSoundManager().play(instance);
    }

    @Environment(EnvType.CLIENT)
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
            name = new TranslatableText(Util.createTranslationKey("music",REGISTRY.getId(this)));
        }
        return name;
    }

    /**
     * Stops this type of music from playing to a player
     */
    public void stop(PlayerEntity p) {
        update(p,false,0,0);
    }

    /**
     * A SoundInstance of a music being played to a player.
     * If the music should loop (when the Speaker is configured for Looping) the {@link PositionedSoundInstance#repeat} property is set to true, and the Minecraft
     * sound system automatically replays the sound when it's done.
     *
     * This instance also adjusts its volume according to the distance of the player from the source (from the Speaker block),
     * according to the speaker's range and volume options.
     */
    @Environment(EnvType.CLIENT)
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
