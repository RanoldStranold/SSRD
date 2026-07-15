package net.ranold.ssrd.mixin;
import net.ranold.ssrd.Config;
import net.ranold.ssrd.ssrd;

import dev.ryanhcode.sable.SableConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem", remap = false)
public class SubLevelTrackingSystemMixin {

    @Inject(method = "shouldLoad", at = @At("HEAD"), cancellable = true)
    private void ssd$checkPlayerRequestedRange(Player player, Vector3dc entityPosition, CallbackInfoReturnable<Boolean> cir) {
        double distSq = entityPosition.distanceSquared(player.getX(), player.getY(), player.getZ());
        
        double range;
        if (player instanceof ServerPlayer sp) {
            Integer requestedChunks = ssrd.playerRequestedRanges.get(sp);
            if (requestedChunks != null) {
                // Mod detected via packet
                range = requestedChunks * 16.0;
            } else if (sp.getServer() != null && sp.getServer().isSingleplayer()) {
                // Singleplayer always has the mod
                range = net.ranold.ssrd.Config.physicsTrackingRange;
            } else {
                // Fallback for multiplayer: use VRD until packet received
                range = sp.requestedViewDistance() * 16.0;
            }
        } else {
            range = net.ranold.ssrd.Config.physicsTrackingRange;
        }

        boolean result = distSq < range * range;

        cir.setReturnValue(result);
    }
}
