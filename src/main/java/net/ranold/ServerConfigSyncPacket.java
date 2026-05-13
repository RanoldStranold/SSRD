package net.ranold;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerConfigSyncPacket(int trackingRangeChunks) implements CustomPacketPayload {
    public static final Type<ServerConfigSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ssrd.MODID, "server_config_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerConfigSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerConfigSyncPacket::trackingRangeChunks,
            ServerConfigSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
