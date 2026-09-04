package net.ranold.ssrd.mixin;

import net.ranold.ssrd.ssrd;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.ranold.ssrd.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class ChunkMapTrackedEntityMixin {

    @Shadow @Final Entity entity;
    @Mutable
    @Shadow int range;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ssd$overrideRange(CallbackInfo ci) {
        String name = EntityType.getKey(this.entity.getType()).toString();
        boolean isContraption = name.startsWith("create:") || name.startsWith("aeronautics:") || name.startsWith("offroad:");
        if (isContraption && (name.contains("contraption") || name.contains("carriage") || name.contains("propeller"))) {
            if (ssrd$isInPlot(this.entity)) {
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
                int ssrdRange = (int) Config.physicsTrackingRange;
                return Math.min(range, ssrdRange);
            }
        }
        return Math.min(range, viewDistanceBlocks);
    }

    @org.spongepowered.asm.mixin.Unique
    private boolean ssrd$isInPlot(Entity entity) {
        try {
            dev.ryanhcode.sable.api.sublevel.SubLevelContainer container =
                    dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(entity.level());
            return container != null && container.getPlot(entity.chunkPosition()) != null;
        } catch (Throwable t) {
            return false;
        }
    }
}
