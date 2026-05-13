package net.ranold.mixin;

import dev.ryanhcode.sable.SableConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem", remap = false)
public class SubLevelTrackingSystemMixin {

    @Redirect(method = "shouldLoad", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/ModConfigSpec$DoubleValue;getAsDouble()D"))
    private double ssd$overrideTrackingRange(net.neoforged.neoforge.common.ModConfigSpec.DoubleValue instance) {
        return 100000.0; // Return huge value to pass internal range checks
    }

    @Inject(method = "shouldLoad", at = @At("HEAD"), cancellable = true)
    private void ssd$checkPlayerRequestedRange(Player player, Vector3dc entityPosition, CallbackInfoReturnable<Boolean> cir) {
        double distSq = entityPosition.distanceSquared(player.getX(), player.getY(), player.getZ());
        
        double range = net.ranold.Config.physicsTrackingRange; // Server's default
        if (player instanceof ServerPlayer sp) {
            Integer requested = net.ranold.ssrd.playerRequestedRanges.get(sp);
            if (requested != null) {
                range = requested * 16.0;
            }
        }

        boolean result = distSq < range * range;
        if (result && distSq > 320 * 320) {
            com.mojang.logging.LogUtils.getLogger().debug("SSRD: Allowing distant tracking for sub-level at {} for player {} (Range: {})", entityPosition, player.getName().getString(), range);
        }
        cir.setReturnValue(result);
    }
}
