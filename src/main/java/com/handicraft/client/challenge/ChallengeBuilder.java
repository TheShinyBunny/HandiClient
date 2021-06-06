/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;

public class ChallengeBuilder<T extends ObjectiveInstance> {

    private int id;
    private T instance;
    private int completeCount;
    private int levels;

    public ChallengeBuilder(int id, T instance, int completeCount, int levels) {
        this.id = id;
        this.instance = instance;
        this.completeCount = completeCount;
        this.levels = levels;
    }

    public ServerChallenge<T> create() {
        return new ServerChallenge<>(id,(ObjectiveType<T>)instance.getType(),instance,completeCount);
    }
}
