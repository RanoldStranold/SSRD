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
        
        // Check if it's a Create contraption
        if (name.startsWith("create:") && (name.contains("contraption") || name.contains("carriage"))) {
            int chunks = (int) Math.ceil(Config.physicsTrackingRange / 16.0);
            int defaultRange = cir.getReturnValue() != null ? cir.getReturnValue() : 5;
            int newRange = Math.max(defaultRange, chunks);
            
            if (newRange > defaultRange) {
                com.mojang.logging.LogUtils.getLogger().debug("SSRD: Overriding tracking range for {} to {} chunks", name, newRange);
            }
            cir.setReturnValue(newRange);
        }
    }
}
