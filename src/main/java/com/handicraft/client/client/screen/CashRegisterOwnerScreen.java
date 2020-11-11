/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.screen.cash_register.CashRegisterOwnerHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CashRegisterOwnerScreen extends HandledScreen<CashRegisterOwnerHandler> {
    private static final Identifier TEXTURE = new Identifier("hcclient:textures/gui/container/cash_register.png");
    private TextFieldWidget costField;

    public CashRegisterOwnerScreen(CashRegisterOwnerHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;
        this.backgroundHeight = 216;
        this.backgroundWidth = 226;
        this.playerInventoryTitleY = 127;
        this.playerInventoryTitleX = 35;
    }

    @Override
    protected void init() {
        super.init();

        addChild(costField = new TextFieldWidget(textRenderer,x + 9,y + 53,48,18,new TranslatableText("container.cash_register.cost")));
        costField.setText("" + handler.cost);
        costField.setChangedListener(cost->{
            try {
                handler.cost = Integer.parseInt(cost);
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeVarInt(handler.cost);
                ClientSidePacketRegistry.INSTANCE.sendToServer(CashRegisterOwnerHandler.UPDATE_COST,buf);
            } catch (Exception ignored) {}
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return this.costField.keyPressed(keyCode, scanCode, modifiers) || this.costField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        costField.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        textRenderer.draw(matrices,"Profits:",x + 13, y + 95,0);
        drawCenteredString(matrices,textRenderer, handler.getProfits() + "$",x + 32,y + 108,0xff00ff22);
    }
}
