package net.ranold.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void ssd$resetFlag(CallbackInfo ci) {
        SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME = false;
        SSRDState.DONE_DH_PASS = false;
    }
}
