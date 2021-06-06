/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class PacketBuilder {

    public static PacketByteBuf create() {
        return new PacketByteBuf(Unpooled.buffer());
    }

}
