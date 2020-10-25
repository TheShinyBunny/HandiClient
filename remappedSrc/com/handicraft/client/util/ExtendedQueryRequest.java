/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.util;

import com.handicraft.client.collectibles.Collectible;
import com.handicraft.client.collectibles.CollectibleType;
import com.handicraft.client.rewards.CollectibleReward;
import com.handicraft.client.rewards.Reward;

import java.util.List;
import java.util.UUID;

public interface ExtendedQueryRequest {

    UUID getUUID();

    Reward getNewClaim();

    Collectible getNewSelected();

    CollectibleType<?> getNewSelectedType();
}
