/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge.objectives;

import com.handicraft.client.challenge.ChallengesManager;
import net.minecraft.entity.player.PlayerEntity;


import java.util.function.Predicate;

public interface ObjectiveType<I extends ObjectiveInstance> {

    default int getLevelsGained() {
        return 1;
    }

    default void trigger(PlayerEntity player, Predicate<I> predicate, int times) {
        ChallengesManager.get(player.getServer().getOverworld()).trigger(this,player,predicate,times);
    }

}
