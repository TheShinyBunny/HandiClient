/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.client;

import com.handicraft.client.CommonMod;
import com.handicraft.client.challenge.ChallengeInstance;
import com.handicraft.client.client.screen.LobbyScreen;
import com.handicraft.client.client.widget.SelectableImageButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Calendar;
import java.util.List;

public class ChallengesScreen extends LobbyScreen<ChallengesScreen> {

    private static final Identifier BACKGROUND = new Identifier("hcclient:textures/gui/challenges/background.png");

    private int selectedWeek = 0;

    public ChallengesScreen(boolean tabs) {
        super(tabs);
    }

    @Override
    protected void init() {
        super.init();

        int xoff = 30;
        for (int i = 0; i < 3; i++) {
            int fi = i;
            addButton(new SelectableImageButton(xoff,height / 3,45,25,0,0,0,new Identifier("hcclient:textures/gui/challenges/week" + (i + 1) + ".png"), 138,89,138,178,b->{
                selectedWeek = fi;
            },s->selectedWeek == fi,89,0x55000000));
            xoff += 50;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        client.getTextureManager().bindTexture(BACKGROUND);
        drawTexture(matrices,0,0,width,height,0,0,1920,1080,1920,1080);

        List<ChallengeInstance> challenges = ClientChallengesManager.getChallenges();

        int yoff = width / 4;

        for (int i = 0; i < 5; i++) {
            int index = selectedWeek * 5 + i;
            drawTextWithShadow(matrices,textRenderer,new LiteralText("#" + (i + 1)).formatted(Formatting.YELLOW),7,yoff + 5,0);
            fill(matrices,25,yoff,300,yoff + 20,0xffd0ceff);
            if (index < challenges.size()) {
                ChallengeInstance ci = challenges.get(index);
                drawChallenge(ci,matrices,yoff);
            } else {
                //drawSoonChallenge((selectedWeek + 1) * 5 - (5 - i) - selectedWeek * 5,index / 5 - challenges.size() / 5,matrices,yoff);
                Calendar c = Calendar.getInstance(CommonMod.TIME_ZONE);
                int day = c.get(Calendar.DAY_OF_WEEK);
                int today = selectedWeek * 7 + i;
                int lastav = challenges.size() + (challenges.size() / 5 * 2) - 1;
                if (day > 5) {
                    lastav -= (day - 5);
                }
                int days = (today - lastav) % 7;
                int weeks = (today - lastav) / 7;
                drawSoonChallenge(days,weeks,matrices,yoff);
            }
            yoff += 25;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawSoonChallenge(int days, int weeks, MatrixStack matrices, int yoff) {
        if (weeks == 0) {
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("challenges.available.days",days).formatted(Formatting.ITALIC,Formatting.GRAY),35,yoff + 5,0);
        } else if (days == 0) {
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("challenges.available.weeks",weeks).formatted(Formatting.ITALIC,Formatting.GRAY),35,yoff + 5,0);
        } else {
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("challenges.available.weeks_days",weeks,days).formatted(Formatting.ITALIC,Formatting.GRAY),35,yoff + 5,0);
        }
    }

    private void drawChallenge(ChallengeInstance ci, MatrixStack matrices, int y) {
        ClientChallenge<?> ch = (ClientChallenge<?>) ci.getChallenge();

        int xoff = 27;

        ItemConvertible[] icons = ch.getIcons();
        for (ItemConvertible i : icons) {
            itemRenderer.renderGuiItemIcon(new ItemStack(i),xoff,y);
            xoff += 25;
        }
        textRenderer.draw(matrices,ch.getText(),xoff, y + 5, 0x00333333);

        drawTextWithShadow(matrices,textRenderer,new LiteralText(ci.getCompleteCount() + "").formatted(ci.isCompleted() ? Formatting.GREEN : Formatting.RED).append("/" + ch.getMinCount()),270,y + 5,0);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public ChallengesScreen create(Screen parent) {
        return new ChallengesScreen(true);
    }

    @Override
    public boolean isSelected(LobbyScreen<?> current) {
        return current instanceof ChallengesScreen;
    }
}
