/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.block.entity.SpeakerBlockEntity;
import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.Collectibles;
import com.handicraft.client.collectibles.Music;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpeakerScreen extends Screen {

    private static final Text LOOPING_TEXT = new TranslatableText("speaker.loop");
    private MusicList list;
    private SpeakerBlockEntity speaker;
    private float volume;
    private double range;
    private boolean loop;

    public SpeakerScreen(SpeakerBlockEntity speaker) {
        super(new TranslatableText("gui.title.speaker"));
        this.speaker = speaker;
        this.volume = speaker.getVolume();
        this.loop = speaker.isLooping();
        this.range = speaker.getRange();
    }

    @Override
    protected void init() {
        super.init();
        addButton(new SliderWidget(width / 2 - 150, 30, 140, 20, new TranslatableText("speaker.volume",0).append("%"),volume) {

            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText("speaker.volume",(int)(volume * 100)).append("%"));
            }

            @Override
            protected void applyValue() {
                volume = (float) getValue(value,0.1,1,0.01);
            }
        }).updateMessage();
        addButton(new SliderWidget(width / 2 + 10, 30, 140, 20, new TranslatableText("speaker.range",2).append(" Blocks"),getRatio(range,2,16,1)) {

            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText("speaker.range",(int)range));
            }

            @Override
            protected void applyValue() {
                range = getValue(value,2,16,1);
            }
        }).updateMessage();
        addButton(new ButtonWidget(width / 2 - 100, 70, 200, 20, ScreenTexts.composeToggleText(LOOPING_TEXT,loop),b->{
            loop = !loop;
            b.setMessage(ScreenTexts.composeToggleText(LOOPING_TEXT,loop));
        }));
        list = addChild(new MusicList(width / 2 - 150, 100, 300, height - 140, 25));
        addButton(new ButtonWidget(width / 2 - 125, height - 50, 100, 20, ScreenTexts.CANCEL, b->{
            onClose();
        }));
        addButton(new ButtonWidget(width / 2 + 25, height - 50, 100, 20, ScreenTexts.DONE, b->{
            updateAndClose();
        }));
    }

    public double getRatio(double value, double min, double max, double step) {
        return MathHelper.clamp((this.adjust(value,min,max,step) - min) / (max - min), 0.0D, 1.0D);
    }

    public double getValue(double ratio, double min, double max, double step) {
        return this.adjust(MathHelper.lerp(MathHelper.clamp(ratio, 0.0D, 1.0D), min, max),min,max,step);
    }

    private double adjust(double value, double min, double max, double step) {
        if (step > 0.0F) {
            value = (step * (float)Math.round(value / step));
        }

        return MathHelper.clamp(value, min, max);
    }

    private void updateAndClose() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(speaker.getPos());
        MusicWidget w = list.selected;
        if (w == null) {
            buf.writeVarInt(-1);
        } else {
            buf.writeVarInt(Collectibles.REGISTRY.getRawId(w.music));
        }
        buf.writeFloat(volume);
        buf.writeDouble(range);
        buf.writeBoolean(loop);
        ClientSidePacketRegistry.INSTANCE.sendToServer(CommonMod.UPDATE_SPEAKER, buf);
        onClose();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredString(matrices,textRenderer,"Speaker",width / 2,10,-1);
        list.render(matrices,mouseX,mouseY,delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public class MusicList extends AbstractParentElement {

        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int maxVisibleItems;
        private final int itemHeight;
        private MusicWidget selected;
        private final List<MusicWidget> entries = new ArrayList<>();
        private int scroll;

        public MusicList(int x, int y, int width, int height, int itemHeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.itemHeight = itemHeight;
            this.maxVisibleItems = height / itemHeight;
            for (Collectible m : Collectibles.REGISTRY.stream().filter(c->c instanceof Music).collect(Collectors.toList())) {
                if (m instanceof Music) {
                    MusicWidget entry = new MusicWidget(((Music) m));
                    entries.add(entry);
                    if (speaker.getMusic() == m) {
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

    public class MusicWidget implements Element {


        private Music music;

        public MusicWidget(Music music) {
            this.music = music;
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
