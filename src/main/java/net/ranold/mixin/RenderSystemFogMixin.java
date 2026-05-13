package net.ranold.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RenderSystem.class, remap = false)
public abstract class RenderSystemFogMixin {

    @Inject(method = "getShaderFogStart", at = @At("HEAD"), cancellable = true)
    private static void ssd$overrideGetFogStart(CallbackInfoReturnable<Float> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(1000000.0f);
        }
    }

    @Inject(method = "getShaderFogEnd", at = @At("HEAD"), cancellable = true)
    private static void ssd$overrideGetFogEnd(CallbackInfoReturnable<Float> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(2000000.0f);
        }
    }
}
