package net.ranold.mixin;

import com.seibel.distanthorizons.common.render.openGl.postProcessing.apply.GlDhApplyShader_neoforge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GlDhApplyShader_neoforge.class, remap = false)
public abstract class DHBlazeApplyDepthFixMixin {

    @Inject(method = "renderToMcTexture", at = @At(value = "INVOKE", target = "Lcom/seibel/distanthorizons/common/render/openGl/postProcessing/GlScreenQuad;render()V"))
    private void ssd$forceDepthTestOnBlit(CallbackInfo ci) {
        // Right before drawing the quad, force depth test and depth writing on
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        com.mojang.blaze3d.systems.RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_LEQUAL);
        com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
        org.lwjgl.opengl.GL32.glDepthMask(true); // Belt and braces for OpenGL state
        org.lwjgl.opengl.GL32.glEnable(org.lwjgl.opengl.GL32.GL_DEPTH_TEST);
    }
    
    @Inject(method = "renderToFrameBuffer", at = @At(value = "INVOKE", target = "Lcom/seibel/distanthorizons/common/render/openGl/postProcessing/GlScreenQuad;render()V"))
    private void ssd$forceDepthTestOnBlitFb(CallbackInfo ci) {
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        com.mojang.blaze3d.systems.RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_LEQUAL);
        com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
        org.lwjgl.opengl.GL32.glDepthMask(true);
        org.lwjgl.opengl.GL32.glEnable(org.lwjgl.opengl.GL32.GL_DEPTH_TEST);
    }
}