/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.minecraft.util.collection.WeightedPicker;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandiUtils {


    public static <T extends WeightedItem> T getWeightedRandom(Random random, List<T> items) {
        WeightedEntry<T> e = WeightedPicker.getRandom(random,items.stream().map(i->new WeightedEntry<>(i.getWeight(),i)).collect(Collectors.toList()));
        return e.value;
    }

    public static <E extends Enum<E>> List<E> enumList(Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).collect(Collectors.toList());
    }

    public static <E extends Enum<E> & WeightedItem> E randomEnum(Random random, Class<E> enumClass) {
        return HandiUtils.getWeightedRandom(random,HandiUtils.enumList(enumClass));
    }

    private static final Pattern PLURAL_PATTERN = Pattern.compile("\\((s|es|ies)\\)");

    public static String pluralize(int count, String name) {
        if (count == 1) {
            Matcher m = PLURAL_PATTERN.matcher(name);
            if (m.find()) {
                String s = m.group(1);
                String repl = "";
                if (s.equals("ies")) {
                    repl = "y";
                }
                return replaceBetween(name,m.start(),m.end(),repl);
            }
            return name;
        } else {
            return name.replaceAll(PLURAL_PATTERN.pattern(),"$1");
        }
    }

    public static String replaceBetween(String input, int start, int end, String replacement) {
        return input.substring(0,start) + replacement + input.substring(end + 1);
    }

    private static class WeightedEntry<T> extends WeightedPicker.Entry {

        private T value;

        public WeightedEntry(int i, T value) {
            super(i);
            this.value = value;
        }
    }

}
