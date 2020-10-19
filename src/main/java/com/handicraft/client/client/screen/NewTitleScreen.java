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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;


public class NewTitleScreen extends LobbyScreen<NewTitleScreen> {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("hcclient:textures/gui/title/background.png");
    public static NewTitleScreen CURRENT;
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
        ClientMod.pingServer();
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
            client.openScreen(new ConnectScreen(this,client, ClientMod.HANDICRAFT_SERVER));
        }));

        int yoff = height / 2;
        int x = (int) (30 * swidth);
        int rwidth = (int) (115 * swidth);
        float rheight = height / 720f;
        for (Reward r : ClientCollectibleCache.getNextRewards()) {
            addButton(new RewardWidget(x,yoff,rwidth,(int)((r.getTextureHeight() == 89 ? 80 : 130) * rheight),r,()->{
                transition.start(new HandiPassScreen(true,r),500);
            }));
            yoff += 100 * sheight;
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

        float swidth = width / 1920f;
        float sheight = height / 720f;

        matrices.push();
        matrices.scale(2.5f,2.5f,1);
        textRenderer.draw(matrices, new LiteralText("" + ClientCollectibleCache.getPassLevel()).styled(s->ClientMod.VANILLA_GUI ? s : s.withFont(ClientMod.ALMONI)),(int)(110 * swidth),(int)(110 * sheight),-1);
        matrices.pop();

        float f = (float)Math.atan(-(mouseX - width / 2d) / 40.0F);
        float g = (float)Math.atan(-(mouseY - height / 2.1) / 40.0F);


        player.bodyYaw = player.prevBodyYaw = 180 + f * 20;
        player.prevHeadYaw = player.headYaw = 180 + f * 40;
        player.prevPitch = player.pitch;
        player.pitch = -g * 20.0F;

        player.render(width / 2f, height / 1.3f,width / 8f,height / 4f);

        if (!ClientMod.handicraftOnline.get()) {
            drawCenteredText(matrices, textRenderer, new LiteralText("HandiCraft is currently Offline.").formatted(Formatting.RED), width / 2, height - 50, 0xffffffff);
        }

        super.render(matrices, mouseX, mouseY, delta);
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
