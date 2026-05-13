package net.ranold.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer", remap = false)
public interface DHVanillaFadeAccessor {
    @Accessor("shader")
    com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram getShader();
}
