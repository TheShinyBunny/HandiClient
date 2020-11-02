/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PacketBuilder {

    private Identifier id;
    private PacketByteBuf buf;

    public PacketBuilder(Identifier id) {
        this.id = id;
        this.buf = new PacketByteBuf(Unpooled.buffer());
    }

    public static PacketBuilder of(Identifier id) {
        return new PacketBuilder(id);
    }

    public PacketBuilder write(Object... values) {
        return this;
    }
}
