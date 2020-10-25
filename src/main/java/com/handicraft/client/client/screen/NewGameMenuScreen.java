/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.challenge.client.ChallengesScreen;
import com.handicraft.client.client.ClientMod;
import com.handicraft.client.client.widget.PlainColorButton;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class NewGameMenuScreen extends Screen {

    private static final Identifier HANDICRACK_LOGO = new Identifier("hcclient:textures/gui/handicrack.png");
    private int animationTime;
    private int dir;

    public NewGameMenuScreen() {
        super(LiteralText.EMPTY);
        animationTime = 1;
        dir = 1;
    }

    @Override
    protected void init() {
        super.init();

        int offset = (int) (Math.sqrt(animationTime * animationTime) * 12 - 10);
        int bw = 100;

        addButton(new PlainColorButton(width - offset,40,bw,20,new TranslatableText("pause.back_to_game").styled(ClientMod.FONT_APPLIER),(b)->{
            dir = -1;
            animationTime = 9;
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
        addButton(new PlainColorButton(width - offset,65,bw,20,new TranslatableText("gui.title.challenges").formatted(Formatting.DARK_PURPLE).styled(ClientMod.FONT_APPLIER),(b)->{
            client.openScreen(new ChallengesScreen(false));
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
        addButton(new PlainColorButton(width - offset,90,bw,20,new TranslatableText("gui.title.handipass").styled(ClientMod.FONT_APPLIER),(b)->{
            client.openScreen(new HandiPassScreen(false));
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
        addButton(new PlainColorButton(width - offset,115,bw,20,new TranslatableText("gui.title.locker").styled(ClientMod.FONT_APPLIER),(b)->{
            client.openScreen(new LockerScreen(false));
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
        addButton(new PlainColorButton(width - offset,140,bw,20,new TranslatableText("gui.title.options").styled(ClientMod.FONT_APPLIER),(b)->{
            client.openScreen(new OptionsScreen(this,client.options));
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
        addButton(new PlainColorButton(width - offset,165,bw,20,new TranslatableText("pause.stats").styled(ClientMod.FONT_APPLIER),(b)->{
            client.openScreen(new StatsScreen(this,client.player.getStatHandler()));
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
        addButton(new PlainColorButton(width - offset,190,bw,20,client.isInSingleplayer() ? new TranslatableText("pause.return_to_menu").styled(ClientMod.FONT_APPLIER) : new TranslatableText("pause.disconnect").styled(ClientMod.FONT_APPLIER),(b)->{
            boolean sp = this.client.isInSingleplayer();
            boolean realms = this.client.isConnectedToRealms();
            this.client.world.disconnect();
            if (sp) {
                this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
            } else {
                this.client.disconnect();
            }

            if (realms) {
                RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
                realmsBridgeScreen.switchToRealms(new TitleScreen());
            } else {
                this.client.openScreen(new NewTitleScreen(true,true));
            }
        },false,0xaacccccc,0,0xffffffff,0,0xffff11ff,0xcccccccc,20));
    }

    @Override
    public void tick() {
        super.tick();
        if (animationTime < 10 && animationTime > 0) {
            animationTime += dir;
            buttons.forEach(b->b.x = width - (int) (Math.sqrt(animationTime * animationTime) * 12) + 10);
        }

        if (animationTime <= 0) {
            client.openScreen(null);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int offset = (int) (Math.sqrt(animationTime * animationTime) * 12);
        fill(matrices,width - offset,0,width,height,0xaa2b2b2b);

        matrices.push();
        Text t = new LiteralText("SEASON " + CommonMod.SEASON).styled(ClientMod.FONT_APPLIER);
        matrices.translate(width - offset + 20, 10, 0.0F);
        matrices.scale(2,2,2);

        textRenderer.draw(matrices,t,0,0,-1);
        matrices.pop();

        client.getTextureManager().bindTexture(HANDICRACK_LOGO);
        drawTexture(matrices,width - offset + 10,225,100,30,0,0,1357,451,1357,451);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        dir *= -1;
        animationTime = 9;
    }
}
