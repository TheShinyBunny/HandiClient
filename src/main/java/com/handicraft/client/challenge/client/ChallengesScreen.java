/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.client;

import com.handicraft.client.challenge.ChallengeInstance;
import com.handicraft.client.client.screen.LobbyScreen;
import com.handicraft.client.client.widget.SelectableImageButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class ChallengesScreen extends LobbyScreen<ChallengesScreen> {

    private static final Identifier BACKGROUND = new Identifier("hcclient:textures/gui/challenges/background.png");
    private static final int PACKS = 5;

    private int selectedPack = 0;

    public ChallengesScreen(boolean tabs) {
        super(tabs);
    }

    @Override
    protected void init() {
        super.init();

        int xoff = 30;
        for (int i = 0; i < PACKS; i++) {
            int fi = i;
            addButton(new SelectableImageButton(xoff,height / 3,45,25,0,0,0,new Identifier("hcclient:textures/gui/challenges/pack" + (i + 1) + ".png"), 138,89,138,178,b->{
                selectedPack = fi;
            },s-> selectedPack == fi,89,0x55000000));
            xoff += 50;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        client.getTextureManager().bindTexture(BACKGROUND);
        drawTexture(matrices,0,0,width,height,0,0,1920,1080,1920,1080);

        List<ChallengeInstance> challenges = ClientChallengesManager.getChallenges();

        int yoff = width / 4;

        for (int i = 0; i < 6; i++) {
            int index = selectedPack * 6 + i;
            drawTextWithShadow(matrices,textRenderer,new LiteralText("#" + (i + 1)).formatted(Formatting.YELLOW),7,yoff + 5,0);
            fill(matrices,25,yoff,360,yoff + 20,0xffd0ceff);
            if (index < challenges.size()) {
                ChallengeInstance ci = challenges.get(index);
                drawChallenge(ci,matrices,yoff);
            } else {
                int current = selectedPack * 6 + i;
                int offset = (current + 1) - challenges.size();

                int days = offset % 7;
                int weeks = offset / 7;
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

        ItemStack icon = ch.getIcon();
        itemRenderer.renderGuiItemIcon(icon,xoff,y);
        xoff += 25;

        textRenderer.draw(matrices,ch.getText(),xoff, y + 5, 0x00333333);

        Text cc = new LiteralText(ci.getCompleteCount() + "").formatted(ci.isCompleted() ? Formatting.GREEN : Formatting.RED).append("/" + ch.getMinCount());
        int tw = textRenderer.getWidth(cc);

        drawTextWithShadow(matrices,textRenderer,cc,360 - tw,y + 5,0);
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
