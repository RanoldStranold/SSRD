package net.ranold.ssrd.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.ranold.ssrd.ClientConfigSyncPacket;
import net.ranold.ssrd.Config;


@EventBusSubscriber(modid = "ssrd", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register config screen
        net.neoforged.fml.ModList.get().getModContainerById("ssrd").ifPresent(container -> {
            container.registerExtensionPoint(IConfigScreenFactory.class, (c, lastScreen) -> new ConfigScreen(lastScreen));
        });

        // Register forge bus events manually to hide client class references from the server scanner
        NeoForge.EVENT_BUS.addListener(ClientModEvents::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(ClientModEvents::onRegisterClientCommands);
        NeoForge.EVENT_BUS.addListener(ClientModEvents::onLoggingOut);
    }

    public static void onLoggingOut(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        // Forget the previous server's distance cap so it doesn't limit the slider in menus/other worlds
        net.ranold.ssrd.ssrd.serverMaxTrackingChunks = -1;
    }

    public static void onRegisterClientCommands(net.neoforged.neoforge.client.event.RegisterClientCommandsEvent event) {
        event.getDispatcher().register(net.minecraft.commands.Commands.literal("ssrd")
                .then(net.minecraft.commands.Commands.literal("config")
                        .executes(context -> {
                            Minecraft.getInstance().setScreen(new ConfigScreen(null));
                            return 1;
                        })
                )
        );
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity().getUUID().equals(Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getUUID() : null)) {
            if (Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().getConnection().hasChannel(ClientConfigSyncPacket.TYPE)) {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new ClientConfigSyncPacket(Config.physicsRenderDistance));
            }
        }
    }
}
