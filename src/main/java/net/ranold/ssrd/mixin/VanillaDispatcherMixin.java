package net.ranold.ssrd.mixin;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.ranold.ssrd.SSRDState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher", remap = false)
public class VanillaDispatcherMixin {

    @Unique
    private final org.joml.Matrix4f ssd$savedProj = new org.joml.Matrix4f();
    @Unique
    private int ssd$savedDepthFunc;

    @Inject(method = "renderSectionLayer", at = @At("HEAD"), cancellable = true)
    private void ssd$preRender(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks, CallbackInfo ci) {
        if (shader == null) return;
        
        SSRDState.IS_SUBLEVEL_RENDER.set(true);
        SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = true;
        
        // Orthographic projection (used in GUI/Diagrams) has m33 == 1.0f. Perspective has m33 == 0.0f.
        if (Math.abs(projection.m33() - 1.0f) < 0.01f) {
            return;
        }

        this.ssd$savedProj.set(projection);
        this.ssd$savedDepthFunc = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC);

        // Since MC 1.20.5 the view-bob transform is multiplied INTO the projection matrix,
        // so the matrix we get here is (pureProjection * bob) while walking. The perspective
        // coefficients must come from the pure matrix; reading or overwriting m22/m32 of the
        // combined matrix corrupts the depth output and made sublevels see-through (issue #37).
        Matrix4f pure = SSRDState.PURE_PROJ_MATRIX;
        float m22p = pure != null ? pure.m22() : projection.m22();
        float m32p = pure != null ? pure.m32() : projection.m32();

        // Detect Reverse-Z (Sodium/DH default)
        boolean reverseZ = Math.abs(m22p) < 0.1f;

        // The old in-place rewrite (m22 = newM22, m32 = newM32) is equivalent to the row
        // operation row2' = a*row2 + b*row3 on the pure matrix. Applying the row operation
        // to the combined matrix keeps the rewrite correct when bob is baked in.
        float a;
        float b;
        if (reverseZ) {
            // Target: m22 = 0, m32 = near (infinite reverse-Z far plane)
            a = 1.0f;
            b = m22p;
            com.mojang.blaze3d.systems.RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_GEQUAL);
        } else {
            // Target: m22 = -1, m32 = -2*near (infinite conventional far plane)
            float near = m32p / (m22p - 1.0f);
            a = (-2.0f * near) / m32p;
            b = a * m22p + 1.0f;
            com.mojang.blaze3d.systems.RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_LEQUAL);
        }

        projection.m02(a * projection.m02() + b * projection.m03());
        projection.m12(a * projection.m12() + b * projection.m13());
        projection.m22(a * projection.m22() + b * projection.m23());
        projection.m32(a * projection.m32() + b * projection.m33());
        
        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projection);
            shader.PROJECTION_MATRIX.upload();
        }

        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
    }

    @Inject(method = "renderSectionLayer", at = @At("RETURN"))
    private void ssd$postRender(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks, CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
        
        if (Math.abs(projection.m33() - 1.0f) < 0.01f) {
            return;
        }
        
        com.mojang.blaze3d.systems.RenderSystem.depthFunc(this.ssd$savedDepthFunc);
        
        projection.set(this.ssd$savedProj);
        if (shader != null && shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projection);
            shader.PROJECTION_MATRIX.upload();
        }
    }
}
