package net.ranold.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Frustum.class, priority = 10000)
public abstract class VanillaFrustumMixin {

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void ssd$ignoreVisibleFrustum(AABB box, CallbackInfoReturnable<Boolean> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "cubeInFrustum", at = @At("HEAD"), cancellable = true)
    private void ssd$ignoreCubeFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<Boolean> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(true);
        }
    }
}
