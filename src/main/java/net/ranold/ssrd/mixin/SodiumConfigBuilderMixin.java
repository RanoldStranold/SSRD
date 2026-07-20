package net.ranold.ssrd.mixin;

import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.ranold.ssrd.Config;
import net.ranold.ssrd.ClientConfigSyncPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SodiumConfigBuilder.class, remap = false)
public class SodiumConfigBuilderMixin {
    private static final StorageEventHandler SSRD_STORAGE = () -> {};

    private static final ThreadLocal<ConfigBuilder> CURRENT_BUILDER = new ThreadLocal<>();

    @Inject(method = "buildGeneralPage", at = @At("HEAD"))
    private void captureBuilder(ConfigBuilder builder, CallbackInfoReturnable<OptionPageBuilder> cir) {
        CURRENT_BUILDER.set(builder);
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
        method = "buildGeneralPage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/api/config/structure/OptionPageBuilder;addOptionGroup(Lnet/caffeinemc/mods/sodium/api/config/structure/OptionGroupBuilder;)Lnet/caffeinemc/mods/sodium/api/config/structure/OptionPageBuilder;",
            ordinal = 0
        )
    )
    private OptionPageBuilder injectPhysicsRenderDistance(OptionPageBuilder page, net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder group) {
        ConfigBuilder builder = CURRENT_BUILDER.get();

        final int finalMin = Config.minPhysicsRenderDistance;

        group.addOption(
            builder.createIntegerOption(ResourceLocation.parse("ssrd:general.physics_render_distance"))
                .setStorageHandler(SSRD_STORAGE)
                .setName(Component.translatable("ssrd.options.physics_render_distance.name"))
                .setTooltip(Component.translatable("ssrd.options.physics_render_distance.tooltip"))
                .setValueFormatter(ControlValueFormatterImpls.translateVariable("options.chunks"))
                // Re-queried every time the video settings screen opens (UPDATE_ON_REBUILD),
                // so the slider max follows the current DH/Voxy LOD distance without a relaunch.
                .setRangeProvider(
                    (state) -> new Range(finalMin, ssrd$queryLodMaxDistance(finalMin), 1),
                    ConfigState.UPDATE_ON_REBUILD
                )
                .setDefaultProvider(
                    (state) -> ssrd$queryLodMaxDistance(finalMin),
                    ConfigState.UPDATE_ON_REBUILD
                )
                .setBinding((value) -> {
                    Config.setPhysicsRenderDistance(value);
                    if (net.minecraft.client.Minecraft.getInstance().getConnection() != null) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new ClientConfigSyncPacket(value));
                    }
                }, () -> Math.min(Config.physicsRenderDistance, ssrd$queryLodMaxDistance(finalMin)))
                .setImpact(OptionImpact.MEDIUM)
        );

        return page.addOptionGroup(group);
    }

    private static int ssrd$queryLodMaxDistance(int minDistance) {
        int maxDistance = Config.maxPhysicsRenderDistance;

        try {
            if (net.neoforged.fml.loading.LoadingModList.get().getModFileById("distanthorizons") != null) {
                Class<?> delayedClass = Class.forName("com.seibel.distanthorizons.api.DhApi$Delayed");
                Object configs = delayedClass.getField("configs").get(null);
                if (configs != null) {
                    Object graphics = configs.getClass().getMethod("graphics").invoke(configs);
                    Object chunkDist = graphics.getClass().getMethod("chunkRenderDistance").invoke(graphics);
                    maxDistance = (int) chunkDist.getClass().getMethod("getValue").invoke(chunkDist);
                    return Math.max(minDistance + 1, maxDistance);
                }
            }
        } catch (Throwable ignored) {}

        try {
            Class<?> voxyConfigClass = Class.forName("me.cortex.voxy.client.config.VoxyConfig");
            Object voxyConfig = voxyConfigClass.getField("CONFIG").get(null);
            if (voxyConfig != null) {
                // Voxy stores distance as a float in top-level sections; its own menu
                // displays round(sectionRenderDistance * 16) * 2 as the chunk distance.
                float sectionDist = voxyConfigClass.getField("sectionRenderDistance").getFloat(voxyConfig);
                maxDistance = Math.round(sectionDist * 16.0f) * 2;
            }
        } catch (Throwable ignored) {}

        return Math.max(minDistance + 1, maxDistance);
    }
}
