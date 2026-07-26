package net.ranold.ssrd.client;

import net.minecraft.client.Minecraft;
import net.ranold.ssrd.Config;

/**
 * Client-side helper for the effective Sub-Level Distance maximum:
 * the DH/Voxy LOD distance, capped by the server's ssrdMaxRenderDistance
 * gamerule while connected to a remote server (singleplayer/LAN hosts exempt).
 */
public final class DistanceLimits {

    public record EffectiveMax(int chunks, String source) {}

    private DistanceLimits() {}

    public static EffectiveMax query(int minDistance) {
        int maxDistance = Config.maxPhysicsRenderDistance;
        String source = "Config";

        boolean lodFound = false;
        try {
            if (net.neoforged.fml.loading.LoadingModList.get().getModFileById("distanthorizons") != null) {
                Class<?> delayedClass = Class.forName("com.seibel.distanthorizons.api.DhApi$Delayed");
                Object configs = delayedClass.getField("configs").get(null);
                if (configs != null) {
                    Object graphics = configs.getClass().getMethod("graphics").invoke(configs);
                    Object chunkDist = graphics.getClass().getMethod("chunkRenderDistance").invoke(graphics);
                    maxDistance = (int) chunkDist.getClass().getMethod("getValue").invoke(chunkDist);
                    source = "Distant Horizons";
                    lodFound = true;
                }
            }
        } catch (Throwable ignored) {}

        if (!lodFound) {
            try {
                Class<?> voxyConfigClass = Class.forName("me.cortex.voxy.client.config.VoxyConfig");
                Object voxyConfig = voxyConfigClass.getField("CONFIG").get(null);
                if (voxyConfig != null) {
                    // Voxy stores distance as a float in top-level sections; its own menu
                    // displays round(sectionRenderDistance * 16) * 2 as the chunk distance.
                    float sectionDist = voxyConfigClass.getField("sectionRenderDistance").getFloat(voxyConfig);
                    maxDistance = Math.round(sectionDist * 16.0f) * 2;
                    source = "Voxy";
                }
            } catch (Throwable ignored) {}
        }

        // While connected to a remote server that synced a distance cap, it wins if lower
        // (issue #47). Singleplayer/LAN hosts are never capped.
        Minecraft mc = Minecraft.getInstance();
        int serverCap = net.ranold.ssrd.ssrd.serverMaxTrackingChunks;
        if (serverCap > 0 && mc.getConnection() != null && !mc.hasSingleplayerServer() && serverCap < maxDistance) {
            maxDistance = serverCap;
            source = "Server";
        }

        return new EffectiveMax(Math.max(minDistance + 1, maxDistance), source);
    }
}
