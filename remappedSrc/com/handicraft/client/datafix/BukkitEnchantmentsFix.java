/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

import java.util.stream.Stream;

public class BukkitEnchantmentsFix extends DataFix {
    public BukkitEnchantmentsFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> t = getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> finder = t.findField("tag");
        return fixTypeEverywhereTyped("BukkitEnchantmentsFix",t,typed -> {
            return typed.updateTyped(finder, (typedx)->{
                return typedx.update(DSL.remainderFinder(), this::fix);
            });
        });
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        Stream<Dynamic<?>> res = dynamic.get("PublicBukkitValues").get("handicraft:custom_enchs").asStreamOpt().map(s->{
            return ((Stream) s.map(d -> {
                return d.set("id", d.createString("minecraft:" + d.get("id").asString("")));
            }));
        }).result().orElse(null);
        if (res == null) return dynamic;
        dynamic = dynamic.remove("PublicBukkitValues");
        dynamic = dynamic.update("display",disp->disp.remove("Lore"));
        if (!dynamic.get("Enchantments").result().isPresent()) {
            dynamic = dynamic.set("Enchantments",dynamic.emptyList());
        }
        Dynamic<?> fixed = dynamic.update("Enchantments",enchs->{
            return res.reduce(enchs,(e,c)->e.merge(c).result().orElse(null));
        });
        System.out.println("fixed: " + fixed);
        return fixed;
    }
}
