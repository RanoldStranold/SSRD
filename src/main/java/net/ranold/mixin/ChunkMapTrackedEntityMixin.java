package net.ranold.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.ranold.Config;
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
            // Force a massive tracking range for contraptions so they are always eligible for tracking
            int requestedRange = 10000;
            if (requestedRange > this.range) {
                this.range = requestedRange;
                com.mojang.logging.LogUtils.getLogger().info("SSRD: ChunkMapTrackedEntityMixin applied! Range for {} increased to {}", name, requestedRange);
            }
        }
    }

    @Redirect(method = "updatePlayer", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int ssd$bypassViewDistanceClamp(int range, int viewDistanceBlocks) {
        String name = EntityType.getKey(this.entity.getType()).toString();
        boolean isContraption = name.startsWith("create:") || name.startsWith("aeronautics:") || name.startsWith("offroad:");
        if (isContraption && (name.contains("contraption") || name.contains("carriage") || name.contains("propeller"))) {
            return range; // Ignore the viewDistance clamp for contraptions
        }
        return Math.min(range, viewDistanceBlocks);
    }
}
