/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class HandiTagsData<T> extends AbstractTagProvider<T> {
    private final String name;
    private final Consumer<HandiTagsData<T>> generator;

    protected HandiTagsData(DataGenerator root, Registry<T> registry, String name, Consumer<HandiTagsData<T>> generator) {
        super(root, registry);
        this.name = name;
        this.generator = generator;
    }

    @Override
    protected void configure() {
        generator.accept(this);
    }

    @Override
    protected Path getOutput(Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/" + name + "/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return name + " Tags";
    }


    public ObjectBuilder<T> make(Tag.Identified<T> tag) {
        return getOrCreateTagBuilder(tag);
    }

    public Tag.Builder getBuilder(Tag.Identified<T> tag) {
        return method_27169(tag);
    }

    public  <E> void addAll(Map<Tag.Identified<T>, List<E>> map, Function<E,T> converter) {
        for (Map.Entry<Tag.Identified<T>, List<E>> e : map.entrySet()) {
            make(e.getKey()).add((T[]) e.getValue().stream().map(converter).toArray());
        }
    }
}
