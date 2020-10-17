/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HoldableImageButton extends ImageButton {
    private boolean pressed;
    private int timer;
    private final PressAction action;
    private int interval;

    public HoldableImageButton(int x, int y, int width, int height, int u, int v, int vOffset, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, PressAction action, int interval) {
        this(x, y, width, height, u, v, vOffset, texture, regionWidth, regionHeight, textureWidth, textureHeight, action, null, interval);
    }

    public HoldableImageButton(int x, int y, int width, int height, int u, int v, int vOffset, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, PressAction action, Text tooltip, int interval) {
        super(x, y, width, height, u, v, vOffset, texture, regionWidth, regionHeight, textureWidth, textureHeight, action,tooltip);
        this.action = action;
        this.interval = interval;
        this.timer = 0;
    }

    @Override
    public void onPress() {
        pressed = true;
        action.onPress(this);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (!active) {
            pressed = false;
            timer = 0;
        }
        if (pressed) {
            timer++;
            if (timer >= interval) {
                timer = 0;
                onPress();
            }
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        pressed = false;
        timer = 0;
    }
}
