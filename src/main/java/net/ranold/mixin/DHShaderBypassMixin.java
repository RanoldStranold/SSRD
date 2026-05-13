package net.ranold.mixin;

import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {
    "com.seibel.distanthorizons.common.render.openGl.postProcessing.fade.GlDhFarFadeShader_neoforge",
    "com.seibel.distanthorizons.common.render.openGl.postProcessing.fade.GlDhVanillaFadeShader_neoforge"
}, remap = false)
public abstract class DHShaderBypassMixin {

    static {
    }

    @Inject(method = "onApplyUniforms", at = @At("RETURN"))
    private void ssd$bypassFade(CallbackInfo ci) {
        if (SSRDState.SUBLEVELS_VISIBLE_THIS_FRAME) {
            try {
                var shader = ((DHVanillaFadeAccessor)this).getShader();
                if (shader != null) {
                    // Try all possible uniform names to be safe
                    String[] startNames = {"uStartFadeBlockDistance", "u_FadeStart", "uFadeStart"};
                    String[] endNames = {"uEndFadeBlockDistance", "u_FadeEnd", "uFadeEnd"};

                    boolean found = false;
                    for (int i = 0; i < startNames.length; i++) {
                        int uStart = shader.tryGetUniformLocation(startNames[i]);
                        int uEnd = shader.tryGetUniformLocation(endNames[i]);

                        if (uStart != -1 && uEnd != -1) {
                            shader.setUniform(uStart, 1000000.0f);
                            shader.setUniform(uEnd, 2000000.0f);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                    }
                }
            } catch (Throwable t) {
            }
        }
    }
}