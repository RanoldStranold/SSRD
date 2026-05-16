package net.ranold.mixin;

import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GlShader.class, remap = false)
public abstract class DHShaderSourceMixin {

    @Inject(method = "loadFile", at = @At("RETURN"), cancellable = true)
    private static void ssd$injectDepthOutput(String path, boolean absolute, CallbackInfoReturnable<String> cir) {
        if (path != null && path.contains("vanilla_fade")) {
            String source = cir.getReturnValue();

            if (source != null && source.contains("fragColor = mix(combinedMcDhColor, dhColor, fadeStep);")) {
                // To allow Sable SubLevels to render beyond vanilla chunks without being faded out by DH,
                // we inject custom logic to check if the vanilla fragment is significantly closer than the DH fragment.
                String injected = source.replace(
                    "fragColor = mix(combinedMcDhColor, dhColor, fadeStep);",
                    "if (mcFragmentDistance > uEndFadeBlockDistance) {\n" +
                    "            float dhDist = length(dhVertexWorldPos.xzy);\n" +
                    "            if (mcFragmentDistance < dhDist - 2.0) {\n" +
                    "                fadeStep = 0.0;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        fragColor = mix(combinedMcDhColor, dhColor, fadeStep);"
                );
                cir.setReturnValue(injected);
            }
        }
    }
}