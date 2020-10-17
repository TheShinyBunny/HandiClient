/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.client;

import com.handicraft.client.challenge.Challenge;
import com.handicraft.client.challenge.ServerChallenge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Queue;

public class ChallengeToastHud extends DrawableHelper {

    private static final Identifier TOAST_TEXTURE = new Identifier("minecraft:textures/gui/toasts.png");
    private static final Identifier ICONS = new Identifier("hcclient:textures/gui/challenges/challenges_icons.png");

    private static final Text CHALLENGE_COMPLETED = new LiteralText("Challenge Completed:");

    public static final ChallengeToastHud INSTANCE = new ChallengeToastHud();

    private Queue<Challenge<?>> queue;
    private Challenge<?> current;
    private int animationTime;

    public ChallengeToastHud() {
        queue = new ArrayDeque<>();
    }

    public void render(MatrixStack matrices) {
        if (current == null && !queue.isEmpty()) {
            animationTime = 0;
            current = queue.poll();
        }
        if (animationTime > 400) {
            animationTime = 0;
            if (queue.isEmpty()) {
                current = null;
            } else {
                current = queue.poll();
            }
        }

        if (current != null) {

            MinecraftClient.getInstance().getTextureManager().bindTexture(TOAST_TEXTURE);
            int width = 160;
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - width / 2;
            int offset = animationTime > 360 ? 360 - animationTime : MathHelper.clamp(animationTime, 0, 40) - 40;
            drawTexture(matrices, x, offset, 0, 32, width, 32);

            MinecraftClient.getInstance().getTextureManager().bindTexture(ICONS);
            drawTexture(matrices, x + 8, 8 + offset, 0, 0, 16, 16);

            matrices.push();
            matrices.scale(0.8f, 0.8f, 0.8f);
            drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, CHALLENGE_COMPLETED, x + 30, 4 + offset, MathHelper.packRgb(71, 71, 71));
            matrices.pop();
            MinecraftClient.getInstance().textRenderer.drawTrimmed(current.getText(), x + 28, 12 + offset, 140, 0);

            animationTime++;
        }
    }

    public void add(Challenge<?> challenge) {
        queue.add(challenge);
    }

}
