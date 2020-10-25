/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ImageButton extends ButtonWidget {
    protected final int u;
    protected final int v;
    protected final int hoveredVOffset;
    protected final Identifier texture;
    protected final int regionWidth;
    protected final int regionHeight;
    protected final int textureWidth;
    protected final int textureHeight;
    private Text tooltip;

    public ImageButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, PressAction action) {
        this(x,y,width,height,u,v,hoveredVOffset,texture,regionWidth,regionHeight,textureWidth,textureHeight,action,null);
    }

    public ImageButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, PressAction action, Text tooltip) {
        super(x, y, width, height, LiteralText.EMPTY, action);
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.tooltip = tooltip;
    }


    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (texture == null) return;
        client.getTextureManager().bindTexture(texture);
        int i = this.v;
        if (this.isHovered() && active) {
            i += this.hoveredVOffset;
        }

        drawTexture(matrices, this.x, this.y, this.width, this.height, this.u, i, this.regionWidth, this.regionHeight, this.textureWidth, this.textureHeight);
        if (!active) {
            fill(matrices,x,y,x + width,y + height,0xaa000000);
        }
    }

    public Text getTooltip() {
        return tooltip;
    }
}
