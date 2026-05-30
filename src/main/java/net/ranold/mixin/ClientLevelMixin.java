package net.ranold.mixin;

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
        Entity entity = ((ClientLevel)(Object)this).getEntity(entityId);
        if (entity != null) {
            String name = entity.getClass().getName();
            if (name.contains("Contraption") || name.contains("Carriage")) {
                if (reason == Entity.RemovalReason.UNLOADED_TO_CHUNK || reason == Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
                    // Check if this contraption is part of an active sub-level
                    // For now, let's just always keep it if it's a contraption,
                    // as they are relatively few and the server will eventually tell us to DISCARD it if truly gone.
                    com.mojang.logging.LogUtils.getLogger().debug("SSRD: Preventing client-side UNLOAD of contraption {}. Keeping for distant render.", entityId);
                    ci.cancel();
                } else {
                    com.mojang.logging.LogUtils.getLogger().debug("SSRD: Allowing client-side REMOVAL of contraption {}. Reason: {}", entityId, reason);
                }
            }
        }
    }
}
