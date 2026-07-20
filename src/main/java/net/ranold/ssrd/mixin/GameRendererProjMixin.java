package net.ranold.ssrd.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.ranold.ssrd.SSRDState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererProjMixin {

    @Inject(method = "getProjectionMatrix", at = @At("RETURN"), cancellable = true)
    private void ssd$extendFarPlane(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        Matrix4f proj = cir.getReturnValue();
        float m22 = proj.m22();
        float m32 = proj.m32();
        
        float near = 0.05f;
        float far = 200000.0f;
        
        Matrix4f largeProj = new Matrix4f(proj);
        
        // Detect if the projection is Reverse-Z (m22 is close to 0 instead of -1)
        if (Math.abs(m22) < 0.1f) {
            largeProj.m22(near / (near - far));
            largeProj.m32((far * near) / (far - near));
        } else {
            largeProj.m22(-(far + near) / (far - near));
            largeProj.m32(-(2.0f * far * near) / (far - near));
        }
        
        SSRDState.PURE_PROJ_MATRIX = new Matrix4f(largeProj);
        cir.setReturnValue(largeProj);
    }

    @Inject(method = "resetProjectionMatrix", at = @At("HEAD"))
    private void ssd$captureLevelProj(Matrix4f matrix, CallbackInfo ci) {
        // Only the level pass goes through resetProjectionMatrix with a perspective
        // matrix (m33 == 0); GUI ortho matrices go to RenderSystem directly. This
        // matrix already contains this frame's view-bob transform (issue #37).
        if (matrix.m33() == 0.0f) {
            SSRDState.LEVEL_PROJ_MATRIX = new Matrix4f(matrix);
        }
    }
}
