/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.datafix;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class EnchantmentFixes {

    public static void fix(CompoundTag tag) {
        if (!tag.contains("tag")) return;
        boolean isEnchBook = tag.getString("id").equalsIgnoreCase("minecraft:enchanted_book");
        CompoundTag t = tag.getCompound("tag");
        if (t.contains("PublicBukkitValues")) {
            CompoundTag bukkit = t.getCompound("PublicBukkitValues");
            if (bukkit.contains("handicraft:custom_enchs")) {
                ListTag enchs = new ListTag();
                for (Tag ench : bukkit.getList("handicraft:custom_enchs", NbtType.COMPOUND)) {
                    if (ench instanceof CompoundTag) {
                        CompoundTag ne = (CompoundTag)ench;
                        if (ne.contains("handicraft:id")) {
                            String id = ne.getString("handicraft:id");
                            int lvl = ne.getShort("handicraft:lvl");
                            ne.remove("handicraft:id");
                            ne.remove("handicraft:lvl");
                            ne.putString("id", "minecraft:" + id);
                            ne.putShort("lvl", (short) lvl);
                        }
                        enchs.add(ne);
                    }
                }
                enchs.addAll(t.getList(isEnchBook ? "StoredEnchantments" : "Enchantments", NbtType.COMPOUND));
                if (isEnchBook) {
                    t.put("StoredEnchantments",enchs);
                } else {
                    t.put("Enchantments",enchs);
                }
            }
            t.remove("PublicBukkitValues");
        }
        if (t.contains("StoredEnchantments")) {
            ListTag stored = t.getList("StoredEnchantments",NbtType.COMPOUND);
            fixDatapack(stored);
            if (!isEnchBook) {
                t.remove("StoredEnchantments");
                t.put("Enchantments",stored);
            }
        }
        if (t.contains("Enchantments")) {
            ListTag stored = t.getList("Enchantments",NbtType.COMPOUND);
            fixDatapack(stored);
            if (isEnchBook) {
                t.remove("Enchantments");
                t.put("StoredEnchantments",stored);
            }
        }
        if (t.contains("display")) {
            t.getCompound("display").remove("Lore");
            if (t.getCompound("display").isEmpty()) {
                t.remove("display");
            }
        }
    }

    private static void fixDatapack(ListTag enchs) {
        for (Tag e : enchs) {
            if (e instanceof CompoundTag) {
                CompoundTag en = (CompoundTag)e;
                String id = en.getString("id");
                if (id.equalsIgnoreCase("handicraft:heat_walker")) {
                    en.putString("id","minecraft:heat_walker");
                } else if (id.equalsIgnoreCase("handicraft:farming_feet")) {
                    en.putString("id","minecraft:farming_feet");
                }
                if (en.contains("handicraft:id")) {
                    en.putString("id","minecraft:" + en.getString("handicraft:id"));
                    en.putShort("lvl",en.getShort("handicraft:lvl"));
                }
            }
        }
    }

}
