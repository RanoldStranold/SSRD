package net.ranold.ssrd.mixin;
import net.ranold.ssrd.ssrd;


import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Shadow @Final private net.minecraft.server.level.ServerLevel level;

    @Inject(method = "isChunkTracked", at = @At("RETURN"), cancellable = true)
    private void ssd$forceTrackContraptionChunks(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> cir) {
        // If vanilla already tracks it, do nothing
        if (cir.getReturnValue()) return;

        // Direct API calls instead of reflection: reflective member enumeration on Sable's
        // SubLevelContainer resolves getContainer(ClientLevel) and trips RuntimeDistCleaner
        // on dedicated servers (Issue 43 log spam).
        try {
            dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer container =
                    dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(this.level);
            if (container == null) return;

            ChunkPos targetPos = new ChunkPos(x, z);

            for (dev.ryanhcode.sable.sublevel.SubLevel subLevel : container.getAllSubLevels()) {
                if (!(subLevel instanceof dev.ryanhcode.sable.sublevel.ServerSubLevel serverSubLevel)) continue;

                if (serverSubLevel.getTrackingPlayers().contains(player.getUUID())) {
                    // SSRD: Only force chunk tracking if player has the mod
                    boolean hasMod = false;
                    if (ssrd.playerRequestedRanges.containsKey(player)) {
                        hasMod = true;
                    } else if (player.getServer() != null && player.getServer().isSingleplayer()) {
                        hasMod = true;
                    }

                    if (!hasMod) {
                        continue;
                    }

                    dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot plot = serverSubLevel.getPlot();

                    for (Object cObj : plot.getContraptions()) {
                        if (cObj instanceof Entity entity) {
                            if (entity.chunkPosition().equals(targetPos)) {
                                cir.setReturnValue(true);
                                return;
                            }
                        }
                    }

                    // SPATIAL FALLBACK: If the chunk is within the sub-level's plot, track it.
                    // This ensures any entities in the plot are tracked even if not in the contraptions list.
                    if (plot.contains(targetPos)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        } catch (Throwable t) {
            // com.mojang.logging.LogUtils.getLogger().error("SSRD: Error in forceTrackContraptionChunks", t);
        }
    }
}
