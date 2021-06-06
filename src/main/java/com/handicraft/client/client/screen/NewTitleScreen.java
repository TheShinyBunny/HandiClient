/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.client.ClientMod;
import com.handicraft.client.client.FakePlayer;
import com.handicraft.client.client.widget.InvisibleButtonWidget;
import com.handicraft.client.client.widget.RewardWidget;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import com.handicraft.client.rewards.Reward;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;


public class NewTitleScreen extends LobbyScreen<NewTitleScreen> {

    public static NewTitleScreen CURRENT;
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("hcclient:textures/gui/title/background.png");
    private static final Identifier JOIN_TEXTURE = new Identifier("hcclient:textures/gui/title/background_join.png");
    private static final Identifier SINGLEPLAYER_TEXTURE = new Identifier("hcclient:textures/gui/title/background_singleplayer.png");

    private FakePlayer player;
    private ButtonWidget join;
    private ButtonWidget singleplayer;
    private ButtonWidget missions;
    private int rotZ, rotX, rotY;

    public NewTitleScreen(boolean fadeIn, boolean ping) {
        super(true);
        if (fadeIn) {
            transition.scheduleStart(1000);
        }
        this.player = FakePlayer.create();
        if (ping) {
            ClientMod.pingServer();
        }
    }

    @Override
    protected void init() {
        super.init();
        CURRENT = this;
        float swidth = width / 1920f;
        float sheight = height / 1080f;

        singleplayer = addDrawableChild(new InvisibleButtonWidget((int) (width - 324 * swidth), (int) (height - 262 * sheight), (int) (268 * swidth), (int) (88 * sheight),(b) -> {
            this.client.openScreen(new SelectWorldScreen(this));
        }));
        join = addDrawableChild(new InvisibleButtonWidget((int) (width - 324 * swidth), (int) (height - 172 * sheight), (int) (267 * swidth), (int) (154 * sheight),(b)->{
            ConnectScreen.connect(this, client, ClientMod.SERVER_ADDRESS, ClientMod.HANDICRAFT_SERVER);
        }));
        //missions = addDrawableChild(new InvisibleButtonWidget(130 * swidth,594 * sheight,))
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
        RenderSystem.setShaderTexture(0, texture);
        drawTexture(matrices, 0, 0, width, height, 0, 0, 1920, 1080, 1920, 1080);

        float f = (float)Math.atan(-(mouseX - width / 2d) / 40.0F);
        float g = (float)Math.atan(-(mouseY - height / 2.1) / 40.0F);


        player.bodyYaw = player.prevBodyYaw = 180 + f * 20;
        player.prevHeadYaw = player.headYaw = 180 + f * 40;
        player.prevPitch = player.getPitch();
        player.setPitch(-g * 20.0F);

        matrices.push();
        matrices.translate(width / 2.5f, height / 1.1f, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(82));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(30));
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(75));

        player.render(matrices,0,0,width / 8f,height / 4f);

        matrices.pop();

        if (!ClientMod.handicraftOnline.get()) {
            drawCenteredText(matrices, textRenderer, new LiteralText("HandiCraft is currently Offline.").formatted(Formatting.RED), width / 2, height - 50, 0xffffffff);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (Screen.hasControlDown()) {
            rotX += amount;
            System.out.println("rotX: " + rotX);
        } else if (Screen.hasAltDown()) {
            rotZ += amount;
            System.out.println("rotZ: " + rotZ);
        } else if (Screen.hasShiftDown()) {
            rotY += amount;
            System.out.println("rotY: " + rotY);
        }
        return true;
    }

    @Override
    public NewTitleScreen create(Screen parent) {
        return this;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isSelected(LobbyScreen<?> current) {
        return current instanceof NewTitleScreen;
    }

    public void reload() {
        init(MinecraftClient.getInstance(),MinecraftClient.getInstance().getWindow().getScaledWidth(),MinecraftClient.getInstance().getWindow().getScaledHeight());
    }
}
