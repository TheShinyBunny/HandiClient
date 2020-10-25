/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.client;

import com.handicraft.client.CommonMod;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class PlayedBoardHud extends DrawableHelper {

    private MinecraftClient client;
    private Map<String, Identifier> skinCache = new HashMap<>();

    public boolean render(MatrixStack matrices, ScoreboardObjective objective) {
        client = MinecraftClient.getInstance();
        int scaledHeight = client.getWindow().getScaledHeight();
        int scaledWidth = client.getWindow().getScaledWidth();
        Scoreboard scoreboard = client.world.getScoreboard();
        if (!objective.getName().toLowerCase().contains("played")) return false;

        TextRenderer textRenderer = client.textRenderer;

        List<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective).stream().filter((score) -> {
            return score.getPlayerName() != null && !score.getPlayerName().startsWith("#") && !CommonMod.ALTS.contains(score.getPlayerName());
        }).sorted(Comparator.comparingInt(ScoreboardPlayerScore::getScore).reversed()).limit(15).collect(Collectors.toList());
        Collections.reverse(scores);

        Text text = new TranslatableText("MONTHLY").formatted(Formatting.RED);
        int i = textRenderer.getWidth(text);
        int j = i;

        ScoreboardPlayerScore scoreboardPlayerScore;
        for(Iterator<ScoreboardPlayerScore> iter = scores.iterator(); iter.hasNext(); j = Math.max(j, 15 + textRenderer.getWidth(toHoursString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = iter.next();
        }

        int size = scores.size();
        int l = size * 9;
        int m = scaledHeight / 2 + l / 3;
        int o = scaledWidth - j - 1;
        int p = 0;
        int q = client.options.getTextBackgroundColor(0.3F);
        int r = client.options.getTextBackgroundColor(0.4F);

        for (ScoreboardPlayerScore score : scores) {
            ++p;
            String string = toHoursString(score.getScore());
            int t = m - p * 9;
            int u = scaledWidth - 3 + 2;
            int x = o - 2;
            fill(matrices, x, t, u, t + 9, q);
            drawPlayerSkin(matrices,score.getPlayerName(),o,t);
            textRenderer.draw(matrices, string, (float) (u - textRenderer.getWidth(string)), (float) t, -1);
            if (p == scores.size()) {
                x = o - 2;
                fill(matrices, x, t - 19, u, t - 1, r);
                fill(matrices, o - 2, t - 1, u, t, q);
                float var10003 = (float) (o + j / 2 - i / 2);
                textRenderer.draw(matrices, text, var10003, (float) (t - 18), -1);
                Text text2 = new TranslatableText("PLAYED").formatted(Formatting.RED);
                int pw = textRenderer.getWidth(text2);
                float w2 = (float) (o + j / 2 - pw / 2);
                textRenderer.draw(matrices, text2, w2, (float) (t - 9), -1);
            }
        }
        return true;
    }

    private void drawPlayerSkin(MatrixStack matrices, String playerName, int x, int y) {
        AbstractClientPlayerEntity player = this.client.world.getPlayers().stream().filter(p->p.getEntityName().equalsIgnoreCase(playerName)).findAny().orElse(null);
        boolean bl2 = player != null && player.isPartVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(playerName) || "Grumm".equals(playerName));
        Identifier texture = player == null ? getSkin(playerName) : player.getSkinTexture();
        this.client.getTextureManager().bindTexture(texture);
        int ad = 8 + (bl2 ? 8 : 0);
        int ae = 8 * (bl2 ? -1 : 1);
        DrawableHelper.drawTexture(matrices, x,y, 8, 8, 8.0F, (float)ad, 8, ae, 64, 64);
        if (player != null && player.isPartVisible(PlayerModelPart.HAT)) {
            int af = 8 + (bl2 ? 8 : 0);
            int ag = 8 * (bl2 ? -1 : 1);
            DrawableHelper.drawTexture(matrices, x, y, 8, 8, 40.0F, (float)af, 8, ag, 64, 64);
        }
    }

    private Identifier getSkin(String playerName) {
        if (skinCache.containsKey(playerName)) return skinCache.get(playerName);
        GameProfile profile = new GameProfile(null, playerName);
        profile = SkullBlockEntity.loadProperties(profile);
        skinCache.put(playerName,DefaultSkinHelper.getTexture());
        if (profile == null) {
            skinCache.put(playerName,DefaultSkinHelper.getTexture());
            return DefaultSkinHelper.getTexture();
        }
        client.getSkinProvider().loadSkin(profile,(type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                skinCache.put(playerName,identifier);
            }
        },true);
        return DefaultSkinHelper.getTexture();
    }

    private String toHoursString(int score) {
        return (score / 20 / 60 / 60) + "h";
    }

}
