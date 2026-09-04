package net.ranold.ssrd.mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.ranold.ssrd.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {

    @Inject(method = "getViewDistance", at = @At("RETURN"), cancellable = true)
    private void ssd$overrideBlockEntityViewDistance(CallbackInfoReturnable<Integer> cir) {
        int minDistance = (int) Config.physicsTrackingRange;
        int current = cir.getReturnValue() != null ? cir.getReturnValue() : 64;
        
        if (minDistance > current) {
            cir.setReturnValue(minDistance);
        }
    }
}
