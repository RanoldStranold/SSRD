package net.ranold.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.engine_room.flywheel.impl.visual.BandedPrimeLimiter", remap = false)
public class BandedPrimeLimiterMixin {

    @Inject(method = "getUpdateDivisor", at = @At("HEAD"), cancellable = true)
    private void ssd$alwaysUpdate(double distanceSquared, CallbackInfoReturnable<Integer> cir) {
        // Force Flywheel visuals to update every tick, regardless of distance.
        // This keeps propellers spinning even at 1000+ blocks.
        cir.setReturnValue(1);
    }
}
