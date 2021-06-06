/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.datafixer.TypeReferences;

public class BukkitXPFix extends DataFix {
    public BukkitXPFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        System.out.println("fixing xp");
        Type<?> t = getInputSchema().getType(TypeReferences.PLAYER);
        return fixTypeEverywhereTyped("BukkitXPFix",t,typed->{
            return typed.updateTyped(t.findField("BukkitValues"),u->{
                return u.update(DSL.remainderFinder(),d->{
                    OptionalDynamic<?> old = d.get("handicraft:enderchest/stored_xp");
                    return d.remove("handicraft:enderchest/stored_xp").set("storedXP",old.get().result().orElse((Dynamic)d.createInt(0)));
                });
            });
        });
    }
}
