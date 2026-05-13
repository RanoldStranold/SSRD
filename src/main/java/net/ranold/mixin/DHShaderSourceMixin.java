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
        if (path != null && (path.contains("apply") || path.contains("frag"))) {
            String source = cir.getReturnValue();

            if (source != null) {
                // Common replacement logic for any apply frag shader
                if (source.contains("fragColor = texture(uSourceColorTexture") && source.contains("uSourceDepthTexture")) {
                    String injected = source.replace(
                        "fragColor = texture(uSourceColorTexture, TexCoord);",
                        "gl_FragDepth = texture(uSourceDepthTexture, TexCoord).r;\n        fragColor = texture(uSourceColorTexture, TexCoord);"
                    );
                    cir.setReturnValue(injected);
                }
                else if (source.contains("fragColor = texture(gDhColorTexture") && source.contains("gDhDepthTexture")) {
                    String injected = source.replace(
                        "fragColor = texture(gDhColorTexture, TexCoord);",
                        "gl_FragDepth = texture(gDhDepthTexture, TexCoord).r;\n        fragColor = texture(gDhColorTexture, TexCoord);"
                    );
                    cir.setReturnValue(injected);
                }
            }
        }
    }
}