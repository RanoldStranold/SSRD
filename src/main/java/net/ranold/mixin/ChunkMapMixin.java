package net.ranold.mixin;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Shadow @Final private net.minecraft.server.level.ServerLevel level;

    @Inject(method = "isChunkTracked", at = @At("RETURN"), cancellable = true)
    private void ssd$forceTrackContraptionChunks(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> cir) {
        // If vanilla already tracks it, do nothing
        if (cir.getReturnValue()) return;

        // Check if any sub-level being tracked by this player has a contraption in this chunk
        SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container == null) return;

        ChunkPos pos = new ChunkPos(x, z);
        
        for (Object slObj : container.getAllSubLevels()) {
            try {
                // Use reflection and Object type to hide SubLevelAccess dependency from compiler
                java.util.Collection<java.util.UUID> trackingPlayers = (java.util.Collection<java.util.UUID>) slObj.getClass().getMethod("getTrackingPlayers").invoke(slObj);
                if (trackingPlayers.contains(player.getUUID())) {
                    Object plot = slObj.getClass().getMethod("getPlot").invoke(slObj);
                    java.util.Collection<?> contraptions = (java.util.Collection<?>) plot.getClass().getMethod("getContraptions").invoke(plot);
                    
                    for (Object contraptionObj : contraptions) {
                        if (contraptionObj instanceof Entity contraptionEntity) {
                            if (contraptionEntity.chunkPosition().equals(pos)) {
                                cir.setReturnValue(true);
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
