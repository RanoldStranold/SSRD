package net.ranold.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dev.engine_room.flywheel.backend.engine.uniform.FrameUniforms", remap = false)
public class FrameUniformsMixin {

    @Redirect(method = "writeCullData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getDepthFar()F", remap = true))
    private static float ssd$overrideFlywheelZFar(net.minecraft.client.renderer.GameRenderer instance) {
        // Force Flywheel GPU culling to use a very large zFar
        return 200000.0f;
    }
}
