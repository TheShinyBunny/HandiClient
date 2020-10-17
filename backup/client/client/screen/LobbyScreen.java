/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import com.handicraft.client.challenge.client.ChallengesScreen;
import com.handicraft.client.challenge.client.ClientChallengesManager;
import com.handicraft.client.client.ClientMod;
import com.handicraft.client.client.widget.PlainColorButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class LobbyScreen<S extends LobbyScreen<S>> extends Screen implements TransitionedScreen, TabProvider<S> {

    private final boolean tabs;
    protected ScreenTransition transition = new ScreenTransition();

    private static List<TabButton> tabButtons = Util.make(new ArrayList<>(), l->{
        l.add(new TabButton("gui.title.lobby",new NewTitleScreen(false)));
        l.add(new TabButton("gui.title.handipass", new HandiPassScreen()));
        l.add(new TabButton("gui.title.locker",s->{}));
        l.add(new TabButton("gui.title.challenges", new ChallengesScreen(true)));
        l.add(new TabButton("gui.title.news", t->{}));
        l.add(new TabButton("gui.title.options", s->s.transition.start(new OptionsScreen(s,MinecraftClient.getInstance().options),500)));
    });


    protected LobbyScreen(boolean tabs) {
        super(LiteralText.EMPTY);
        this.tabs = tabs;
    }

    @Override
    protected void init() {
        if (tabs) {
            int bwidth = -10;
            for (TabButton b : tabButtons) {
                bwidth += 10 + b.getWidth();
            }

            int offset = 0;
            for (TabButton b : tabButtons) {
                //addButton(new ImageButton(width / 2 - bwidth / 2 + offset,10,b.width / 3,20,0,0,110,b.texture,b.width,63,b.width,171,x->b.action.run()));
                addButton(new PlainColorButton(width / 2 - bwidth / 2 + offset, 10, b.getWidth(), 20, b.text, x -> b.action.accept(this), b.isSelected(this), 0x2bbfbfbf, 0xffffde00, 0x00ffffff, 0, 0x00ffff00, 0x2bbfbfbf));
                offset += 10 + b.getWidth();
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        float opacity = transition.getOpacity();
        fill(matrices,0,0,width,height, BackgroundHelper.ColorMixer.getArgb((int) (opacity * 255),0,0,0));
    }

    @Override
    public ScreenTransition getTransition() {
        return transition;
    }

    private static class TabButton {
        private TabProvider<?> provider;
        private Text text;
        private Consumer<LobbyScreen<?>> action;

        public TabButton(String textKey, TabProvider<?> provider) {
            this(textKey,s->{
                Screen ns = provider.create(s);
                if (ns != null) {
                    s.transition.start(ns, 500);
                }
            });
            this.provider = provider;
        }

        public TabButton(String textKey, Consumer<LobbyScreen<?>> action) {
            this.text = new TranslatableText(textKey).styled(s->s.withFont(ClientMod.NOTO));
            this.action = action;
        }

        public int getWidth() {
            return MinecraftClient.getInstance().textRenderer.getWidth(text.asOrderedText()) + 20;
        }

        public boolean isSelected(LobbyScreen<?> current) {
            return provider != null && provider.isSelected(current);
        }
    }
}
