/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.util.WeightedItem;

public interface ModifierWeightedItem<T extends ModifierWeightedItem<T>> extends WeightedItem {

    float getCountModifier();

    default T multiply(CountModifier modifier) {
        modifier.multiply(getCountModifier());
        return (T)this;
    }

}
