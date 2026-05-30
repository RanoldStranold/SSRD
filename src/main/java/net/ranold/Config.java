package net.ranold;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ssrd.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue PHYSICS_RENDER_DISTANCE = BUILDER
            .comment("The render distance for physics objects (SubLevels) in chunks.")
            .translation("ssrd.config.physics_render_distance")
            .defineInRange("physicsRenderDistance", 32, 2, 4096);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int physicsRenderDistance = 32;
    public static double physicsTrackingRange = 512.0;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        physicsRenderDistance = PHYSICS_RENDER_DISTANCE.get();
        physicsTrackingRange = (double) physicsRenderDistance * 16.0;
    }

    public static void setPhysicsRenderDistance(int value) {
        PHYSICS_RENDER_DISTANCE.set(value);
        PHYSICS_RENDER_DISTANCE.save();
        physicsRenderDistance = value;
        physicsTrackingRange = (double) value * 16.0;
        com.mojang.logging.LogUtils.getLogger().info(
                Component.translatable("ssrd.log.physics_render_distance_set", value, physicsTrackingRange).getString()
        );
    }
}
