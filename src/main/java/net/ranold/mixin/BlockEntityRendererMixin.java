package net.ranold.mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.ranold.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {

    @Inject(method = "getViewDistance", at = @At("RETURN"), cancellable = true)
    private void ssd$overrideBlockEntityViewDistance(CallbackInfoReturnable<Integer> cir) {
        // Create sets many BE distances to 128 or 64. We want to bypass this if physics render distance is high.
        // physicsRenderDistance is in blocks (e.g. 512)
        int minDistance = Config.physicsRenderDistance;
        int current = cir.getReturnValue() != null ? cir.getReturnValue() : 64;
        
        if (minDistance > current) {
            cir.setReturnValue(minDistance);
        }
    }
}
