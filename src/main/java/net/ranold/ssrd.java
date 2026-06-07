package net.ranold;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
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
        LOGGER.info("SSRD: Initialized v{} (Standard Mode)", modContainer.getModInfo().getVersion());
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").optional();
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
                            LOGGER.info("SSRD: Received range request from player {}: {} chunks", sp.getScoreboardName(), payload.requestedRangeChunks());
                        }
                    });
                }
        );
    }

    @SubscribeEvent
    public void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        SSRDCommand.register(event.getDispatcher());
    }

    public static int getPlayerRequestedRange(ServerPlayer player) {
        Integer requested = playerRequestedRanges.get(player);
        if (requested != null) return requested;
        return (int) Math.ceil(Config.physicsTrackingRange / 16.0);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (hasMod(serverPlayer)) {
                int chunks = (int) Math.ceil(Config.physicsTrackingRange / 16.0);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, new ServerConfigSyncPacket(chunks));
            } else {
                LOGGER.info("SSRD: Client {} does not have SSRD, skipping sync.", serverPlayer.getScoreboardName());
            }
        }
    }

    public static boolean hasMod(ServerPlayer p) {
        // Check if client has the mod by looking at registered channels/payloads
        // In NeoForge 21.1, we can check the connection's capabilities
        return p.connection.hasChannel(ServerConfigSyncPacket.TYPE);
    }
}
