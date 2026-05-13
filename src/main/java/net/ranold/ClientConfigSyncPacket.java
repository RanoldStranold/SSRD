package net.ranold;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientConfigSyncPacket(int requestedRangeChunks) implements CustomPacketPayload {
    public static final Type<ClientConfigSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ssrd.MODID, "client_config_sync"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientConfigSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientConfigSyncPacket::requestedRangeChunks,
            ClientConfigSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
