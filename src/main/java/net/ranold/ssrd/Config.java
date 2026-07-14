package net.ranold.ssrd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = "ssrd", bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue PHYSICS_RENDER_DISTANCE = BUILDER
            .comment("The render distance for physics objects (SubLevels) in chunks.")
            .translation("ssrd.config.physics_render_distance")
            .defineInRange("physicsRenderDistance", 128, 1, 1000000);

    private static final ModConfigSpec.IntValue MIN_PHYSICS_RENDER_DISTANCE = BUILDER
            .comment("The minimum render distance for physics objects (SubLevels) in chunks.")
            .translation("ssrd.config.min_physics_render_distance")
            .defineInRange("minPhysicsRenderDistance", 16, 1, 1000000);

    private static final ModConfigSpec.IntValue MAX_PHYSICS_RENDER_DISTANCE = BUILDER
            .comment("The maximum render distance for physics objects (SubLevels) in chunks.")
            .translation("ssrd.config.max_physics_render_distance")
            .defineInRange("maxPhysicsRenderDistance", 4096, 1, 1000000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int physicsRenderDistance = 128;
    public static int minPhysicsRenderDistance = 16;
    public static int maxPhysicsRenderDistance = 4096;
    public static double physicsTrackingRange = 2048.0;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        physicsRenderDistance = PHYSICS_RENDER_DISTANCE.get();
        minPhysicsRenderDistance = MIN_PHYSICS_RENDER_DISTANCE.get();
        maxPhysicsRenderDistance = MAX_PHYSICS_RENDER_DISTANCE.get();
        physicsTrackingRange = (double) physicsRenderDistance * 16.0;
    }

    public static void setPhysicsRenderDistance(int value) {
        PHYSICS_RENDER_DISTANCE.set(value);
        PHYSICS_RENDER_DISTANCE.save();
        physicsRenderDistance = value;
        physicsTrackingRange = (double) value * 16.0;
        com.mojang.logging.LogUtils.getLogger().info("SSRD: Physics Render Distance set to {} (Tracking Range: {})", value, physicsTrackingRange);
    }

    public static void setMinPhysicsRenderDistance(int value) {
        MIN_PHYSICS_RENDER_DISTANCE.set(value);
        MIN_PHYSICS_RENDER_DISTANCE.save();
        minPhysicsRenderDistance = value;
    }

    public static void setMaxPhysicsRenderDistance(int value) {
        MAX_PHYSICS_RENDER_DISTANCE.set(value);
        MAX_PHYSICS_RENDER_DISTANCE.save();
        maxPhysicsRenderDistance = value;
    }
}
