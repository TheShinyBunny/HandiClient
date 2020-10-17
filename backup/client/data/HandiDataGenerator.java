/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.data;

import net.minecraft.data.DataGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class HandiDataGenerator {

    public static void run() {
        DataGenerator generator = new DataGenerator(Paths.get("generated"), Collections.emptyList());
        generator.install(new LootTableData(generator));
        try {
            generator.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
