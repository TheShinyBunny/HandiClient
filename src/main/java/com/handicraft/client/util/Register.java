/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.util.TriConsumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Annotating a field with this annotation will register its value to the registry.
 * This is only used by {@link com.handicraft.client.CommonMod#registerAll(Class, Class, Registry, TriConsumer, Function)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Register {

    /**
     * The {@link net.minecraft.util.Identifier} string the value should be registered as.
     */
    String value();

}
