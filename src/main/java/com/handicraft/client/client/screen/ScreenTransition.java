/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class ScreenTransition {

    private Screen screen;
    private long start;
    private boolean willStart;
    private long time;

    public ScreenTransition() {

    }

    public void start(Screen screen, long time) {
        this.screen = screen;
        this.start = Util.getMeasuringTimeMs();
        this.time = time;
    }

    public void start(long time) {
        if (start == 0) {
            this.start = Util.getMeasuringTimeMs();
            this.time = time;
        }
    }

    public float getOpacity() {
        if (willStart) {
            start(time);
            willStart = false;
        }
        if (start == 0) return screen == null ? 0 : 1;
        float d = Util.getMeasuringTimeMs() - start;
        if (d > time) {
            if (screen != null) {
                MinecraftClient.getInstance().openScreen(screen);
                if (screen instanceof TransitionedScreen) {
                    ((TransitionedScreen) screen).getTransition().start(time);
                }
            }

            start = 0;
            float f = screen == null ? 0 : 1;
            screen = null;
            return f;
        }
        if (screen == null) {
            d = time - d;
        }
        return MathHelper.clamp(d / time,0,1);
    }


    public void scheduleStart(long time) {
        willStart = true;
        this.time = time;
    }
}
