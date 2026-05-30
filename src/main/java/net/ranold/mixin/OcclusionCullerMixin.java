package net.ranold.mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.minecraft.network.chat.Component;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller", remap = false)
public abstract class OcclusionCullerMixin {

    static {
        com.mojang.logging.LogUtils.getLogger().info(Component.translatable("ssrd.log.occlusion_culler_mixin_loaded").getString());
    }

    @Shadow @Final private it.unimi.dsi.fastutil.longs.Long2ReferenceMap<RenderSection> sections;

    @Inject(method = "findVisible", at = @At("HEAD"), cancellable = true)
    public void ssd$forceAllVisible(OcclusionCuller.Visitor visitor, Viewport viewport, float searchDistance, boolean useOcclusionCulling, int frame, CallbackInfo ci) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            for (RenderSection section : this.sections.values()) {
                section.setLastVisibleFrame(frame);
                visitor.visit(section);
            }
            ci.cancel();
        }
    }

    @Inject(method = "getAngleVisibilityMask", at = @At("HEAD"), cancellable = true)
    private static void ssd$bypassAngleCulling(net.caffeinemc.mods.sodium.client.render.viewport.Viewport viewport, net.caffeinemc.mods.sodium.client.render.chunk.RenderSection section, CallbackInfoReturnable<Long> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(-1L);
        }
    }

    @Inject(method = "isWithinRenderDistance", at = @At("HEAD"), cancellable = true)
    private static void ssd$overrideDistance(net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform camera, net.caffeinemc.mods.sodium.client.render.chunk.RenderSection section, float maxDistance, CallbackInfoReturnable<Boolean> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isWithinFrustum", at = @At("HEAD"), cancellable = true)
    private static void ssd$bypassFrustum(net.caffeinemc.mods.sodium.client.render.viewport.Viewport viewport, net.caffeinemc.mods.sodium.client.render.chunk.RenderSection section, CallbackInfoReturnable<Boolean> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(true);
        }
    }
}
