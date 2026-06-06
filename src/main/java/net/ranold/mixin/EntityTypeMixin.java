package net.ranold.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.ranold.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(method = "clientTrackingRange", at = @At("RETURN"), cancellable = true)
    private void ssd$overrideContraptionTrackingRange(CallbackInfoReturnable<Integer> cir) {
        EntityType<?> type = (EntityType<?>) (Object) this;
        String name = EntityType.getKey(type).toString();
        
        // Check if it's a Create or Simulated contraption
        boolean isContraption = name.startsWith("create:") || name.startsWith("aeronautics:") || name.startsWith("offroad:") || name.startsWith("vs_eureka:") || name.startsWith("valkyrienskies:");
        if (isContraption && (name.contains("contraption") || name.contains("carriage") || name.contains("propeller") || name.contains("ship"))) {
            // Force a massive tracking range for contraptions on client too
            int newRange = 1000;
            int defaultRange = cir.getReturnValue() != null ? cir.getReturnValue() : 5;
            
            if (newRange > defaultRange) {
                com.mojang.logging.LogUtils.getLogger().debug("SSRD: Overriding tracking range for {} to {} chunks", name, newRange);
            }
            cir.setReturnValue(newRange);
        }
    }
}
