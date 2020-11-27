/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.item.ModItems;
import com.handicraft.client.screen.cash_register.CashRegisterOwnerHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class CashRegisterOwnerScreen extends HandledScreen<CashRegisterOwnerHandler> {
    private static final Identifier TEXTURE = new Identifier("hcclient:textures/gui/container/cash_register.png");
    private TextFieldWidget costField;
    private ButtonWidget remove;
    private TextFieldWidget passwordField;

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
        addChild(passwordField = new TextFieldWidget(textRenderer,x + backgroundWidth / 2 - 70,y - 22,100,20,new TranslatableText("container.cash_register.password")));
        passwordField.setChangedListener(pw->{
            ClientSidePacketRegistry.INSTANCE.sendToServer(CashRegisterOwnerHandler.CHANGE_PASSWORD,new PacketByteBuf(Unpooled.buffer()).writeString(pw));
        });
        passwordField.setTextPredicate(pw->pw != null && !pw.isEmpty());
        passwordField.setText(handler.getPassword());
        addChild(costField = new TextFieldWidget(textRenderer,x + 8,y + 53,48,18,new TranslatableText("container.cash_register.cost")));
        costField.setText("" + handler.cost);
        costField.setChangedListener(cost->{
            try {
                int c = Integer.parseInt(cost);
                if (c < 0) {
                    costField.setText("0");
                    return;
                }
                handler.cost = Integer.parseInt(cost);
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeVarInt(handler.cost);
                ClientSidePacketRegistry.INSTANCE.sendToServer(CashRegisterOwnerHandler.UPDATE_COST, buf);
            } catch (Exception ignored) {}
        });
        ButtonWidget claim = addButton(new ButtonWidget(x + 10,y + 105,46,18,new TranslatableText("container.cash_register.claim"),b->{
            client.interactionManager.clickButton(handler.syncId,0);
            handler.clearProfits();
            b.active = false;
            remove.active = handler.canRemove();
        }));
        claim.active = handler.getProfits() > 0;
        this.remove = addButton(new ButtonWidget(x + backgroundWidth + 2,y,70,20,new TranslatableText("container.cash_register.remove"),b->{
            client.interactionManager.clickButton(handler.syncId,1);
        }));
        remove.active = handler.canRemove();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return this.passwordField.keyPressed(keyCode, scanCode, modifiers) || this.passwordField.isActive() || this.costField.keyPressed(keyCode, scanCode, modifiers) || this.costField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        remove.active = handler.canRemove();
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        passwordField.render(matrices, mouseX, mouseY, delta);
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
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);
        textRenderer.draw(matrices,new TranslatableText("container.cash_register.cost"),10,40,0);
        int pw = textRenderer.getWidth("Profits:");
        textRenderer.draw(matrices,"Profits:",32 - pw / 2f,80,0);
        String text = (handler.getProfits() / 9) + "";
        if (handler.getProfits() > 0 && handler.getProfits() < 9) {
            text = "1 >";
        }
        int textW = textRenderer.getWidth(text);
        int w = textW + 11;
        textRenderer.draw(matrices,text,32 - (w / 2f),92,0xffee1111);
        itemRenderer.renderGuiItemIcon(new ItemStack(ModItems.RUBY),32 - (w / 2) + textW + 2,87);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        if (remove.x < x && remove.x + remove.getWidth() > x && remove.y < y && remove.y + remove.getHeight() > y && !remove.active) {
            renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("container.cash_register.cannot_remove"),100),x,y);
        }
    }
}
