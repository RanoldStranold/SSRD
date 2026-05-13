package net.ranold;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Map;
import java.util.WeakHashMap;

@Mod(ssrd.MODID)
public class ssrd {
    public static final String MODID = "ssrd";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static int serverMaxTrackingChunks = -1;
    public static final Map<ServerPlayer, Integer> playerRequestedRanges = new WeakHashMap<>();

    public ssrd(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::registerPayloads);
        
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("SSRD: Initialized v0.2.5 (Standard Mode)");
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                ServerConfigSyncPacket.TYPE,
                ServerConfigSyncPacket.CODEC,
                (payload, context) -> {
                    serverMaxTrackingChunks = payload.trackingRangeChunks();
                }
        );
        registrar.playToServer(
                ClientConfigSyncPacket.TYPE,
                ClientConfigSyncPacket.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() instanceof ServerPlayer sp) {
                            playerRequestedRanges.put(sp, payload.requestedRangeChunks());
                        }
                    });
                }
        );
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            int chunks = (int) Math.ceil(Config.physicsTrackingRange / 16.0);
            serverPlayer.connection.send(new ServerConfigSyncPacket(chunks));
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity().getUUID().equals(net.minecraft.client.Minecraft.getInstance().player != null ? net.minecraft.client.Minecraft.getInstance().player.getUUID() : null)) {
            net.minecraft.client.Minecraft.getInstance().getConnection().send(new ClientConfigSyncPacket(Config.physicsRenderDistance));
        }
    }
}
