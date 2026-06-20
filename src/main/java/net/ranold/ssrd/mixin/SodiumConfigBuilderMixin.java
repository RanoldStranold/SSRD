package net.ranold.ssrd.mixin;

import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
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

        int minDistance = Config.minPhysicsRenderDistance;
        int maxDistance = Config.maxPhysicsRenderDistance;

        try {
            if (net.neoforged.fml.loading.LoadingModList.get().getModFileById("distanthorizons") != null) {
                Class<?> delayedClass = Class.forName("com.seibel.distanthorizons.api.DhApi$Delayed");
                Object configs = delayedClass.getField("configs").get(null);
                if (configs != null) {
                    Object graphics = configs.getClass().getMethod("graphics").invoke(configs);
                    Object chunkDist = graphics.getClass().getMethod("chunkRenderDistance").invoke(graphics);
                    maxDistance = (int) chunkDist.getClass().getMethod("getValue").invoke(chunkDist);
                }
            }
        } catch (Throwable ignored) {}

        final int finalMin = minDistance;
        final int finalMax = Math.max(finalMin + 1, maxDistance);

        int current = Config.physicsRenderDistance;
        if (current < finalMin) current = finalMin;
        if (current > finalMax) current = finalMax;

        group.addOption(
            builder.createIntegerOption(ResourceLocation.parse("ssrd:general.physics_render_distance"))
                .setStorageHandler(SSRD_STORAGE)
                .setName(Component.translatable("ssrd.options.physics_render_distance.name"))
                .setTooltip(Component.translatable("ssrd.options.physics_render_distance.tooltip"))
                .setValueFormatter(ControlValueFormatterImpls.translateVariable("options.chunks"))
                .setRange(finalMin, Math.max(finalMin + 1, finalMax), 1)
                .setDefaultValue(finalMax)
                .setBinding((value) -> {
                    Config.setPhysicsRenderDistance(value);
                    if (net.minecraft.client.Minecraft.getInstance().getConnection() != null) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new ClientConfigSyncPacket(value));
                    }
                }, () -> Math.min(Config.physicsRenderDistance, finalMax))
                .setImpact(OptionImpact.MEDIUM)
        );

        return page.addOptionGroup(group);
    }
}
