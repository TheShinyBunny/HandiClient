/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

public interface CountModifier {

    static CountModifier create(int base) {
        return new CountModifier() {

            private int count = base;

            @Override
            public void multiply(float m) {
                count *= m;
            }

            @Override
            public void add(int count) {
                this.count += count;
            }

            @Override
            public int get() {
                return count;
            }
        };
    }

    void multiply(float m);

    void add(int count);

    int get();

    default <P extends ObjectiveParameter<I>,I> P modify(P param) {
        param.modify(this);
        return param;
    }

}
