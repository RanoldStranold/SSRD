package net.ranold.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.ranold.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap", remap = false)
public abstract class SubLevelHoldingChunkMapMixin {

    @Shadow @Final private net.minecraft.server.level.ServerLevel level;

    @Inject(method = "updateChunkStatus", at = @At("HEAD"), cancellable = true)
    private void ssd$preventUnload(ChunkPos pos, boolean status, CallbackInfo ci) {
        if (!status) { // If unloading
            double centerX = (pos.x << 4) + 8;
            double centerZ = (pos.z << 4) + 8;

            for (var player : this.level.players()) {
                double range = net.ranold.Config.physicsTrackingRange;
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    Integer requested = net.ranold.ssrd.playerRequestedRanges.get(sp);
                    if (requested != null) {
                        range = requested * 16.0;
                    }
                }
                
                double rangeSq = range * range;
                double dx = player.getX() - centerX;
                double dz = player.getZ() - centerZ;
                if (dx * dx + dz * dz < rangeSq) {
                    com.mojang.logging.LogUtils.getLogger().debug(
                            Component.translatable("ssrd.log.prevent_sublevel_unload", pos, player.getName().getString(), range).getString()
                    );
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
