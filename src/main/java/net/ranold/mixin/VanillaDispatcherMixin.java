package net.ranold.mixin;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.ranold.SSRDState;
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
    private float ssd$savedFogStart;
    @Unique
    private float ssd$savedFogEnd;
    @Unique
    private int ssd$savedDepthFunc;

    @Inject(method = "renderSectionLayer", at = @At("HEAD"), cancellable = true)
    private void ssd$preRender(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks, CallbackInfo ci) {
        if (shader == null) return;
        
        SSRDState.IS_SUBLEVEL_RENDER.set(true);
        SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = true;
        
        this.ssd$savedFogStart = com.mojang.blaze3d.systems.RenderSystem.getShaderFogStart();
        this.ssd$savedFogEnd = com.mojang.blaze3d.systems.RenderSystem.getShaderFogEnd();
        this.ssd$savedProj.set(projection);
        this.ssd$savedDepthFunc = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC);

        float m22 = projection.m22();
        float m32 = projection.m32();
        
        // Detect Reverse-Z (Sodium/DH default)
        boolean reverseZ = Math.abs(m22) < 0.1f;

        if (reverseZ) {
            float near = m32;
            projection.m22(0.0f);
            projection.m32(near);
            com.mojang.blaze3d.systems.RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_GEQUAL);
        } else {
            float near = m32 / (m22 - 1.0f);
            projection.m22(-1.0f);
            projection.m32(-2.0f * near);
            com.mojang.blaze3d.systems.RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_LEQUAL);
        }
        
        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projection);
            shader.PROJECTION_MATRIX.upload();
        }

        com.mojang.blaze3d.systems.RenderSystem.setShaderFogStart(1000000.0f);
        com.mojang.blaze3d.systems.RenderSystem.setShaderFogEnd(2000000.0f);
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
    }

    @Inject(method = "renderSectionLayer", at = @At("RETURN"))
    private void ssd$postRender(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks, CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
        
        com.mojang.blaze3d.systems.RenderSystem.depthFunc(this.ssd$savedDepthFunc);
        
        projection.set(this.ssd$savedProj);
        if (shader != null && shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projection);
            shader.PROJECTION_MATRIX.upload();
        }
        com.mojang.blaze3d.systems.RenderSystem.setShaderFogStart(this.ssd$savedFogStart);
        com.mojang.blaze3d.systems.RenderSystem.setShaderFogEnd(this.ssd$savedFogEnd);
    }
}
