package net.ranold.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.engine_room.flywheel.lib.visual.AbstractEntityVisual", remap = false)
public class AbstractEntityVisualMixin {

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void ssd$forceVisible(org.joml.FrustumIntersection frustum, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
