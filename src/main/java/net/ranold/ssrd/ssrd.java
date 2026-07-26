package net.ranold.ssrd;
import net.ranold.ssrd.ClientConfigSyncPacket;
import net.ranold.ssrd.Config;

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
        modEventBus.addListener(this::onCommonSetup);
        
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
                            // The singleplayer/LAN host is never capped by the gamerule
                            int cap = sp.server.isSingleplayerOwner(sp.getGameProfile())
                                    ? Integer.MAX_VALUE
                                    : getServerMaxRenderDistance(sp.server);
                            int granted = Math.min(payload.requestedRangeChunks(), cap);
                            playerRequestedRanges.put(sp, granted);
                            if (granted != payload.requestedRangeChunks()) {
                                LOGGER.info("SSRD: Received range request from player {}: {} chunks (clamped to server max {})", sp.getScoreboardName(), payload.requestedRangeChunks(), cap);
                            } else {
                                LOGGER.info("SSRD: Received range request from player {}: {} chunks", sp.getScoreboardName(), granted);
                            }
                        }
                    });
                }
        );
    }

    public static int getPlayerRequestedRange(ServerPlayer player) {
        Integer requested = playerRequestedRanges.get(player);
        if (requested != null) return requested;
        return (int) Math.ceil(Config.physicsTrackingRange / 16.0);
    }

    public static int getServerMaxRenderDistance(net.minecraft.server.MinecraftServer server) {
        if (SSRDGameRules.RULE_SSRD_MAX_RENDER_DISTANCE == null) {
            return SSRDGameRules.DEFAULT_MAX_RENDER_DISTANCE;
        }
        return server.getGameRules().getInt(SSRDGameRules.RULE_SSRD_MAX_RENDER_DISTANCE);
    }

    /** Called when the ssrdMaxRenderDistance gamerule changes: re-clamp stored requests and re-sync the cap to clients. */
    public static void onMaxRenderDistanceChanged(net.minecraft.server.MinecraftServer server, int newCap) {
        playerRequestedRanges.replaceAll((player, requested) ->
                player.server.isSingleplayerOwner(player.getGameProfile()) ? requested : Math.min(requested, newCap));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (hasMod(player)) {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new ServerConfigSyncPacket(newCap));
            }
        }
        LOGGER.info("SSRD: Max render distance gamerule changed to {} chunks; re-synced {} player(s).", newCap, server.getPlayerList().getPlayerCount());
    }

    public void onCommonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SSRDGameRules.register();
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        SSRDCommand.register(event.getDispatcher());
        // The max render distance cap never applies to the singleplayer host, so hide
        // its gamerule from /gamerule on integrated servers to avoid confusion.
        if (event.getCommandSelection() == net.minecraft.commands.Commands.CommandSelection.INTEGRATED) {
            SSRDCommand.hideMaxRenderDistanceGamerule(event.getDispatcher());
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (hasMod(serverPlayer)) {
                int chunks = getServerMaxRenderDistance(serverPlayer.server);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, new ServerConfigSyncPacket(chunks));
            } else {
                LOGGER.info("SSRD: Client {} does not have SSRD, skipping sync.", serverPlayer.getScoreboardName());
            }
        }
    }

    public static boolean hasMod(ServerPlayer p) {
        return p.connection.hasChannel(ServerConfigSyncPacket.TYPE);
    }
}
