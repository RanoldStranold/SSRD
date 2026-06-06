package net.ranold.mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.minecraft.client.Camera;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RenderSectionManager.class, remap = true)
public abstract class RenderSectionManagerMixin {

    static {
        com.mojang.logging.LogUtils.getLogger().info("SSRD: RenderSectionManagerMixin loaded");
    }

    @Shadow(remap = false) private int lastUpdatedFrame;
    @Shadow(remap = false) private it.unimi.dsi.fastutil.longs.Long2ReferenceMap<RenderSection> sectionByPosition;

    @Inject(method = "renderLayer", at = @At("HEAD"), remap = false)
    private void ssd$preRenderLayer(ChunkRenderMatrices matrices, TerrainRenderPass pass, double x, double y, double z, CallbackInfo ci) {
        if (this.getClass().getName().toLowerCase().contains("sublevel")) {
            SSRDState.IS_SUBLEVEL_RENDER.set(true);
            SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = true;
        }
    }

    @Inject(method = "renderLayer", at = @At("RETURN"), remap = false)
    private void ssd$postRenderLayer(ChunkRenderMatrices matrices, TerrainRenderPass pass, double x, double y, double z, CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
    }

    @Inject(method = "update(Lnet/minecraft/client/Camera;Lnet/caffeinemc/mods/sodium/client/render/viewport/Viewport;Z)V", at = @At("HEAD"), remap = false)
    private void ssd$preUpdate(net.minecraft.client.Camera camera, net.caffeinemc.mods.sodium.client.render.viewport.Viewport viewport, boolean spectator, CallbackInfo ci) {
        String className = this.getClass().getName();
        if (className.toLowerCase().contains("sublevel")) {
            SSRDState.IS_SUBLEVEL_RENDER.set(true);
            SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = true;

            // Mark all sections as visible to bypass BFS issues at distance
            if (this.sectionByPosition != null) {
                for (RenderSection section : this.sectionByPosition.values()) {
                    section.setLastVisibleFrame(this.lastUpdatedFrame + 1);
                }
            }
        }
    }
    @Inject(method = "update(Lnet/minecraft/client/Camera;Lnet/caffeinemc/mods/sodium/client/render/viewport/Viewport;Z)V", at = @At("RETURN"), remap = false)
    private void ssd$postUpdate(net.minecraft.client.Camera camera, net.caffeinemc.mods.sodium.client.render.viewport.Viewport viewport, boolean spectator, CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
    }

    @Inject(method = "getSearchDistance", at = @At("HEAD"), cancellable = true, remap = false)
    private void ssd$overrideSearchDistance(CallbackInfoReturnable<Float> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue((float) net.ranold.Config.physicsRenderDistance * 16.0f);
        }
    }

    @Inject(method = "getRenderDistance", at = @At("HEAD"), cancellable = true, remap = false)
    private void ssd$overrideGetRenderDistance(CallbackInfoReturnable<Float> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue((float) net.ranold.Config.physicsRenderDistance * 16.0f);
        }
    }

    @Inject(method = "getEffectiveRenderDistance", at = @At("HEAD"), cancellable = true, remap = false)
    private void ssd$overrideGetEffectiveRenderDistance(CallbackInfoReturnable<Float> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue((float) net.ranold.Config.physicsRenderDistance * 16.0f);
        }
    }

    @Inject(method = "shouldUseOcclusionCulling", at = @At("HEAD"), cancellable = true, remap = false)
    private void ssd$overrideOcclusionCulling(Camera camera, boolean spectator, CallbackInfoReturnable<Boolean> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(false);
        }
    }
}
