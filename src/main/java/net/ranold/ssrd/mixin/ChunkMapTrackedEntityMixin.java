package net.ranold.ssrd.mixin;

import net.ranold.ssrd.ssrd;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.ranold.ssrd.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class ChunkMapTrackedEntityMixin {

    @Shadow @Final Entity entity;
    @Shadow int range;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ssd$overrideRange(CallbackInfo ci) {
        String name = EntityType.getKey(this.entity.getType()).toString();
        boolean isContraption = name.startsWith("create:") || name.startsWith("aeronautics:") || name.startsWith("offroad:");
        if (isContraption && (name.contains("contraption") || name.contains("carriage") || name.contains("propeller"))) {
            if (ssrd$isInPlot(this.entity)) {
                // Use SSRD's configured tracking range instead of a massive hardcoded value
                int requestedRange = (int) Config.physicsTrackingRange;
                if (requestedRange > this.range) {
                    this.range = requestedRange;
                    com.mojang.logging.LogUtils.getLogger().debug("SSRD: SubLevel Contraption tracking range for {} set to {}", name, requestedRange);
                }
            }
        }
    }

    @Redirect(method = "updatePlayer", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int ssd$bypassViewDistanceClamp(int range, int viewDistanceBlocks) {
        String name = EntityType.getKey(this.entity.getType()).toString();
        boolean isContraption = name.startsWith("create:") || name.startsWith("aeronautics:") || name.startsWith("offroad:");
        if (isContraption && (name.contains("contraption") || name.contains("carriage") || name.contains("propeller"))) {
            if (ssrd$isInPlot(this.entity)) {
                // Clamp to SSRD tracking range instead of vanilla viewDistance, but don't remove clamp entirely
                int ssrdRange = (int) Config.physicsTrackingRange;
                return Math.min(range, ssrdRange);
            }
        }
        return Math.min(range, viewDistanceBlocks);
    }

    @org.spongepowered.asm.mixin.Unique
    private boolean ssrd$isInPlot(Entity entity) {
        // Direct API call instead of reflection: reflective method enumeration on Sable's
        // SubLevelContainer resolves getContainer(ClientLevel) and trips RuntimeDistCleaner
        // on dedicated servers (Issue 43 log spam), silently breaking this check.
        try {
            dev.ryanhcode.sable.api.sublevel.SubLevelContainer container =
                    dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(entity.level());
            return container != null && container.getPlot(entity.chunkPosition()) != null;
        } catch (Throwable t) {
            return false;
        }
    }
}
