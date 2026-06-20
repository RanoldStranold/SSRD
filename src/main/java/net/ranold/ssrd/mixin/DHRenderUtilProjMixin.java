package net.ranold.ssrd.mixin;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.seibel.distanthorizons.core.util.RenderUtil", remap = false)
public class DHRenderUtilProjMixin {

    @Inject(method = "setDhProjectionMatrix", at = @At("HEAD"), cancellable = true)
    private static void ssd$forceMcProjMatrix(DhApiMat4f updateMatrix, DhApiMat4f mcProjMat, CallbackInfo ci) {
        // Force Distant Horizons to use the exact same projection matrix as Minecraft.
        // This ensures the depth values in DH's depth buffer mathematically match
        // the depth values in MC's depth buffer, allowing correct compositing of late-rendered
        // SubLevel objects (like entities and translucent blocks).
        updateMatrix.set(mcProjMat);
        ci.cancel();
    }
}
