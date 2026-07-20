package net.ranold.ssrd.mixin;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import net.ranold.ssrd.SSRDState;
import org.joml.Matrix4f;
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
        Matrix4f live = SSRDState.LEVEL_PROJ_MATRIX;
        if (live != null) {
            // Copy the projection captured from this frame's level pass so the depth values
            // always include the same view-bob transform as the vanilla terrain pass.
            // DhApiMat4f is row-major relative to JOML, so the copy is transposed.
            updateMatrix.m00 = live.m00(); updateMatrix.m01 = live.m10(); updateMatrix.m02 = live.m20(); updateMatrix.m03 = live.m30();
            updateMatrix.m10 = live.m01(); updateMatrix.m11 = live.m11(); updateMatrix.m12 = live.m21(); updateMatrix.m13 = live.m31();
            updateMatrix.m20 = live.m02(); updateMatrix.m21 = live.m12(); updateMatrix.m22 = live.m22(); updateMatrix.m23 = live.m32();
            updateMatrix.m30 = live.m03(); updateMatrix.m31 = live.m13(); updateMatrix.m32 = live.m23(); updateMatrix.m33 = live.m33();
        } else {
            updateMatrix.set(mcProjMat);
        }
        ci.cancel();
    }
}
