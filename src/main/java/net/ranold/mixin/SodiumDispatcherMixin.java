package net.ranold.mixin;

import net.minecraft.network.chat.Component;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.render.dispatcher.SodiumSubLevelRenderDispatcher", remap = false)
public class SodiumDispatcherMixin {

    static {
        com.mojang.logging.LogUtils.getLogger().info(Component.translatable("ssrd.log.sodium_dispatcher_mixin_loaded").getString());
    }

    @Inject(method = "updateCulling", at = @At("HEAD"))
    private void ssd$preCulling(CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(true);
    }

    @Inject(method = "updateCulling", at = @At("RETURN"))
    private void ssd$postCulling(CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
    }

    @Inject(method = "renderSectionLayer", at = @At("HEAD"))
    private void ssd$preRenderLayer(CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(true);
        SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = true;
    }

    @Inject(method = "renderSectionLayer", at = @At("RETURN"))
    private void ssd$postRenderLayer(CallbackInfo ci) {
        SSRDState.IS_SUBLEVEL_RENDER.set(false);
    }
}
