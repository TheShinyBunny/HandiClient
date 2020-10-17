/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.WeightedItem;
import net.minecraft.text.Text;

public interface ObjectiveParameter<I> extends WeightedItem {

    float getCountModifier();

    String getName();

    boolean test(I input);

    default void modify(CountModifier modifier) {
        modifier.multiply(getCountModifier());
    }

}
