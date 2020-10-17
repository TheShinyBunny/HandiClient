/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import com.handicraft.client.client.screen.HandiPassScreen;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import com.handicraft.client.rewards.Reward;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class RewardWidget extends AbstractButtonWidget {


    private static final Text CLAIM_TEXT = new TranslatableText("handipass.claim");
    private static final Text CLAIMED_TEXT = new TranslatableText("handipass.claimed");
    private Runnable onPress;
    private Reward reward;
    private HandiPassScreen screen;
    private boolean wasHovered;

    public RewardWidget(int x, int y, int width, int height, Reward reward, HandiPassScreen screen) {
        super(x, y, width, height, new LiteralText(reward.getName()));
        this.reward = reward;
        this.screen = screen;
    }

    public RewardWidget(int x, int y, int width, int height, Reward reward, Runnable onPress) {
        super(x, y, width, height, new LiteralText(reward.getName()));
        this.reward = reward;
        this.onPress = onPress;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (screen != null) {
            if (isHovered()) {
                if (!wasHovered) {
                    screen.setPreviewed(reward);
                }
                wasHovered = true;
            } else {
                if (wasHovered) {
                    screen.setPreviewed(null);
                }
                wasHovered = false;
            }
        }

        MinecraftClient.getInstance().getTextureManager().bindTexture(reward.getTexture());
        drawTexture(matrices,x,y,width,height,0,0,89,reward.getTextureHeight(),89,reward.getTextureHeight());

        if (screen != null && screen.getSelected() == reward) {
            fill(matrices,x,y,x + width,y + height,0x7711ff11);
        }

        MatrixStack stack = new MatrixStack();
        if (ClientCollectibleCache.needsClaim(reward)) {
            drawCenteredText(stack, MinecraftClient.getInstance().textRenderer, CLAIM_TEXT, x + width / 2, y + 2, 16776960);
        } else if (ClientCollectibleCache.wasClaimed(reward)) {
            drawCenteredText(stack, MinecraftClient.getInstance().textRenderer, CLAIMED_TEXT, x + width / 2, y + 2, 0xffff1111);
        }
    }

    public Reward getReward() {
        return reward;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (screen != null) {
            screen.setSelected(reward);
        } else if (onPress != null) {
            onPress.run();
        }
    }
}
