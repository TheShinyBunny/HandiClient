/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class InvisibleButtonWidget extends ButtonWidget {
    public InvisibleButtonWidget(int x, int y, int width, int height, PressAction onPress) {
        super(x, y, width, height, LiteralText.EMPTY, onPress);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }
}
