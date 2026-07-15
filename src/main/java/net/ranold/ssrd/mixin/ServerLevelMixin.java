package net.ranold.ssrd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/DistanceManager;inEntityTickingRange(J)Z"))
    private boolean ssd$forceTickContraptionChunks(DistanceManager instance, long l, Operation<Boolean> original) {
        // If vanilla says it's in range, use that
        if (original.call(instance, l)) return true;

        final ChunkPos chunkPos = new ChunkPos(l);
        
        // Direct API calls instead of reflection: reflective member enumeration on Sable's
        // SubLevelContainer resolves getContainer(ClientLevel) and trips RuntimeDistCleaner
        // on dedicated servers (Issue 43 log spam).
        try {
            dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer container =
                    dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer((ServerLevel) (Object) this);
            if (container == null) return false;

            // If any active sub-level has a contraption in this chunk, force tick it
            for (dev.ryanhcode.sable.sublevel.SubLevel subLevel : container.getAllSubLevels()) {
                if (!(subLevel instanceof dev.ryanhcode.sable.sublevel.ServerSubLevel serverSubLevel)) continue;

                for (Object contraptionObj : serverSubLevel.getPlot().getContraptions()) {
                    if (contraptionObj instanceof Entity e) {
                        if (e.chunkPosition().equals(chunkPos)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // com.mojang.logging.LogUtils.getLogger().error("SSRD: Error in forceTickContraptionChunks", t);
        }

        return false;
    }
}
