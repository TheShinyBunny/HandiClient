/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.challenge;

import com.handicraft.client.challenge.objectives.ObjectiveInstance;
import com.handicraft.client.challenge.objectives.ObjectiveType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.Objects;

public abstract class Challenge<I extends ObjectiveInstance> {

    private int id;
    protected ObjectiveType<I> objective;
    protected int minCount;

    public Challenge(int id, ObjectiveType<I> objective, int minCount) {
        this.id = id;
        this.objective = objective;
        this.minCount = minCount;
    }

    public int getId() {
        return id;
    }

    public int getMinCount() {
        return minCount;
    }

    public abstract Text getText();

    public ObjectiveType<I> getObjective() {
        return objective;
    }

    public void writePacket(PacketByteBuf buf) {

    }

    @Override
    public String toString() {
        return "Challenge{" +
                "objective=" + objective +
                ", minCount=" + minCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Challenge)) return false;
        Challenge<?> challenge = (Challenge<?>) o;

        return Objects.equals(id, challenge.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    public abstract ChallengeInstance createNewInstance();
}
