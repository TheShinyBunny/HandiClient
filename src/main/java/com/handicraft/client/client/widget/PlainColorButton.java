/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import com.handicraft.client.client.ClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PlainColorButton extends ButtonWidget {

    private final int fontSize;
    private boolean selected;
    private int background;
    private int selectedBackground;
    private int textColor;
    private int selectedTextColor;
    private int hoveredTextColor;
    private int hoveredBackground;

    public PlainColorButton(int x, int y, int width, int height, Text message, PressAction onPress, boolean selected, int background, int selectedBackground, int textColor, int selectedTextColor, int hoveredTextColor, int hoveredBackground, int fontSize) {
        super(x, y, width, height, message, onPress);
        this.selected = selected;
        this.background = background;
        this.selectedBackground = selectedBackground;
        this.textColor = textColor;
        this.selectedTextColor = selectedTextColor;
        this.hoveredTextColor = hoveredTextColor;
        this.hoveredBackground = hoveredBackground;
        this.fontSize = fontSize;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (ClientMod.VANILLA_BUTTONS) {
            super.renderButton(matrices, mouseX, mouseY, delta);
        } else {
            fill(matrices, x, y, x + width, y + height, isHovered() && !selected ? hoveredBackground : selected ? selectedBackground : active ? background : 0xff777777);
            int width = MinecraftClient.getInstance().textRenderer.getWidth(getMessage());
            MinecraftClient.getInstance().textRenderer.draw(matrices, getMessage(), (float) (x + (this.width / 2) - width / 2), y + (height - 8) / 2f, isHovered() && !selected ? hoveredTextColor : selected ? selectedTextColor : textColor);
        }
    }

    @Override
    public boolean isHovered() {
        return (super.isHovered() && active) || selected;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return super.clicked(mouseX, mouseY) && !selected;
    }
}
