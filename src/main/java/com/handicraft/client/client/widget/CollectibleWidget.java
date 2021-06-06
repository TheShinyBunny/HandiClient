/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.widget;

import com.handicraft.client.collectibles.ClientCollectibleCache;
import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.CollectibleType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class CollectibleWidget extends ButtonWidget {

    private static final Identifier NONE = new Identifier("hcclient:textures/gui/locker_none.png");

    private CollectibleType type;
    private final Collectible collectible;

    public CollectibleWidget(CollectibleType type, Collectible c, int x, int y, PressAction onPress) {
        super(x,y,50,80, LiteralText.EMPTY,onPress);
        this.type = type;
        this.collectible = c;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (collectible == null) {
            if (ClientCollectibleCache.isNoneSelected(type)) {
                fill(matrices, x - 2, y - 2, x + width + 2, y + height + 2, 0xffffff22);
            }

            MinecraftClient.getInstance().getTextureManager().bindTexture(NONE);
            drawTexture(matrices, x, y, width, height, 0,0, 89, 138, 89, 138);
        } else {
            if (ClientCollectibleCache.isSelected(collectible)) {
                fill(matrices, x - 2, y - 2, x + width + 2, y + height + 2, 0xffffff22);
            }

            MinecraftClient.getInstance().getTextureManager().bindTexture(collectible.getDisplayTexture());
            drawTexture(matrices, x, y, width, height, 0,0, 89,138, 89, 138);
        }
    }
}
