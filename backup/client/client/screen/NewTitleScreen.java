/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.client.FakePlayer;
import com.handicraft.client.client.widget.ImageButton;
import com.handicraft.client.client.widget.InvisibleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.net.UnknownHostException;


public class NewTitleScreen extends LobbyScreen<NewTitleScreen> {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("hcclient:textures/gui/title/background.png");
    private static final Identifier JOIN_TEXTURE = new Identifier("hcclient:textures/gui/title/background_join.png");
    private static final Identifier SINGLEPLAYER_TEXTURE = new Identifier("hcclient:textures/gui/title/background_singleplayer.png");

    private FakePlayer player;
    private ButtonWidget join;
    private ButtonWidget singleplayer;

    public NewTitleScreen(boolean fadeIn) {
        super(true);
        if (fadeIn) {
            transition.scheduleStart(1000);
        }
        this.player = FakePlayer.create();
    }

    @Override
    protected void init() {
        super.init();
        float swidth = width / 1920f;
        float sheight = height / 1080f;


        singleplayer = addButton(new InvisibleButtonWidget((int) (width - 324 * swidth), (int) (height - 262 * sheight), (int) (268 * swidth), (int) (88 * sheight),(b) -> {
            this.client.openScreen(new SelectWorldScreen(this));
        }));
        join = addButton(new InvisibleButtonWidget((int) (width - 324 * swidth), (int) (height - 172 * sheight), (int) (267 * swidth), (int) (154 * sheight),(b)->{
            client.openScreen(new ConnectScreen(this,client, CommonMod.HANDICRAFT_SERVER));
        }));

        /*if (!ClientChallengesManager.loadDefault()) {
            client.getToastManager().add(SystemToast.create(client, SystemToast.Type.PACK_LOAD_FAILURE, new TranslatableText("challenges.load.failure"), new TranslatableText("challenges.load.failure.desc")));
        }*/

        MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
        try {
            pinger.add(CommonMod.HANDICRAFT_SERVER,()->{
                client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_BACKUP,new LiteralText("Logged in to handicraft"),null));
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Identifier texture;
        if (singleplayer.isMouseOver(mouseX,mouseY)) {
            texture = SINGLEPLAYER_TEXTURE;
        } else if (join.isMouseOver(mouseX,mouseY)) {
            texture = JOIN_TEXTURE;
        } else {
            texture = BACKGROUND_TEXTURE;
        }
        client.getTextureManager().bindTexture(texture);
        drawTexture(matrices, 0, 0, width, height, 0, 0, 1920, 1080, 1920, 1080);

        float f = (float)Math.atan(-(mouseX - width / 2d) / 40.0F);
        float g = (float)Math.atan(-(mouseY - height / 2.1) / 40.0F);

        /*
        player.bodyYaw = 180 + f * 20;
        player.yaw = 180 + f * 40;
        player.pitch = -g * 20.0F;
        player.bodyYaw = player.yaw;
        player.prevBodyYaw = player.bodyYaw;
        player.prevHeadYaw = player.headYaw;
        player.headYaw = player.yaw;*/

        player.bodyYaw = player.prevBodyYaw = 180 + f * 20;
        player.prevHeadYaw = player.headYaw = 180 + f * 40;
        player.prevPitch = player.pitch;
        player.pitch = -g * 20.0F;

        //player.bodyYaw = MathHelper.wrapDegrees(player.bodyYaw);
        //player.headYaw = MathHelper.wrapDegrees(player.headYaw);

        player.render(width / 2f, height / 1.3f,width / 8f,height / 4f);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public ScreenTransition getTransition() {
        return transition;
    }


    @Override
    public NewTitleScreen create(Screen parent) {
        return this;
    }

    @Override
    public boolean isSelected(LobbyScreen<?> current) {
        return current instanceof NewTitleScreen;
    }
}
