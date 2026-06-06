package net.ranold.mixin;

import dev.ryanhcode.sable.SableConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.ranold.SSRDForceloadData;
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
            Integer requestedChunks = net.ranold.ssrd.playerRequestedRanges.get(sp);
            if (requestedChunks != null) {
                // Mod detected via packet
                range = requestedChunks * 16.0;
            } else if (sp.getServer() != null && sp.getServer().isSingleplayer()) {
                // Singleplayer always has the mod
                range = net.ranold.Config.physicsTrackingRange;
            } else {
                // Fallback for multiplayer: use VRD until packet received
                range = sp.requestedViewDistance() * 16.0;
            }
        } else {
            range = net.ranold.Config.physicsTrackingRange;
        }

        // If forceloaded on server, give a 2x range bonus (up to 10k blocks)
        boolean isForceloaded = false;
        if (player instanceof ServerPlayer sp) {
            try {
                SSRDForceloadData data = net.ranold.SSRDForceloadData.get(sp.serverLevel());
                if (!data.getEntries().isEmpty()) {
                    dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer container = 
                        dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(sp.serverLevel());
                    
                    if (container != null) {
                        for (UUID uuid : data.getEntries().keySet()) {
                            Object sl = container.getSubLevel(uuid);
                            if (sl != null) {
                                Object pose = sl.getClass().getMethod("logicalPose").invoke(sl);
                                double slX, slY, slZ;
                                try {
                                    Object posObj = pose.getClass().getMethod("position").invoke(pose);
                                    slX = (double) posObj.getClass().getMethod("x").invoke(posObj);
                                    slY = (double) posObj.getClass().getMethod("y").invoke(posObj);
                                    slZ = (double) posObj.getClass().getMethod("z").invoke(posObj);
                                } catch (Exception e) {
                                    slX = (double) pose.getClass().getMethod("x").invoke(pose);
                                    slY = (double) pose.getClass().getMethod("y").invoke(pose);
                                    slZ = (double) pose.getClass().getMethod("z").invoke(pose);
                                }

                                if (Math.abs(slX - entityPosition.x()) < 0.1 && 
                                    Math.abs(slY - entityPosition.y()) < 0.1 && 
                                    Math.abs(slZ - entityPosition.z()) < 0.1) {
                                    isForceloaded = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        if (isForceloaded) {
            range = Math.max(range, 256.0); // Minimum 16 chunks for forceloaded
            range = Math.min(10000.0, range * 2.0);
        }

        boolean result = distSq < range * range;
        if (result && distSq > 320 * 320) {
            com.mojang.logging.LogUtils.getLogger().debug("SSRD: Allowing distant tracking for sub-level at {} for player {} (Range: {})", entityPosition, player.getName().getString(), range);
        }
        cir.setReturnValue(result);
    }
}
