package net.ranold.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererProjMixin {

    @Inject(method = "getProjectionMatrix", at = @At("RETURN"), cancellable = true)
    private void ssd$extendFarPlane(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        Matrix4f proj = cir.getReturnValue();
        
        // Use a very large but FINITE far plane to avoid JOML dividing by zero when normalizing the far plane.
        // Sodium uses this matrix to build its Frustum, and an infinite far plane (m22=-1.0) creates a (0,0,0) far plane normal.
        float near = 0.05f;
        float far = 200000.0f; 
        
        Matrix4f largeProj = new Matrix4f(proj);
        largeProj.m22(-(far + near) / (far - near));
        largeProj.m32(-(2.0f * far * near) / (far - near));
        
        cir.setReturnValue(largeProj);
    }
}
