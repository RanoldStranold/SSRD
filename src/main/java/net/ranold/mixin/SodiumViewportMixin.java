package net.ranold.mixin;

import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.viewport.Viewport", remap = false)
public class SodiumViewportMixin {

    @Inject(method = "isBoxVisible", at = @At("HEAD"), cancellable = true)
    private void ssd$bypassBoxVisibility(int intOriginX, int intOriginY, int intOriginZ, float floatSizeX, float floatSizeY, float floatSizeZ, CallbackInfoReturnable<Boolean> cir) {
        if (SSRDState.IS_SUBLEVEL_RENDER.get()) {
            cir.setReturnValue(true);
        }
    }
}
