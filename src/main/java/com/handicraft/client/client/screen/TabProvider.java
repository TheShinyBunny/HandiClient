/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client.screen;

import net.minecraft.client.gui.screen.Screen;

public interface TabProvider<S extends Screen> {

    S create(Screen parent);

    boolean isSelected(LobbyScreen<?> current);

}
