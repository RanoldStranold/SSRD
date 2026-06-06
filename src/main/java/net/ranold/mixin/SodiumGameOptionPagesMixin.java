package net.ranold.mixin;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import net.caffeinemc.mods.sodium.client.gui.options.*;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;
import net.ranold.Config;
import net.ranold.SodiumConfigStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SodiumGameOptionPages.class, remap = false)
public class SodiumGameOptionPagesMixin {
    private static final OptionStorage<Void> SSRD_STORAGE = new SodiumConfigStorage();

    @Inject(method = "general", at = @At("RETURN"), cancellable = true)
    private static void onGeneralPage(CallbackInfoReturnable<OptionPage> cir) {
        OptionPage page = cir.getReturnValue();
        List<OptionGroup> groups = new ArrayList<>(((OptionPageAccessor) page).getGroups());

        if (!groups.isEmpty()) {
            OptionGroup firstGroup = groups.get(0);
            List<Option<?>> options = new ArrayList<>(((OptionGroupAccessor) firstGroup).getOptions());

            // Safe DH / Voxy sync
            int minDistance = Config.minPhysicsRenderDistance;
            int maxDistance = Config.maxPhysicsRenderDistance;
            boolean isVoxyOnly = false;

            try {
                if (net.neoforged.fml.loading.LoadingModList.get().getModFileById("distanthorizons") != null) {
                    // Reflection for DH to avoid crash
                    Class<?> delayedClass = Class.forName("com.seibel.distanthorizons.api.DhApi$Delayed");
                    Object configs = delayedClass.getField("configs").get(null);
                    if (configs != null) {
                        Object graphics = configs.getClass().getMethod("graphics").invoke(configs);
                        Object chunkDist = graphics.getClass().getMethod("chunkRenderDistance").invoke(graphics);
                        maxDistance = (int) chunkDist.getClass().getMethod("getValue").invoke(chunkDist);
                    }
                } else if (net.neoforged.fml.loading.LoadingModList.get().getModFileById("voxy") != null) {
                    isVoxyOnly = true;
                }
            } catch (Throwable ignored) {}

            final int finalMin = minDistance;
            final int finalMax = Math.max(finalMin + 1, maxDistance);
            final boolean finalVoxyOnly = isVoxyOnly;
            
            // Validate current config value
            int current = net.ranold.Config.physicsRenderDistance;
            if (current < finalMin) current = finalMin;
            if (current > finalMax) current = finalMax;

            final int sliderMax = isVoxyOnly ? current : finalMax;
            final int sliderMin = isVoxyOnly ? current : finalMin;
            
            OptionImpl<Void, Integer> physicsDistanceOption = OptionImpl.createBuilder(int.class, SSRD_STORAGE)
                    .setName(Component.literal((isVoxyOnly ? "§7§o" : "") + Component.translatable("ssrd.options.physics_render_distance.name").getString() + (isVoxyOnly ? "§r" : "")))
                    .setTooltip(Component.translatable(isVoxyOnly ? "ssrd.options.physics_render_distance.voxy_tooltip" : "ssrd.options.physics_render_distance.tooltip"))
                    .setControl(option -> new SliderControl(option, sliderMin, Math.max(sliderMin + 1, sliderMax), 1, ControlValueFormatter.translateVariable("options.chunks")))
                    .setBinding((storage, value) -> {
                        if (finalVoxyOnly) return;
                        Config.setPhysicsRenderDistance(value);
                        if (net.minecraft.client.Minecraft.getInstance().getConnection() != null) {
                            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.ranold.ClientConfigSyncPacket(value));
                        }
                    }, storage -> Math.min(net.ranold.Config.physicsRenderDistance, finalMax))
                    .setEnabled(() -> !finalVoxyOnly)
                    .setImpact(OptionImpact.MEDIUM)
                    .build();

            int index = Math.min(2, options.size());
            options.add(index, physicsDistanceOption);
            groups.set(0, OptionGroupAccessor.create(ImmutableList.copyOf(options)));
            
            cir.setReturnValue(new OptionPage(page.getName(), ImmutableList.copyOf(groups)));
        }
    }
}
