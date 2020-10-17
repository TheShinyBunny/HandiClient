/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class SelectableImageButton extends ImageButton {

    private Predicate<SelectableImageButton> selected;
    private int selectedVOffset;
    private int hoverColor;

    public SelectableImageButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, PressAction action, Predicate<SelectableImageButton> selected, int selectedVOffset, int hoverColor) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, regionWidth, regionHeight, textureWidth, textureHeight, action);
        this.selected = selected;
        this.selectedVOffset = selectedVOffset;
        this.hoverColor = hoverColor;
    }

    public SelectableImageButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, PressAction action, Text tooltip, Predicate<SelectableImageButton> selected, int selectedVOffset, int hoverColor) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, regionWidth, regionHeight, textureWidth, textureHeight, action, tooltip);
        this.selected = selected;
        this.selectedVOffset = selectedVOffset;
        this.hoverColor = hoverColor;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (texture == null) return;
        client.getTextureManager().bindTexture(texture);
        int i = this.v;
        if (selected.test(this)) {
            i += this.selectedVOffset;
        } else if (this.isHovered() && active) {
            i += this.hoveredVOffset;
        }

        drawTexture(matrices, this.x, this.y, this.width, this.height, this.u, i, this.regionWidth, this.regionHeight, this.textureWidth, this.textureHeight);
        if (!active) {
            fill(matrices,x,y,x + width,y + height,0xaa000000);
        } else if (isHovered() && hoverColor != -1) {
            fill(matrices,x,y,x + width,y + height,hoverColor);
        }
    }
}
