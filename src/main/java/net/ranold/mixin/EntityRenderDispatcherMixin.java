package net.ranold.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.ranold.SSRDState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void ssd$forceRenderContraptions(E entity, Frustum frustum, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (ssd$isSubLevelEntity(entity)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private <E extends Entity> void ssd$preRenderEntity(E entity, double x, double y, double z, float yRot, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (ssd$isSubLevelEntity(entity)) {
            SSRDState.IS_SUBLEVEL_RENDER.set(true);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private <E extends Entity> void ssd$postRenderEntity(E entity, double x, double y, double z, float yRot, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (ssd$isSubLevelEntity(entity)) {
            SSRDState.IS_SUBLEVEL_RENDER.set(false);
        }
    }

    private boolean ssd$isSubLevelEntity(Entity entity) {
        String typeKey = net.minecraft.world.entity.EntityType.getKey(entity.getType()).toString();
        boolean isPhysicsMod = typeKey.startsWith("create:") || typeKey.startsWith("aeronautics:") || typeKey.startsWith("offroad:") || typeKey.startsWith("vs_eureka:") || typeKey.startsWith("valkyrienskies:");
        
        String className = entity.getClass().getName().toLowerCase();
        boolean matchesKeywords = isPhysicsMod || className.contains("contraption") || className.contains("carriage") || className.contains("propeller") || className.contains("seat") || className.contains("hull") || className.contains("ship");
        
        if (matchesKeywords) {
            try {
                // Safely get HELPER via reflection to handle optional Sable
                Class<?> sableClass = Class.forName("dev.ryanhcode.sable.Sable");
                Object helper = sableClass.getField("HELPER").get(null);

                // Try getContaining first
                Object containing = helper.getClass().getMethod("getContaining", Entity.class).invoke(helper, entity); 
                if (containing != null) {
                    return true;
                }

                // Then try getTrackingSubLevel
                Object tracking = helper.getClass().getMethod("getTrackingSubLevel", Entity.class).invoke(helper, entity);
                if (tracking != null) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                // Sable not present, ignore
            } catch (Exception e) {
                //com.mojang.logging.LogUtils.getLogger().error("SSRD: Error in EntityRenderDispatcherMixin", e);
            }
        }
        return matchesKeywords;
    }
}
