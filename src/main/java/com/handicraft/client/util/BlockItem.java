/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Block field to also be registered as a standard {@link net.minecraft.item.BlockItem}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BlockItem {

    /**
     * The creative tab the item should show up in
     */
    CreativeTab value() default CreativeTab.NONE;

}
