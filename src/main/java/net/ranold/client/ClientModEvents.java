package net.ranold.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.ranold.ssrd;

@EventBusSubscriber(modid = ssrd.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register config screen using the mod container from FML
        // NeoForge 1.21 typically uses registerExtensionPoint during mod constructor or via ModLoadingContext, 
        // but since we want to avoid loading client classes in the main class, we can use the FML ModList.
        net.neoforged.fml.ModList.get().getModContainerById(ssrd.MODID).ifPresent(container -> {
            container.registerExtensionPoint(IConfigScreenFactory.class, (c, lastScreen) -> new ConfigScreen(lastScreen));
        });
    }
}
