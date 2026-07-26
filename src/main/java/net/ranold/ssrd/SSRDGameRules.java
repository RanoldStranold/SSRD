package net.ranold.ssrd;

import net.minecraft.world.level.GameRules;

public class SSRDGameRules {
    public static final int DEFAULT_FORCELOAD_LIMIT = 2;
    public static final int DEFAULT_MAX_RENDER_DISTANCE = 128;

    public static GameRules.Key<GameRules.IntegerValue> RULE_SSRD_FORCELOAD_LIMIT;
    public static GameRules.Key<GameRules.IntegerValue> RULE_SSRD_MAX_RENDER_DISTANCE;

    public static void register() {
        RULE_SSRD_FORCELOAD_LIMIT = GameRules.register("ssrdForceloadLimit", GameRules.Category.MISC, GameRules.IntegerValue.create(DEFAULT_FORCELOAD_LIMIT));
        RULE_SSRD_MAX_RENDER_DISTANCE = GameRules.register("ssrdMaxRenderDistance", GameRules.Category.MISC,
                GameRules.IntegerValue.create(DEFAULT_MAX_RENDER_DISTANCE, (server, value) -> ssrd.onMaxRenderDistanceChanged(server, value.get())));
    }
}
