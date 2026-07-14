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
            // Use SSRD's configured tracking range instead of a massive hardcoded value
            int requestedRange = (int) Config.physicsTrackingRange;
            if (requestedRange > this.range) {
                this.range = requestedRange;
                com.mojang.logging.LogUtils.getLogger().debug("SSRD: Contraption tracking range for {} set to {}", name, requestedRange);
            }
        }
    }

    @Redirect(method = "updatePlayer", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int ssd$bypassViewDistanceClamp(int range, int viewDistanceBlocks) {
        String name = EntityType.getKey(this.entity.getType()).toString();
        boolean isContraption = name.startsWith("create:") || name.startsWith("aeronautics:") || name.startsWith("offroad:");
        if (isContraption && (name.contains("contraption") || name.contains("carriage") || name.contains("propeller"))) {
            // Clamp to SSRD tracking range instead of vanilla viewDistance, but don't remove clamp entirely
            int ssrdRange = (int) Config.physicsTrackingRange;
            return Math.min(range, ssrdRange);
        }
        return Math.min(range, viewDistanceBlocks);
    }
}
