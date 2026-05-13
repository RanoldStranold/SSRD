package net.ranold.mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.minecraft.client.renderer.RenderType;
import net.ranold.SSRDState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.render.sodium.SubLevelRenderSectionManager", remap = false)
public abstract class SubLevelRenderSectionManagerMixin {

    @Unique
    private Matrix4f ssd$oldProj;

    @Inject(method = "render", at = @At("HEAD"))
    private void ssd$preRender(ChunkRenderMatrices originalMatrices, RenderType layer, double camX, double camY, double camZ, CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(true);
        SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = true;


        com.mojang.blaze3d.systems.RenderSystem.setShaderFogStart(1000000.0f);
        com.mojang.blaze3d.systems.RenderSystem.setShaderFogEnd(2000000.0f);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void ssd$postRender(ChunkRenderMatrices originalMatrices, RenderType layer, double camX, double camY, double camZ, CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
    }}
