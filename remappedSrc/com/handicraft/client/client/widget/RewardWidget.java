/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.rewards.Reward;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class RewardWidget extends AbstractButtonWidget {

    private Reward reward;
    private HandiPassScreen screen;
    private int hoveredTicks = 0;

    public RewardWidget(int x, int y, int width, int height, Reward reward, HandiPassScreen screen) {
        super(x, y, width, height, new LiteralText(reward.getName()));
        this.reward = reward;
        this.screen = screen;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (isHovered()) {
            if (hoveredTicks == 0) {
                reward.startedHover(screen);
            }
            reward.hoveredTick(screen,hoveredTicks);
            hoveredTicks++;
        } else {
            if (hoveredTicks > 0) {
                reward.stoppedHover(screen);
            }
            hoveredTicks = 0;
        }

        MinecraftClient.getInstance().getTextureManager().bindTexture(reward.getTexture());
        drawTexture(matrices,x,y,width,height,0,0,89,reward.getTextureHeight(),89,reward.getTextureHeight());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        reward.clicked(screen);
    }
}
