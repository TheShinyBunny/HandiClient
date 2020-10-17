/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.client.FakePlayer;
import com.handicraft.client.client.widget.ImageButton;
import com.handicraft.client.client.widget.RewardWidget;
import com.handicraft.client.rewards.Reward;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HandiPassScreen extends LobbyScreen<HandiPassScreen> {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("hcclient:textures/gui/handipass/background.png");
    private static final Identifier ARRWOS = new Identifier("hcclient:textures/gui/handipass/arrows.png");

    public final FakePlayer player;
    private int rewardPage = 0;
    private int maxPages;
    private List<Consumer<MatrixStack>> renderJobs = new ArrayList<>();
    private List<Runnable> tickJobs = new ArrayList<>();
    private ButtonWidget left;
    private ButtonWidget right;

    protected HandiPassScreen() {
        super(true);
        player = FakePlayer.create();
        maxPages = (Reward.REGISTRY.stream().map(Reward::getLevel).max(Integer::compareTo).orElse(1) - 1) / 5;
    }

    public void addRenderJob(Consumer<MatrixStack> action) {
        renderJobs.add(action);
    }

    public void addTickJob(Runnable action) {
        tickJobs.add(action);
    }

    @Override
    public void init() {
        super.init();
        float scalex = width / 1920f;
        float scaley = height / 720f;

        float xOffset = 216 * scalex;
        for (int i = 1; i <= 5; i++) {
            float yoff = 276 * scaley;
            for (Reward r : Reward.getByLevel(rewardPage * 5 + i)) {
                addButton(new RewardWidget((int)(xOffset + 35 * scalex),(int)(yoff), (int) (115 * scalex),(int)((r.getTextureHeight() == 89 ? 80 : 130) * scaley),r,this));
                yoff += 100 * scaley;
            }
            xOffset += 177 * scalex;
        }

        left = addButton(new ImageButton((int) ((width / 1920f) * 20),height / 2,40,40,0,0,0,ARRWOS,87,90,174,90,b->{
            rewardPage = MathHelper.clamp(rewardPage - 1,0,maxPages);
            init(client,width,height);
        }));
        right = addButton(new ImageButton((int) ((width / 1920f) * 1150),height / 2,40,40,87,0,0,ARRWOS,87,90,174,90, b->{
            rewardPage = MathHelper.clamp(rewardPage + 1,0,maxPages);
            init(client,width,height);
        }));
        left.visible = rewardPage > 0;
        right.visible = rewardPage < maxPages;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexture(matrices, 0, 0, width, height, 0, 0, 1920, 1080, 1920, 1080);

        float scalex = width / 1920f;
        float scaley = height / 720f;

        float xOffset = 216 * scalex;

        for (int i = 1; i <= 5; i++) {
            drawCenteredText(matrices, textRenderer, new LiteralText(i + ""), (int) (xOffset + 83f * scalex), (int) (240f * scaley), 0xffffffff);
            xOffset += 177 * scalex;
        }


        player.bodyYaw = ++player.prevBodyYaw;
        player.prevHeadYaw = ++player.headYaw;
        player.prevCapeX = player.capeX = 0;
        player.prevCapeY = player.capeY = -30;
        player.prevCapeZ = player.capeZ = 0;

        player.bodyYaw = MathHelper.wrapDegrees(player.bodyYaw);
        player.headYaw = MathHelper.wrapDegrees(player.headYaw);

        player.render(width / 1.2f,height / 1.3f,width / 8f,height / 4f);

        renderJobs.forEach(r->r.accept(matrices));
        renderJobs.clear();

        super.render(matrices, mouseX, mouseY, delta);


    }

    @Override
    public void tick() {
        super.tick();
        tickJobs.forEach(Runnable::run);
        tickJobs.clear();
    }

    @Override
    public HandiPassScreen create(Screen parent) {
        return new HandiPassScreen();
    }

    @Override
    public boolean isSelected(LobbyScreen<?> current) {
        return current instanceof HandiPassScreen;
    }
}
