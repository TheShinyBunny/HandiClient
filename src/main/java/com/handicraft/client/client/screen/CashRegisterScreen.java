/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.screen.cash_register.CashRegisterScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CashRegisterScreen extends HandledScreen<CashRegisterScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("hcclient:textures/gui/container/cash_register.png");
    private TextFieldWidget passwordField;
    private Text loginReject;

    public CashRegisterScreen(CashRegisterScreenHandler handler, PlayerInventory inventory, Text title) {
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
        addChild(passwordField = new TextFieldWidget(textRenderer,x + backgroundWidth / 2 - 70,y - 22,100,20,new TranslatableText("container.cash_register.password")));
        addButton(new ButtonWidget(x + backgroundWidth / 2 + 35,y - 21,80,18,new TranslatableText("container.cash_register.admin.label"),b->{
            loginReject = null;
            if (passwordField.getText().isEmpty()) {
                loginReject = new TranslatableText("container.cash_register.password_empty");
            } else {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeString(passwordField.getText());
                ClientSidePacketRegistry.INSTANCE.sendToServer(CashRegisterScreenHandler.ADMIN_LOGIN, buf);
            }
        }));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return this.passwordField.keyPressed(keyCode, scanCode, modifiers) || this.passwordField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void onInvalidPassword() {
        loginReject = new TranslatableText("container.cash_register.invalid_password");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        passwordField.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (loginReject != null) {
            drawTextWithShadow(matrices,textRenderer,loginReject,x + backgroundWidth / 2,y - 40,0xffff1111);
        }
    }


}
