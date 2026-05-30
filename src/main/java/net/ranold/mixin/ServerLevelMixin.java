package net.ranold.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/DistanceManager;inEntityTickingRange(J)Z"))
    private boolean ssd$forceTickContraptionChunks(DistanceManager instance, long l, Operation<Boolean> original) {
        // If vanilla says it's in range, use that
        if (original.call(instance, l)) return true;

        final ChunkPos chunkPos = new ChunkPos(l);
        final SubLevelContainer container = SubLevelContainer.getContainer((Level) (Object) this);
        if (container == null) return false;

        // If any active sub-level has a contraption in this chunk, force tick it
        for (Object slObj : container.getAllSubLevels()) {
            try {
                Object plot = slObj.getClass().getMethod("getPlot").invoke(slObj);
                java.util.Collection<?> contraptions = (java.util.Collection<?>) plot.getClass().getMethod("getContraptions").invoke(plot);
                for (Object contraptionObj : contraptions) {
                    if (contraptionObj instanceof net.minecraft.world.entity.Entity e) {
                        if (e.chunkPosition().equals(chunkPos)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        return false;
    }
}
