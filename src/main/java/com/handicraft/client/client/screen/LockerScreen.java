/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.client.widget.CollectibleWidget;
import com.handicraft.client.collectibles.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class LockerScreen extends LobbyScreen<LockerScreen> {

    private static final Identifier BACKGROUND = new Identifier("hcclient:textures/gui/locker_background.png");

    public LockerScreen(boolean tabs) {
        super(tabs);
    }

    @Override
    protected void init() {
        super.init();

        float scaley = height / 720f;
        int offset = (int) (190 * scaley);
        for (CollectibleType t : CollectibleType.all()) {
            int width = 80;
            addDrawableChild(new CollectibleWidget(t,null,width,offset,b->{
                select(t,null);
            }));
            width += 60;
            for (Collectible c : ClientCollectibleCache.getOwned(t)) {
                addDrawableChild(new CollectibleWidget(t,c,width,offset,b->{
                    select(t,c);
                }));
                width += 60;
            }
            offset += 240 * scaley;
        }
    }

    private void select(CollectibleType type, @Nullable Collectible c) {
        if (MinecraftClient.getInstance().world == null) {
            ClientCollectibleCache.selectOffline(type,c);
        } else {
            ClientCollectibleCache.select(type,c);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeEnumConstant(type);
            if (c == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeVarInt(Collectibles.REGISTRY.getRawId(c));
            }
            ClientSidePacketRegistry.INSTANCE.sendToServer(PlayerCollectibles.SELECT_COLLECTIBLE,buf);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        client.getTextureManager().bindTexture(BACKGROUND);
        drawTexture(matrices,0,0,width,height,0,0,1920,1080,1920,1080);

        float scaley = height / 720f;
        int offset = (int) (scaley * 250);

        for (CollectibleType t : CollectibleType.all()) {
            drawTextWithShadow(matrices,textRenderer,new LiteralText("").append(t.getName()).formatted(Formatting.BOLD).append(":"),20,offset,-1);
            offset += scaley * 240;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public LockerScreen create(Screen parent) {
        return new LockerScreen(true);
    }

    @Override
    public boolean isSelected(LobbyScreen<?> current) {
        return current instanceof LockerScreen;
    }
}
