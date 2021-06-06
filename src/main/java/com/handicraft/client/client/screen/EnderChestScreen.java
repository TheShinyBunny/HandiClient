/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.CommonMod;
import com.handicraft.client.client.widget.HoldableImageButton;
import com.handicraft.client.client.widget.ImageButton;
import com.handicraft.client.screen.EnderChestScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class EnderChestScreen extends HandledScreen<EnderChestScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final Identifier ICONS = new Identifier("hcclient:textures/gui/enderchest.png");
    private static final Text STORE_TOOLTIP = new TranslatableText("gui.enderchest.store");
    private static final Text REDRAW_TOOLTIP = new TranslatableText("gui.enderchest.redraw");

    private ButtonWidget addButton;
    private ButtonWidget takeButton;

    public EnderChestScreen(EnderChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;
        this.backgroundHeight = 114 + 3 * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        addButton = addDrawableChild(new HoldableImageButton(x + backgroundWidth + 16,y + backgroundHeight / 2 - 40,20,20,0,0,20,ICONS,20,20,40,60,b->{
            int points = handler.addXP();
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(points);
            ClientPlayNetworking.send(CommonMod.CHANGE_STORED_XP,buf);
        },STORE_TOOLTIP,50));
        takeButton = addDrawableChild(new HoldableImageButton(x + backgroundWidth + 16,y + backgroundHeight / 2,20,20,20,0,20,ICONS,20,20,40,60, b->{
            int points = handler.takeXP();
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(-points);
            ClientPlayNetworking.send(CommonMod.CHANGE_STORED_XP,buf);
        },REDRAW_TOOLTIP,50));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        addButton.active = handler.getPlayer().experienceLevel > 0;
        takeButton.active = handler.getStoredXP() > 0;

        drawCenteredText(matrices,textRenderer,new LiteralText(handler.getStoredLevels() + "").formatted(Formatting.GREEN),x + backgroundWidth + 26,y + backgroundHeight / 2 - 18,0x00ffffff);

        client.getTextureManager().bindTexture(ICONS);
        drawTexture(matrices,x + backgroundWidth + 7,y + backgroundHeight / 2 - 10,2,48,36,2,40,60);
        drawTexture(matrices,x + backgroundWidth + 7,y + backgroundHeight / 2 - 10,2,52, (int) (handler.getProgress() * 36),2,40,60);
        itemRenderer.renderGuiItemIcon(new ItemStack(Items.EXPERIENCE_BOTTLE),x + backgroundWidth + 46,y + backgroundHeight / 2 - 18);

        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, 3 * 18 + 17);
        this.drawTexture(matrices, i, j + 3 * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (hoveredElement(mouseX, mouseY).filter(e->e.mouseReleased(mouseX, mouseY, button)).isPresent()) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        hoveredElement(x,y).ifPresent(e->{
            if (e instanceof ImageButton) {
                renderTooltip(matrices,((ImageButton) e).getTooltip(),x,y);
            }
        });
    }
}
