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

            // Safe DH sync
            int maxDistance = 4096;
            try {
                if (net.neoforged.fml.loading.LoadingModList.get().getModFileById("distanthorizons") != null) {
                    var dhConfig = com.seibel.distanthorizons.api.DhApi.Delayed.configs;
                    if (dhConfig != null) {
                        maxDistance = dhConfig.graphics().chunkRenderDistance().getValue();
                    }
                }
            } catch (Throwable ignored) {}

            final int finalMax = Math.max(32, maxDistance);
            
            // Validate current config value to prevent constructor crash
            int current = net.ranold.Config.physicsRenderDistance;
            if (current < 16) current = 16;
            if (current > finalMax) current = finalMax;
            
            OptionImpl<Void, Integer> physicsDistanceOption = OptionImpl.createBuilder(int.class, SSRD_STORAGE)
                    .setName(Component.translatable("ssrd.options.physics_render_distance.name"))
                    .setTooltip(Component.translatable("ssrd.options.physics_render_distance.tooltip"))
                    .setControl(option -> new SliderControl(option, 16, finalMax, 1, ControlValueFormatter.translateVariable("options.chunks")))
                    .setBinding((storage, value) -> {
                        Config.setPhysicsRenderDistance(value);
                        if (net.minecraft.client.Minecraft.getInstance().getConnection() != null) {
                            net.minecraft.client.Minecraft.getInstance().getConnection().send(new net.ranold.ClientConfigSyncPacket(value));
                        }
                    }, storage -> Math.min(net.ranold.Config.physicsRenderDistance, finalMax))
                    .setImpact(OptionImpact.MEDIUM)
                    .build();

            int index = Math.min(2, options.size());
            options.add(index, physicsDistanceOption);
            groups.set(0, OptionGroupAccessor.create(ImmutableList.copyOf(options)));
            
            cir.setReturnValue(new OptionPage(page.getName(), ImmutableList.copyOf(groups)));
        }
    }
}
