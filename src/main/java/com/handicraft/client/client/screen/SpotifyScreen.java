/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.SpotifyBlockEntity;
import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.collectibles.Music;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpotifyScreen extends Screen {

    private MusicList list;
    private SpotifyBlockEntity spotify;
    private float volume;

    public SpotifyScreen(SpotifyBlockEntity spotify) {
        super(new TranslatableText("gui.title.spotify"));
        this.spotify = spotify;
        this.volume = spotify.getVolume();
    }

    @Override
    protected void init() {
        super.init();
        addButton(new SliderWidget(width / 2 - 100, 30, 200, 20, new TranslatableText("spotify.volume.label"),volume) {

            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText("spotify.volume",(int)(volume * 100)).append("%"));
            }

            @Override
            protected void applyValue() {
                volume = (float)MathHelper.clamp(this.value,0.1,1.0);
            }
        }).updateMessage();
        list = addChild(new MusicList(client, width / 2 - 150, 70, 300, height - 140, 25));
        addButton(new ButtonWidget(width / 2 - 125, height - 50, 100, 20, ScreenTexts.CANCEL, b->{
            onClose();
        }));
        addButton(new ButtonWidget(width / 2 + 25, height - 50, 100, 20, ScreenTexts.DONE, b->{
            updateAndClose();
        }));
    }

    private void updateAndClose() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(spotify.getPos());
        MusicWidget w = list.selected;
        if (w == null) {
            buf.writeVarInt(-1);
        } else {
            buf.writeVarInt(Collectibles.REGISTRY.getRawId(w.music));
        }
        buf.writeFloat(this.volume);
        ClientSidePacketRegistry.INSTANCE.sendToServer(CommonMod.UPDATE_SPOTIFY, buf);
        onClose();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredString(matrices,textRenderer,"Spotify",width / 2,10,-1);
        list.render(matrices,mouseX,mouseY,delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public class MusicList extends AbstractParentElement {

        private final MinecraftClient client;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int maxVisibleItems;
        private int itemHeight;
        private MusicWidget selected;
        private List<MusicWidget> entries = new ArrayList<>();
        private int scroll;

        public MusicList(MinecraftClient client, int x, int y, int width, int height, int itemHeight) {
            this.client = client;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.itemHeight = itemHeight;
            this.maxVisibleItems = height / itemHeight;
            for (Collectible m : Collectibles.REGISTRY.stream().filter(c->c instanceof Music).collect(Collectors.toList())) {
                if (m instanceof Music) {
                    MusicWidget entry = new MusicWidget(this,((Music) m));
                    entries.add(entry);
                    if (spotify.getMusic() == m) {
                        selected = entry;
                    }
                }
            }
        }


        @Override
        public List<? extends Element> children() {
            return entries;
        }

        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            int index = 0;
            for (MusicWidget w : entries) {
                if (index >= scroll && index < scroll + maxVisibleItems) {

                    int y = this.y + (index - scroll) * this.itemHeight;
                    if (selected == w) {
                        int n = this.itemHeight - 4;
                        int r = x;
                        int q = x + width;
                        RenderSystem.disableTexture();
                        float f = 0.5F;
                        RenderSystem.color4f(f, f, f, 1.0F);
                        bufferBuilder.begin(7, VertexFormats.POSITION);
                        bufferBuilder.vertex(r, y + n + 2, 0.0D).next();
                        bufferBuilder.vertex(q, y + n + 2, 0.0D).next();
                        bufferBuilder.vertex(q, y - 2, 0.0D).next();
                        bufferBuilder.vertex(r, y - 2, 0.0D).next();
                        tessellator.draw();
                        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
                        bufferBuilder.begin(7, VertexFormats.POSITION);
                        bufferBuilder.vertex(r + 1, y + n + 1, 0.0D).next();
                        bufferBuilder.vertex(q - 1, y + n + 1, 0.0D).next();
                        bufferBuilder.vertex(q - 1, y - 1, 0.0D).next();
                        bufferBuilder.vertex(r + 1, y - 1, 0.0D).next();
                        tessellator.draw();
                        RenderSystem.enableTexture();
                    }

                    w.render(matrices,index,this.x,y,width,itemHeight,mouseX,mouseY, Objects.equals(w,getEntryAt(mouseX,mouseY)),delta);
                }
                index++;
            }
        }

        private MusicWidget getEntryAt(int mouseX, int mouseY) {
            int top = this.y - itemHeight * scroll - 2;
            if (mouseY < top) return null;
            int index = (mouseY - top) / itemHeight;
            if (index >= 0 && index < maxVisibleItems && index < entries.size() && mouseX > x && mouseX < x + width) {
                return entries.get(index);
            }
            return null;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseY > y && mouseY < y + height && mouseX > x && mouseX < x + width;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            scroll -= amount > 0 ? 1 : -1;
            scroll = MathHelper.clamp(scroll,0,Math.max(0,entries.size() - maxVisibleItems));
            return true;
        }
    }

    public static class MusicWidget implements Element {

        private MusicList list;
        private Music music;
        private Identifier texture;

        public MusicWidget(MusicList list, Music music) {
            this.list = list;
            this.music = music;
            this.texture = new Identifier("hcclient:textures/rewards/music/" + music.getId().getPath() + ".png");
        }


        public void render(MatrixStack matrices, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            drawTextWithShadow(matrices,MinecraftClient.getInstance().textRenderer, music.getText(), x + 5, y + 6, hovered ? 0xffaaaaaa : -1);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (list.getEntryAt((int)mouseX,(int)mouseY) == this) {
                list.selected = this;
                return true;
            }
            return false;
        }
    }
}
