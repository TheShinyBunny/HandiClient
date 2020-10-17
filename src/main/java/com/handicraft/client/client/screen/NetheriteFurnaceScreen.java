/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.screen.NetheriteFurnaceScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NetheriteFurnaceScreen extends HandledScreen<NetheriteFurnaceScreenHandler> {

    private static final Identifier BACKGROUND = new Identifier("hcclient:textures/gui/netherite_furnace.png");

    public NetheriteFurnaceScreen(NetheriteFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 168;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        titleY = 2;
        playerInventoryTitleY = 75;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int l;
        if (this.handler.isBurning()) {
            l = this.handler.getFuelProgress();
            this.drawTexture(matrices, i + 78, j + 45 + 12 - l, 176, 12 - l, 14, l + 1);
        }

        int offset = 16;
        for (int m = 0; m < 3; m++) {
            l = handler.getCookProgress(m);
            this.drawTexture(matrices, i + 39, j + offset, 176, 29, l, 10);
            offset += 23;
        }
        offset = 16;
        for (int m = 3; m < 6; m++) {
            l = handler.getCookProgress(m);
            this.drawTexture(matrices, i + 118 + (12 - l), j + offset, 176 + (12 - l), 16, l + 1, 10);
            offset += 23;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices,mouseX,mouseY);
    }
}
