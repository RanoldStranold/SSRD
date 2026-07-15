package net.ranold.ssrd.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "removeEntity", at = @At("HEAD"), cancellable = true)
    private void ssd$preventContraptionRemoval(int entityId, Entity.RemovalReason reason, CallbackInfo ci) {
        // SSRD used to prevent contraption removal here, but that caused Issue 36 & 40 (stuck/invisible trains).
        // By allowing normal vanilla removal, the client correctly unloads distant trains
        // and safely reconstructs them when they re-enter tracking range.
    }
}
