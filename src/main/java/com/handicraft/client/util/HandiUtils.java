/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HandiUtils {

    private static final Pattern PLURAL_PATTERN = Pattern.compile("\\((s|es|ies|ves)\\)");

    public static String pluralize(int count, String name) {
        if (count == 1) {
            Matcher m = PLURAL_PATTERN.matcher(name);
            if (m.find()) {
                String s = m.group(1);
                String repl = "";
                if (s.equals("ies")) {
                    repl = "y";
                } else if (s.equals("ves")) {
                    repl = "f";
                }
                return replaceBetween(name,m.start(),m.end(),repl);
            }
            return name;
        } else {
            return name.replaceAll(PLURAL_PATTERN.pattern(),"$1");
        }
    }

    public static String replaceBetween(String input, int start, int end, String replacement) {
        return input.substring(0,start) + replacement + (input.length() < (end + 1) ? "" : input.substring(end + 1));
    }

    public static <T> void fill(List<T> list, int size, Supplier<T> valueSupplier) {
        for (int i = 0; i < size; i++) {
            list.add(valueSupplier.get());
        }
    }

    public static <T> NbtList toListTag(List<T> list, Function<T,NbtElement> tagFactory) {
        NbtList tag = new NbtList();
        for (T t : list) {
            tag.add(tagFactory.apply(t));
        }
        return tag;
    }

    public static <T> NbtList toRegistryListTag(List<T> list, Registry<T> registry) {
        return toListTag(list,t-> NbtString.of(registry.getId(t).toString()));
    }

    public static <T> List<T> fromListTag(NbtCompound tag, String key, int type, Function<NbtElement,T> itemFactory) {
        List<T> list = new ArrayList<>();
        NbtList lt = tag.getList(key,type);
        for (NbtElement t : lt) {
            T item = itemFactory.apply(t);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public static <T> List<T> fromRegistryListTag(NbtCompound tag, String key, Registry<T> registry) {
        return fromListTag(tag,key, NbtType.STRING,t->registry.get(new Identifier(t.asString())));
    }

    public static <T> void writeRegistryList(PacketByteBuf buf, List<T> list, Registry<? super T> registry) {
        writePacketList(buf,list,(i,b)->{
            b.writeVarInt(registry.getRawId(i));
        });
    }

    public static <T> void writePacketList(PacketByteBuf buf, List<T> list, BiConsumer<T,PacketByteBuf> itemWriter) {
        buf.writeVarInt(list.size());
        for (T t : list) {
            itemWriter.accept(t,buf);
        }
    }

    public static <T> List<T> readRegistryList(PacketByteBuf buf, Registry<T> registry) {
        return readPacketList(buf,b->registry.get(b.readVarInt()));
    }

    public static <T> List<T> readPacketList(PacketByteBuf buf, Function<PacketByteBuf,T> itemReader) {
        int size = buf.readVarInt();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T item = itemReader.apply(buf);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public static <K,V> Map<K, V> readMap(PacketByteBuf buf, Function<PacketByteBuf,K> keyReader, Function<PacketByteBuf,V> valueReader) {
        int size = buf.readVarInt();
        Map<K,V> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            K k = keyReader.apply(buf);
            V v = valueReader.apply(buf);
            map.put(k,v);
        }
        return map;
    }

    public static <K,V> void writeMap(PacketByteBuf buf, Map<K,V> map, BiConsumer<PacketByteBuf,K> keyWriter, BiConsumer<PacketByteBuf,V> valueWriter) {
        buf.writeVarInt(map.size());
        for (Map.Entry<K,V> e : map.entrySet()) {
            keyWriter.accept(buf,e.getKey());
            valueWriter.accept(buf,e.getValue());
        }
    }

    public static <E extends Enum<E>> E randomEnum(Random random, Class<E> enumClass) {
        E[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants[0] instanceof WeightedItem) {
            return WeightedPicker.getRandom(random,Arrays.stream(enumConstants).map(e->new WeightedEntry<E>(((WeightedItem)e).getWeight(),e)).collect(Collectors.toList())).orElseThrow(()->new RuntimeException("weighted picker failed")).value;
        }
        return enumConstants[random.nextInt(enumConstants.length)];
    }


    private static class WeightedEntry<T> extends WeightedPicker.Entry {

        private T value;

        public WeightedEntry(int i, T value) {
            super(i);
            this.value = value;
        }
    }

}
