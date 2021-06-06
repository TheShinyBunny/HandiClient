/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.client.FakePlayer;
import com.handicraft.client.client.widget.ImageButton;
import com.handicraft.client.client.widget.PlainColorButton;
import com.handicraft.client.client.widget.RewardWidget;
import com.handicraft.client.collectibles.ClientCollectibleCache;
import com.handicraft.client.rewards.CollectibleReward;
import com.handicraft.client.rewards.Reward;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HandiPassScreen extends LobbyScreen<HandiPassScreen> {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("hcclient:textures/gui/handipass/background.png");
    private static final Identifier ARRWOS = new Identifier("hcclient:textures/gui/handipass/arrows.png");

    public final FakePlayer player;
    public boolean backgroundVisible = true;
    public float scaleY;
    private List<Consumer<MatrixStack>> renderJobs = new ArrayList<>();
    private List<Runnable> tickJobs = new ArrayList<>();
    private ButtonWidget claim;

    private Reward selected;
    private Reward previewed;

    private int viewingTicks;

    private static final int[] levels = {5,10,15,20,25,30,35,45,55,65};

    public static HandiPassScreen CURRENT;

    protected HandiPassScreen(boolean tabs) {
        super(tabs);
        player = FakePlayer.create();
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
        CURRENT = this;
        float scalex = width / 1920f;
        float scaley = height / 1080f;

        float xOffset = 56 * scalex;
        for (int lvl : levels) {
            float yoff = 380 * scaley;
            Reward r = Reward.getByLevel(lvl);
            if (r != null) {
                addDrawableChild(new RewardWidget((int)(xOffset + 35 * scalex),(int)(yoff), (int) (115 * scalex), (int) (r.getTextureHeight() * scaley),r,this));
            }
            xOffset += 180 * scalex;
        }

        claim = addDrawableChild(new PlainColorButton((int) (480 * scalex), (int) (height - 150 * scaley),100,30,new TranslatableText("handipass.claim"), b->{
            claimReward();
            b.visible = false;
        },false,0xff66ff44,0,-1,0,-1,0xff33ff31,0));
        claim.visible = selected != null && ClientCollectibleCache.needsClaim(selected);
        if (selected instanceof CollectibleReward || MinecraftClient.getInstance().world != null) {
            claim.setMessage(new TranslatableText("handipass.claim"));
            claim.active = true;
        } else {
            claim.setMessage(new TranslatableText("handipass.claim.ingame"));
            claim.active = false;
        }
    }


    private void claimReward() {
        if (MinecraftClient.getInstance().world == null) {
            if (selected instanceof CollectibleReward) {
                ClientCollectibleCache.claimOffline((CollectibleReward<?>) selected);
            }
        } else {
            ClientCollectibleCache.claim(selected);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        float scalex = width / 1920f;
        float scaley = height / 720f;

        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        if (backgroundVisible) {
            drawTexture(matrices, 0, 0, width, height, 0, 0, 1920, 1080, 1920, 1080);
        } else {
            drawTexture(matrices,(int)(208 * scalex),(int)(233 * scaley),(int)(904 * scalex),(int)(258 * scaley),206,349,1110 - 206,732 - 349,1920,1080);
        }


        player.bodyYaw = player.prevBodyYaw += 1 * delta * 2;
        player.prevHeadYaw = player.headYaw += 1 * delta * 2;
        player.prevCapeX = player.capeX = 0;
        player.prevCapeY = player.capeY = -30;
        player.prevCapeZ = player.capeZ = 0;

        player.bodyYaw = MathHelper.wrapDegrees(player.bodyYaw);
        player.headYaw = MathHelper.wrapDegrees(player.headYaw);

        player.render(matrices,width / 2f,height,width / 8f,height / 4f);

        if (previewed != null && previewed != selected) {
            textRenderer.drawTrimmed(new LiteralText(previewed.getName()),this.width / 2  + 30,height - 30, 300, 0);
            previewed.selectTick(this,viewingTicks);
            viewingTicks++;
        } else if (selected != null) {
            textRenderer.drawTrimmed(new LiteralText(selected.getName()), this.width / 2 + 30,height - 30, 300, 0);
            selected.selectTick(this,viewingTicks);
            viewingTicks++;
            if(ClientCollectibleCache.wasClaimed(selected)) {
                claim.visible = false;
            }
        } else {
            viewingTicks = 0;
        }

        super.render(matrices, mouseX, mouseY, delta);

        renderJobs.forEach(r->r.accept(matrices));
        renderJobs.clear();
    }

    @Override
    public void tick() {
        super.tick();
        tickJobs.forEach(Runnable::run);
        tickJobs.clear();
    }

    @Override
    public HandiPassScreen create(Screen parent) {
        return new HandiPassScreen(true);
    }

    @Override
    public void removed() {
        super.removed();
        if (CURRENT == this) {
            CURRENT = null;
        }
    }

    @Override
    public boolean isSelected(LobbyScreen<?> current) {
        return current instanceof HandiPassScreen;
    }

    public void setSelected(Reward reward) {
        if (selected != null && selected != reward) {
            selected.onDeselect(this);
        }
        this.selected = reward;
        if (selected != null) {
            selected.onSelect(this);
        }
        if (ClientCollectibleCache.needsClaim(reward)) {
            claim.visible = true;
            if (reward instanceof CollectibleReward || MinecraftClient.getInstance().world != null) {
                claim.setMessage(new TranslatableText("handipass.claim"));
                claim.active = true;
            } else {
                claim.setMessage(new TranslatableText("handipass.claim.ingame"));
                claim.active = false;
            }
        } else {
            claim.visible = false;
        }
    }

    public void setPreviewed(Reward reward) {
        if (this.previewed != null && this.previewed != reward && this.previewed != selected) {
            this.previewed.onDeselect(this);
        }
        if (this.previewed != null && this.selected != null && this.selected != this.previewed) {
            selected.onSelect(this);
        }
        this.previewed = reward;
        if (this.previewed != null) {
            if (this.selected == null) {
                this.previewed.onSelect(this);
            } else if (this.selected != reward) {
                this.selected.onDeselect(this);
                this.previewed.onSelect(this);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scaleY += amount * 0.1;
        System.out.println("scaley:" + scaleY);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public Reward getSelected() {
        return selected;
    }

    @Override
    public boolean isPauseScreen() {
        return MinecraftClient.getInstance().world == null;
    }

    public void reload() {
        init(MinecraftClient.getInstance(),MinecraftClient.getInstance().getWindow().getScaledWidth(),MinecraftClient.getInstance().getWindow().getScaledHeight());
    }
}
