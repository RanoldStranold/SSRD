package net.ranold.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class AbstractContraptionEntityMixin {

    @Inject(method = "shouldRenderAtSqrDistance", at = @At("HEAD"), cancellable = true)
    private void ssd$alwaysRenderContraptionsFar(double distance, CallbackInfoReturnable<Boolean> cir) {
        String className = this.getClass().getName();
        if (className.contains("Contraption") || className.contains("Carriage")) {
            cir.setReturnValue(true);
        }
    }
}
